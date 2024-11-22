package io.jadu.pages.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.jadu.pages.TodoActivity
import io.jadu.pages.ui.theme.ButtonBlue
import io.jadu.pages.ui.theme.LightGray
import io.jadu.pages.ui.theme.White
import kotlinx.coroutines.launch

@Composable
fun AddTodoBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    val bottomSheetState = androidx.compose.material.rememberModalBottomSheetState(
        ModalBottomSheetValue.Expanded,
        skipHalfExpanded = true
    )
    val scope = rememberCoroutineScope()
    var newTodoText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            scope.launch { bottomSheetState.show() }
            focusRequester.requestFocus()
        } else {
            scope.launch { bottomSheetState.hide() }
        }
    }


    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetBackgroundColor = MaterialTheme.colorScheme.onBackground,
        sheetContent = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Add TODO",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(
                        modifier = Modifier.clickable {
                            scope.launch {
                                newTodoText = ""
                                bottomSheetState.hide()
                                onDismiss()
                            }
                        },
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newTodoText,
                    onValueChange = { newTodoText = it },
                    label = { Text("TODO") },
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = LightGray,
                        focusedContainerColor = White,
                        focusedLabelColor = Color.White,
                        focusedTextColor = Color.Black,
                    ),
                )
                Spacer(modifier = Modifier.height(16.dp))
                ElevatedButton(
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = ButtonBlue,
                    ),
                    onClick = {
                        onSave(newTodoText)
                        newTodoText = ""
                        scope.launch { bottomSheetState.hide() }
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        "Save",
                        style = TextStyle(
                            color = White,
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                            fontWeight = FontWeight.Black
                        )
                    )
                }
            }
        }
    ) {
    }
}
