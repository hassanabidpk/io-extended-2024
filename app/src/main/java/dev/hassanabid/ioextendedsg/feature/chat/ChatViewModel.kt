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
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.FunctionResponsePart
import com.google.firebase.vertexai.type.InvalidStateException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class ChatViewModel(
    generativeModel: GenerativeModel
) : ViewModel() {

    private val generativeModel = generativeModel
    private val chat = generativeModel.startChat(
        history = listOf(
           /* content(role = "user") { text("Hello, I have 2 dogs in my house.") },
            content(role = "model") { text("Great to meet you. What would you like to know?") }*/
        )
    )

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
                    Log.d("ChatViewModel", "Function calling | name : ${functionCall.name} API Response: $apiResponse ")

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
}
