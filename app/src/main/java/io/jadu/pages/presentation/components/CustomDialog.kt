package io.jadu.pages.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun CustomDialog(
    title: String,
    description: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
        },
        text = {
            Text(text = description,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm) {
                Text("OK", color = MaterialTheme.colorScheme.onSurface, fontSize = MaterialTheme.typography.bodyLarge.fontSize, fontWeight = FontWeight.W600, fontFamily = MaterialTheme.typography.bodyLarge.fontFamily)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("CANCEL", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = MaterialTheme.typography.bodyLarge.fontSize, fontWeight = FontWeight.Normal, fontFamily = MaterialTheme.typography.bodyLarge.fontFamily)
            }
        }
    )
}