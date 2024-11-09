package io.jadu.pages.presentation.components

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext


@Composable
fun RequestPermissionsAndPickDocument(
    permissions: List<String>,
    onPermissionGranted: (uri: Uri?) -> Unit, // Accepts a nullable Uri
    onPermissionDenied: (deniedPermissions: List<String>) -> Unit,
    context: Context,
) {

    val openDocumentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        onPermissionGranted(uri)
    }

    val requestPermissions = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        val deniedPermissions = results.filter { !it.value }
        if (deniedPermissions.isNotEmpty()) {
            Log.d("RequestPermissionsAndPickDocument", "Permissions denied")
            val deniedPermissionsNames = deniedPermissions.keys.joinToString(", ")
            Toast.makeText(
                context,
                "Please grant permissions to access media files.",
                Toast.LENGTH_LONG
            ).show()
            onPermissionDenied(deniedPermissions.keys.toList())
        } else {
            Log.d("RequestPermissionsAndPickDocument", "Permissions granted")
            openDocumentLauncher.launch(arrayOf("image/*")) // Launch the document picker
        }
    }

    LaunchedEffect(permissions) {
        Log.d("RequestPermissionsAndPickDocument", "Requesting permissions")
        requestPermissions.launch(permissions.toTypedArray())
    }
}
