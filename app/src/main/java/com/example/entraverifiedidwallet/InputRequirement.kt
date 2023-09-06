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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputRequirement(onNavigate: () -> Unit, entraClient: EntraClientViewModel) {
    var pin by remember {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.padding(
            horizontal = 16.dp, vertical = 16.dp
        )
    ) {
        TextField(value = pin, onValueChange = { pin = it })
        Button(
            onClick = {
                Log.d("fuga", pin)
                scope.launch {
                    entraClient.pinInput(pin).fold(
                        onSuccess = { Log.d("PIN", "Success") },
                        onFailure = { Log.d("PIN", "Error") }
                    )
                }
            },
            interactionSource = remember { MutableInteractionSource() },
        ) {
            Text(text = "PIN INPUT")
        }
    }
}