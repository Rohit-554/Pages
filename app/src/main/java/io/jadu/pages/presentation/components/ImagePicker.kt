package io.jadu.pages.presentation.components

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import io.jadu.pages.core.noRippleClickable
import java.io.File


@Composable
fun ImagePickerDialog(onImagePicked: (Uri?) -> Unit) {
    val context = LocalContext.current

    // Create a URI for the camera image
    val cameraImageUri = remember {
        createImageUri(context)
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            onImagePicked(cameraImageUri)
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImagePicked(uri)
    }

    Dialog(onDismissRequest = { onImagePicked(null) }) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceBright,
            tonalElevation = 4.dp
        ) {
            Column {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Row with Camera and Gallery options
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Camera option
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .border(
                                    2.dp,
                                    MaterialTheme.colorScheme.outlineVariant,
                                    shape = CircleShape
                                )
                                .padding(12.dp)
                                .noRippleClickable {
                                    if (cameraImageUri != null) {
                                        cameraLauncher.launch(cameraImageUri)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera Icon",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Spacer to add some space between the icons
                        Spacer(modifier = Modifier.size(32.dp))

                        // Gallery option
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .border(
                                    2.dp,
                                    MaterialTheme.colorScheme.outlineVariant,
                                    shape = CircleShape
                                )
                                .padding(12.dp)
                                .noRippleClickable {
                                    galleryLauncher.launch("image/*")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Gallery Icon",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    HorizontalDivider()
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Note : Donot Upload any sensitive information",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    )


                }
            }
        }
    }
}



fun createImageUri(context: Context): Uri? {
    val tempFile = File.createTempFile(
        "temp_image_${System.currentTimeMillis()}",
        ".jpg",
        context.cacheDir
    )

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider", // Authority defined in your manifest
        tempFile
    )
}

