package com.example.entraverifiedidwallet

import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestVCUrl(
    onNavigate: () -> Unit,
    entraClient: EntraClientViewModel,
) {
    var url by remember {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.padding(
            horizontal = 16.dp, vertical = 16.dp
        )
    ) {
        TextField(value = url, onValueChange = { url = it })
        Button(
            onClick = {
                Log.d("hoge", url)
                scope.launch {
                    val result = entraClient.createRequest(url)
                    result.fold(
                        onSuccess = {
                            onNavigate.invoke()
                        },
                        onFailure = {
                            onNavigate.invoke()
                        }
                    )
                }
            },
            interactionSource = remember { MutableInteractionSource() },
        ) {
            Text(text = "Request")
        }
    }
}
