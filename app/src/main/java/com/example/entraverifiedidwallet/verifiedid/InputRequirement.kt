package com.example.entraverifiedidwallet.verifiedid

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
import com.example.entraverifiedidwallet.ui.component.SimpleAlertDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputRequirement(onNavigate: () -> Unit, entraClient: EntraClientViewModel) {
    var pin by remember {
        mutableStateOf("")
    }
    val scope = rememberCoroutineScope()

    var showDialog by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.padding(
            horizontal = 16.dp, vertical = 16.dp
        )
    ) {
        TextField(value = pin, onValueChange = { pin = it })
        Button(
            onClick = {
                scope.launch {
                    entraClient.pinInput(pin).fold(
                        onSuccess = { Log.d("PIN", "Success") },
                        onFailure = {
                            Log.d("PIN", "Error")
                            showDialog = true
                        }
                    )
                }
            },
            interactionSource = remember { MutableInteractionSource() },
        ) {
            Text(text = "PIN INPUT")
        }
    }
    when {
        showDialog -> {
            SimpleAlertDialog(
                title = "失敗",
                message = "不正なPINです",
                onDismiss = {
                    showDialog = false
                },
                confirmButton = {
                    showDialog = false
                })
        }
    }
}