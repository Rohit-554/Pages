package io.jadu.pages.presentation.components

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.jadu.pages.BuildConfig
import papaya.`in`.sendmail.SendMail




@Composable
fun TextFieldDialogue(
    onDismissRequest: () -> Unit,
    onSubmit: (String) -> Unit,
    isFeedbackClicked:Boolean = false
) {
    var bugDescription by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    val email by remember { mutableStateOf(BuildConfig.EMAIL) }
    val context = androidx.compose.ui.platform.LocalContext.current
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceContainer,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if(!isFeedbackClicked){
                    Row {
                        Text(
                            text = "Report a Bug",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 20.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.BugReport,
                            contentDescription = "Bug Report",
                        )
                    }
                }else{
                    Row {
                        Text(
                            text = "Feedback",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 20.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.EmojiEmotions,
                            contentDescription = "Bug Report",
                        )
                    }
                }



                //createa a OutlinedTextField for the title
                CustomTextField(
                    title, onValueChange = { title = it },
                    modifier = Modifier,
                    "Title"
                )
                CustomTextField(
                    bugDescription,
                    onValueChange = { bugDescription = it },
                    modifier = Modifier.height(150.dp),
                    "Description"
                )

                // Submit Button
                Button(
                    onClick = {
                        if(title.isNotEmpty()){
                            sendEmail(email, title.trim(), bugDescription.trim(), context)
                            onDismissRequest()
                        }else {
                            Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Submit")
                }
            }
        }
    }
}

fun sendEmail(email: String, title: String, bugDescription: String, context: Context) {
    try {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        intent.putExtra(Intent.EXTRA_SUBJECT, title)
        intent.putExtra(Intent.EXTRA_TEXT, bugDescription)
        intent.type = "message/rfc822"
        intent.setPackage("com.google.android.gm")

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            context.startActivity(
                Intent.createChooser(
                    intent,
                    "Choose an email client"
                )
            )
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Error sending email", Toast.LENGTH_SHORT).show()
        Log.e("ErrorEmail", e.message.toString())
    }
}


@Composable
private fun CustomTextField(
    title: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String
) {
    OutlinedTextField(
        value = title,
        onValueChange = {
            onValueChange(it)
        },
        label = {
            Text(
                label,
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.onSurface,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.onSurface,
            selectionColors = TextSelectionColors(
                handleColor = MaterialTheme.colorScheme.onSurface,
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ),
        placeholder = { Text("Type the $title here...") },
        modifier = modifier.fillMaxWidth(),
        textStyle = TextStyle(
            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp
        ),
        singleLine = false,
    )
}