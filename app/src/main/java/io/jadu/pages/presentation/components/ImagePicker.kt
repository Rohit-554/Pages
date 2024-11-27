package io.jadu.pages.presentation.components

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle

@Composable
fun ImagePickerDialog(onImagePicked: (Uri?) -> Unit) {
    val context = LocalContext.current

    // Temporary URI for camera image
    val cameraImageUri = remember {
        createImageUri(context)
    }

    // Launcher for camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            onImagePicked(cameraImageUri)
        }
    }

    // Launcher for gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImagePicked(uri)
    }

    // Show a dialog with options
    AlertDialog(
        onDismissRequest = {},
        title = { Text("Choose Image") },
        text = { Text("Select an image from the gallery or take a new photo.") },
        confirmButton = {
            TextButton(onClick = {
                cameraImageUri?.let { cameraLauncher.launch(it) }
            }) {
                Text(
                    "Take Photo",
                    style = TextStyle(
                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = {
                galleryLauncher.launch("image/*")
            }) {
                Text("Choose from Gallery")
            }
        }
    )
}

fun createImageUri(context: Context): Uri? {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "new_image_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    return context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    )
}
