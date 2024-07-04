/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.hassanabid.ioextendedsg.feature.chat

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.FunctionResponsePart
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.defineFunction
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.vertexai.type.InvalidStateException
import com.hassan.myaiapplication.ApiInterface
import com.hassan.myaiapplication.Order
import com.hassan.myaiapplication.RetrofitClient
import dev.hassanabid.ioextendedsg.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class ChatViewModel(
    generativeModel1: GenerativeModel
) : ViewModel() {

    val config = generationConfig {
        temperature = 0.7f
    }

    val date = LocalDate.now()
    val dow = date.dayOfWeek
    val dayName = dow.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val systemInstructions = "You are a food & beverages delivery app chatbot," +
            "who orders Coffee or Tea based on my recent orders and preferences." +
            "Ask clarifying questions if not enough information is available to complete the request." +
            "Today's date and day of the week for reference : $date and $dayName" +
            "My Current Location/Address : Posts and Telecommunications Institute of Technology, " +
            "96A, Tran Phu Street, Ha Dong District, Hanoi. "

    val placeOrderFunction = defineFunction(
        "placeOrder",
        "Place order of coffee or Tea with snacks if any",
        Schema.str("orderSummary", "Type of coffee or tea to order. Also add any additional food item"),
        Schema.str("address", "address to deliver food items")
    ) { order, address ->
        placeOrderApiRequest(order, address)
    }

    val pastOrdersFunction = defineFunction(
        name = "getPastOrders",
        description = "Get list of past orders of drinks or snacks day-wise. Include preferences if any",
        Schema.str("queryString", "Get past orders instructions"),
        Schema.str("location", "Current address or city")
    ) { queryString,  currentLocation ->
        // Call the api function that you declared above
        runBlocking {
            val jsonObject = pastOrdersApiRequest(currentLocation)
            return@runBlocking jsonObject
        }

    }

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey,
        generationConfig = config,
        systemInstruction = content { text(systemInstructions)},
        tools = listOf(Tool(listOf(pastOrdersFunction, placeOrderFunction)))
    )

    private val chat = generativeModel.startChat(
        history = listOf(
           /* content(role = "user") { text("Hello, I have 2 dogs in my house.") },
            content(role = "model") { text("Great to meet you. What would you like to know?") }*/
        )
    )

    suspend fun pastOrdersApiRequest(
        currentLocation: String
    ): JSONObject {
        // This hypothetical API returns a JSON such as:
        // {"data":"get past orders list","today":"saturday"}
        var jsonObject:JSONObject
        runBlocking {
            val pastOrders = getPastOrdersList()
            jsonObject = JSONObject().apply {
                put("data", pastOrders)
                put("today", "$dayName")
            }
        }
        return jsonObject
    }

    suspend fun placeOrderApiRequest(
        order: String,
        address : String
    ): JSONObject {
        // This hypothetical API returns a JSON such as:
        return JSONObject().apply {
            put("response", "Order placed for $order to be delivered to $address")
        }
    }


    private val _uiState: MutableStateFlow<ChatUiState> =
        MutableStateFlow(ChatUiState(chat.history.map { content ->
            // Map the initial messages
            ChatMessage(
                text = content.parts.first().asTextOrNull() ?: "",
                participant = if (content.role == "user") Participant.USER else Participant.MODEL,
                isPending = false
            )
        }))
    val uiState: StateFlow<ChatUiState> =
        _uiState.asStateFlow()


    fun sendMessage(userMessage: String) {
        // Add a pending message
        _uiState.value.addMessage(
            ChatMessage(
                text = userMessage,
                participant = Participant.USER,
                isPending = true
            )
        )
        viewModelScope.launch {
            try {
                var response = chat.sendMessage(userMessage)
                // Check if the model responded with a function call
                response.functionCalls.firstOrNull()?.let { functionCall ->
                    // Try to retrieve the stored lambda from the model's tools and
                    // throw an exception if the returned function was not declared
                    val matchedFunction = generativeModel.tools?.flatMap { it.functionDeclarations }
                        ?.first { it.name == functionCall.name }
                        ?: throw InvalidStateException("Function not found: ${functionCall.name}")

                    // Call the lambda retrieved above
                    val apiResponse: JSONObject = matchedFunction.execute(functionCall)
                    Log.d("ChatViewModel", "Function calling | name : ${functionCall.name} args: ${functionCall.args} " +
                            "API Response: $apiResponse ")

                    // Send the API response back to the generative model
                    // so that it generates a text response that can be displayed to the user
                    response = chat.sendMessage(
                        content(role = "function") {
                            part(FunctionResponsePart(functionCall.name, apiResponse))
                        }
                    )
                }

                _uiState.value.replaceLastPendingMessage()

                response.text?.let { modelResponse ->
                    _uiState.value.addMessage(
                        ChatMessage(
                            text = modelResponse,
                            participant = Participant.MODEL,
                            isPending = false
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.value.replaceLastPendingMessage()
                _uiState.value.addMessage(
                    ChatMessage(
                        text = e.localizedMessage,
                        participant = Participant.ERROR
                    )
                )
            }
        }
    }

    suspend fun getPastOrdersList(): List<Order> {
        var retrofit = RetrofitClient.getInstance()
        var apiInterface = retrofit.create(ApiInterface::class.java)
        var ordersList: List<Order> =  emptyList()
        Log.d("ChatViewModel", "getPastOrdersList called")
        /*viewModelScope.launch(Dispatchers.IO)*/
        runBlocking {
            Log.e("getPastOrdersList", "Start api call")
            try {
                val response = apiInterface.getPastOrders()
                if (response.isSuccessful) {
                    //your code for handling success response
                    Log.d("ChatViewModel", response.body()?.data.toString())
                    ordersList = response.body()?.data!!

                } else {
                    /*                    Toast.makeText(
                                            context,
                                            response.message(),
                                            Toast.LENGTH_LONG
                                        ).show()*/
                }
            } catch (Ex:Exception){
                Log.e("Error", Ex.localizedMessage.toString())
            }
        }

        return ordersList

    }

}
