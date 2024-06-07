package dev.hassanabid.ioextendedsg

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.Firebase
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BakingViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
//        modelName = "gemini-pro-vision",
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    val configWithVertexAI = com.google.firebase.vertexai.type.generationConfig {
        temperature = 0.7f
    }
    private val generativeModelVertexAI = Firebase.vertexAI.generativeModel(
        modelName = "gemini-1.5-flash",
        generationConfig = configWithVertexAI
    )


    fun sendPrompt(
        bitmap: Bitmap,
        prompt: String
    ) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        image(bitmap)
                        text(prompt)
                    }
                )
/*                val responseWithVertexAI = generativeModelVertexAI.generateContent(
                    com.google.firebase.vertexai.type.content {
                        image(bitmap)
                        text(prompt)
                    }
                )*/
                response.text?.let { outputContent ->
                    _uiState.value = UiState.Success(outputContent)
                }

/*                responseWithVertexAI?.text?.let { outputContent ->
                    _uiState.value = UiState.Success(outputContent)
                }*/
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }
}