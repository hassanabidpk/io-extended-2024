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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.defineFunction
import com.google.ai.client.generativeai.type.generationConfig
import dev.hassanabid.ioextendedsg.BuildConfig
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale


val GenerativeViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        viewModelClass: Class<T>,
        extras: CreationExtras
    ): T {
        val config = generationConfig {
            temperature = 0.7f
        }
        val date = LocalDate.now()
        val dow = date.dayOfWeek
        val dayName = dow.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        /*val today = DayOfWeek.of(dow).toString()*/
        Log.d("ViewModelFactory","date : $date day : $dayName")

        val systemInstructions = "You are a food & beverages delivery app, who orders Coffee or Tea based on my " +
                "Today : $date and $dayName" +
                "Location/Address : Posts and Telecommunications Institute of Technology, " +
                "96A, Tran Phu Street, Ha Dong District, Hanoi. "

        val pastOrderDummyData = "preferences and past orders. " +
                "Monday : Milk tea with muffin, Tuesday : Black Coffee " +
                "with cupcake, " +
                "Wednesday : Latte, Thursday : Bubble tea with 25% sugar, " +
                "Friday : Black coffee with croissant, " +
                "Saturday: Vietnamese coffee with Banh mi" +
                "Preferences : 1. decaf on Monday 2. Normal on other days "

        suspend fun makePastOrdersApiRequest(
            currentLocation: String
        ): JSONObject {
            // This hypothetical API returns a JSON such as:
            // {"foodItems":"milk tea","whichDay":"muffin"}
            return JSONObject().apply {
                put("foodItems", pastOrderDummyData)
                put("whichDay", "$dayName")
            }
        }

        suspend fun makePlaceOrderApiRequest(
            order: String,
            address : String
        ): JSONObject {
            // This hypothetical API returns a JSON such as:
            // {"foodItems":"milk tea","whichDay":"muffin"}
            return JSONObject().apply {
                put("response", "Order placed for $order to be delivered to $address")
            }
        }

        val placeOrderFunction = defineFunction(
            "placeOrder",
            "Place order of coffee or Tea",
            Schema.str("orderSummary", "Type of coffee or tea to order. Also add any additional food item"),
            Schema.str("address", "address to deliver food items")
        ) { order, address ->
            makePlaceOrderApiRequest(order, address)
        }

        val pastOrdersFunction = defineFunction(
            name = "getPastOrders",
            description = "Get list of past orders of drinks or snacks day-wise. Include preferences if any",
            Schema.str("location", "Current address or city")
        ) { currentLocation ->
            // Call the api function that you declared above
            makePastOrdersApiRequest(currentLocation)
        }

        return with(viewModelClass) {
            when {
                isAssignableFrom(ChatViewModel::class.java) -> {
                    // Initialize a GenerativeModel with the `gemini-flash` AI model for chat
                    val generativeModel = GenerativeModel(
                        modelName = "gemini-1.5-flash",
                        apiKey = BuildConfig.apiKey,
                        generationConfig = config,
                        systemInstruction = content { text(systemInstructions)},
                        tools = listOf(Tool(listOf(placeOrderFunction, pastOrdersFunction)))
                    )
                    ChatViewModel(generativeModel)
                }

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${viewModelClass.name}")
            }

        } as T
    }
}
