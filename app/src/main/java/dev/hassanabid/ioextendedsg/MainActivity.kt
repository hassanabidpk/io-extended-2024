package dev.hassanabid.ioextendedsg

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.Firebase
import com.google.firebase.vertexai.vertexAI
import dev.hassanabid.ioextendedsg.ui.theme.IOExtendedSGTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IOExtendedSGTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    BakingScreen()
                }
            }
        }

        // Initialize the Vertex AI service and the generative model
        // Specify a model that supports your use case
        // Gemini 1.5 models are versatile and can be used with all API capabilities
        val generativeModel = Firebase.vertexAI.generativeModel("gemini-1.5-flash")
        CoroutineScope(Dispatchers.Main).launch {
            callVertexAI()
        }
    }

    suspend fun callVertexAI() {
        // Initialize the Vertex AI service and the generative model
        // Specify a model that supports your use case
        // Gemini 1.5 models are versatile and can be used with all API capabilities
        val generativeModel = Firebase.vertexAI.generativeModel("gemini-1.5-flash")

        // Provide a prompt that contains text
        val prompt = "Write a story about a magic backpack."

        // To generate text output, call generateContent with the text input
        val response = generativeModel.generateContent(prompt)
        print(response.text)
        Log.d("MainActivity", response.text!!)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    BakingScreen()
}