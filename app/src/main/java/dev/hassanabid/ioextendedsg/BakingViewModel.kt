package dev.hassanabid.ioextendedsg

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.vertexai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.Firebase
import com.google.firebase.vertexai.type.FunctionResponsePart
import com.google.firebase.vertexai.type.InvalidStateException
import com.google.firebase.vertexai.type.Schema
import com.google.firebase.vertexai.type.Tool
import com.google.firebase.vertexai.type.defineFunction
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class BakingViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    suspend fun makeApiRequest(
        currencyFrom: String,
        currencyTo: String
    ): JSONObject {
        // This hypothetical API returns a JSON such as:
        // {"base":"SGD","rates":{"MYR": 3.47}}
        return JSONObject().apply {
            put("base", currencyFrom)
            put("rates", hashMapOf(currencyTo to 3.47))
        }
    }

    val upperCaseFunction = defineFunction(
        "upperCase",
        "Returns the upper case version of the input string",
        Schema.str("input", "Text to transform")
    ) { input ->
        JSONObject("{\"response\": \"${input.uppercase()}\"}")
    }

    val getExchangeRate = defineFunction(
        name = "getExchangeRate",
        description = "Get the exchange rate for currencies between countries",
        Schema.str("currencyFrom", "The currency to convert from."),
        Schema.str("currencyTo", "The currency to convert to.")
    ) { from, to ->
        // Call the function that you declared above
        makeApiRequest(from, to)
    }

    val configWithVertexAI = com.google.firebase.vertexai.type.generationConfig {
        temperature = 0.7f
    }
    private val generativeModelVertexAI = Firebase.vertexAI.generativeModel(
        modelName = "gemini-1.5-flash-preview-0514",
        generationConfig = configWithVertexAI,
        // Specify the function declaration.
        tools = listOf(Tool(listOf(getExchangeRate, upperCaseFunction)))
    )

    fun sendPrompt(
        prompt: String
    ) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {

                val chat = generativeModelVertexAI.startChat()
                var chatResponse = chat.sendMessage(prompt)
                /*
                val responseWithVertexAI = generativeModelVertexAI.generateContent(
                    com.google.firebase.vertexai.type.content {
                        text(prompt)
                    }
                )*/
                // Check if the model responded with a function call
                chatResponse.functionCalls.firstOrNull()?.let { functionCall ->
                    // Try to retrieve the stored lambda from the model's tools and
                    // throw an exception if the returned function was not declared
                    val matchedFunction = generativeModelVertexAI.tools?.flatMap { it.functionDeclarations }
                        ?.first { it.name == functionCall.name }
                        ?: throw InvalidStateException("Function not found: ${functionCall.name}")

                    // Call the lambda retrieved above
                    val apiResponse: JSONObject = matchedFunction.execute(functionCall)
                    Log.d("BakingViewModel", "Function calling API Response: $apiResponse ${functionCall.name}")

                    // Send the API response back to the generative model
                    // so that it generates a text response that can be displayed to the user
                    chatResponse = chat.sendMessage(
                        content(role = "function") {
                            part(FunctionResponsePart(functionCall.name, apiResponse))
                        }
                    )
                }

                chatResponse?.text?.let { outputContent ->
                    _uiState.value = UiState.Success(outputContent)
                }

            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }

    // Function calling
/*    API Request
    {
        "currencyFrom": "USD",
        "currencyTo": "SEK"
    }
    API Response
    {
        "base": "USD",
        "rates": {"SEK": 10.99}
    }*/



}