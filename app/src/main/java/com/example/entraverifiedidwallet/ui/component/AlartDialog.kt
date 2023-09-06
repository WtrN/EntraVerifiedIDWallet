package com.example.entraverifiedidwallet.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun SimpleAlertDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    confirmButton: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(text = title)
        },
        text = {
            Text(text = message)
        },
        onDismissRequest =
        { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = {
                    confirmButton()
                },
            ) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                },
            ) {
                Text(text = "Cancel")
            }
        }
    )
}