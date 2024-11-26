package io.jadu.pages.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.jadu.pages.ui.theme.White

@Composable
fun CustomInputFields(
    text: String,
    onTitleChange: (String) -> Unit,
    textStyle: TextStyle = TextStyle(
        color = White,
        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold
    ),
    hintText: String = "Your Title...",
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.outline,
        backgroundColor = MaterialTheme.colorScheme.outline
    )
    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        BasicTextField(
            value = text,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            onValueChange = onTitleChange,
            modifier = modifier,
            singleLine = singleLine,
            textStyle = textStyle,
            decorationBox = { innerTextField ->
                if (text.isEmpty()) {
                    Text(
                        text = hintText,
                        color = Color.White.copy(alpha = 0.7f),
                        style = textStyle
                    )
                }
                innerTextField()
            }
        )
    }
}