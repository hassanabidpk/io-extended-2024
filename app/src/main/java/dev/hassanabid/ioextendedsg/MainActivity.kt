package dev.hassanabid.ioextendedsg

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.vertexai.vertexAI
import dev.hassanabid.ioextendedsg.feature.chat.ChatScreen
import dev.hassanabid.ioextendedsg.ui.theme.IOExtendedSGTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IOExtendedSGTheme {
                Scaffold(
                    topBar = { AppBar() }
                ) { innerPadding ->
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        ChatScreen(
                            onPhotoPickerClick = {}
                        )
                    }
                }
            }
        }

    }
    // TopAppBar is marked as experimental in Material 3
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppBar() {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            TopAppBar(
                title = { Text(stringResource(R.string.activity_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
            )
            Box(
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Text(
                    text = stringResource(R.string.activity_subtitle),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    ChatScreen(onPhotoPickerClick = {})
}