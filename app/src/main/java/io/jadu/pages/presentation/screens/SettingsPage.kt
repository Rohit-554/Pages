package io.jadu.pages.presentation.screens

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.PixelCopy.Request
import android.widget.Space
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import io.jadu.pages.R
import io.jadu.pages.core.Constants
import io.jadu.pages.core.PreferencesManager
import io.jadu.pages.presentation.components.CustomTopAppBar
import io.jadu.pages.presentation.components.InfoCard
import io.jadu.pages.presentation.components.RequestPermissionsAndPickDocument
import io.jadu.pages.presentation.navigation.NavigationItem
import io.jadu.pages.ui.theme.PagesTheme
import io.jadu.pages.ui.theme.TickColor
import io.jadu.pages.ui.theme.White


@Composable
fun SettingsPage(navHostController: NavHostController) {
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp
    val cardHeightDp = screenHeightDp * 0.15f
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    var name by remember { mutableStateOf(preferencesManager.getName() ?: "Creator") }
    val image by remember { mutableStateOf(preferencesManager.getString(Constants.GET_IMAGE) ?: "") }
    var previousName by remember { mutableStateOf(name) }
    var isEditing by remember { mutableStateOf(false) }
    var toastShown by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploadPhotoClicked by remember { mutableStateOf(false) }

    LaunchedEffect(isEditing) {
        if (isEditing) {
            focusRequester.requestFocus()
        }
    }
    val openDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            val contentResolver = context.contentResolver
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            selectedImageUri = uri
            preferencesManager.putString(Constants.GET_IMAGE, uri.toString())
        }
    }

    val requestPermissions =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            val deniedPermissions = results.filter { !it.value }
            if (deniedPermissions.isNotEmpty()) {
                val deniedPermissionsNames = deniedPermissions.keys.joinToString(", ")
                Toast.makeText(
                    context,
                    "Permissions denied, Please grant to access media files",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                openDocumentLauncher.launch(arrayOf("image/*"))
            }
        }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Settings",
                navHostController = navHostController
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        preferencesManager.setName(name)
                        if (isEditing) {
                            isEditing = false
                        }
                    })
                }
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Area A (30%)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.3f)
                        .background(MaterialTheme.colorScheme.surfaceContainerLow),
                    contentAlignment = Alignment.Center,
                ) {
                    if (!isEditing) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(
                                    color = Color.Transparent,
                                    shape = CircleShape,
                                )
                                .padding(top = 8.dp, end = 8.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Name",
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.Center)
                                    .clickable {
                                        isEditing = true
                                    }
                            )
                        }
                    }else{
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(
                                    color = Color.Transparent,
                                    shape = CircleShape,
                                )
                                .padding(top = 8.dp, end = 8.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Save Name",
                                tint = TickColor,
                                modifier = Modifier
                                    .size(36.dp)
                                    .align(Alignment.Center)
                                    .clickable {
                                        if (name.isEmpty()) {
                                            Toast
                                                .makeText(
                                                    context,
                                                    "Name cannot be empty",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                            return@clickable
                                        }
                                        isEditing = false
                                        preferencesManager.setName(name)
                                    }
                            )
                        }
                    }


                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(White)
                                .clickable {
                                    isUploadPhotoClicked = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isUploadPhotoClicked && isEditing) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                    requestPermissions.launch(
                                        arrayOf(
                                            READ_MEDIA_IMAGES,
                                            READ_MEDIA_VIDEO,
                                            READ_MEDIA_VISUAL_USER_SELECTED
                                        )
                                    )

                                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    requestPermissions.launch(
                                        arrayOf(
                                            READ_MEDIA_IMAGES,
                                            READ_MEDIA_VIDEO
                                        )
                                    )
                                } else {
                                    requestPermissions.launch(
                                        arrayOf(
                                            READ_EXTERNAL_STORAGE
                                        )
                                    )
                                }
                                isUploadPhotoClicked = false
                            }

                            if(image.isEmpty()) {
                                if (selectedImageUri == null) {
                                    Image(
                                        painter = painterResource(id = R.drawable.avatar),
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(RoundedCornerShape(50.dp))
                                            .clickable {
                                                isUploadPhotoClicked = true
                                            }
                                        ,
                                        contentScale = ContentScale.Crop
                                    )
                                }else{
                                    Image(
                                        painter = rememberAsyncImagePainter(selectedImageUri),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(RoundedCornerShape(50.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            } else {
                                Image(
                                    painter = rememberAsyncImagePainter(Uri.parse(image)),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(50.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        Spacer(modifier = Modifier.size(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (isEditing) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    TextField(
                                        modifier = Modifier.focusRequester(focusRequester),
                                        value = name,
                                        onValueChange = {
                                            if (it.length <= 13) {
                                                previousName = name
                                                name = it
                                                toastShown = false
                                            } else {
                                                if (it.length > previousName.length && !toastShown) {
                                                    Toast.makeText(
                                                        context,
                                                        "Name cannot be more than 12 characters",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    toastShown = true
                                                }
                                            }
                                        },
                                        label = null,
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            cursorColor = Color.White
                                        ),
                                        textStyle = TextStyle(
                                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                                            fontSize = 36.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),

                                        )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    /*Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Save Name",
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clickable {
                                                if (name.isEmpty()) {
                                                    Toast
                                                        .makeText(
                                                            context,
                                                            "Name cannot be empty",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                        .show()
                                                    return@clickable
                                                }
                                                isEditing = false
                                                preferencesManager.setName(name)
                                            },
                                        tint = TickColor
                                    )*/
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Area B (70%)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.7f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Row {
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .height(cardHeightDp.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(), // Ensure the Row takes up full width
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    InfoCard(
                                        title = "2100",
                                        subtitle = "Notes",
                                        modifier = Modifier.weight(1f)
                                    )

                                    InfoCard(
                                        title = "2100",
                                        subtitle = "Notes",
                                        modifier = Modifier.weight(1f)
                                    )

                                    InfoCard(
                                        title = "2100",
                                        subtitle = "Notes",
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = {
                                navHostController.navigate(NavigationItem.AboutPage.route)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(4.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 2.dp,
                                pressedElevation = 0.dp
                            ),
                            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "About Us!",
                                    style = TextStyle(
                                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Normal
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }

                        }
                    }
                }
            }

            // Card positioned 50% in Area A and 50% in Area B

        }
    }



}

private fun getUri(context: Context, uri: Uri) {
    val contentResolver = context.contentResolver
    contentResolver.takePersistableUriPermission(
        uri,
        Intent.FLAG_GRANT_READ_URI_PERMISSION
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewSettingsPage() {
    val context = LocalContext.current
    PagesTheme {
        Surface {
            SettingsPage(NavHostController(context))
        }
    }
}