package io.jadu.pages.presentation.screens

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import io.jadu.pages.R
import io.jadu.pages.core.Constants
import io.jadu.pages.core.PreferencesManager
import io.jadu.pages.presentation.components.CustomTopAppBar
import io.jadu.pages.presentation.components.InfoCard
import io.jadu.pages.presentation.components.TextFieldDialogue
import io.jadu.pages.presentation.navigation.NavigationItem
import io.jadu.pages.presentation.viewmodel.NotesViewModel
import io.jadu.pages.presentation.viewmodel.TodoViewModel
import io.jadu.pages.ui.theme.TickColor
import io.jadu.pages.ui.theme.White
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val TAG = "SettingsPage"
fun handleSignIn(result: GetCredentialResponse, scope: CoroutineScope, context:Context) {
    // Handle the successfully returned credential.
    when (val credential = result.credential) {
        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val googleTokenId = googleIdTokenCredential.idToken
                    val authCredential = GoogleAuthProvider.getCredential(googleTokenId, null)
                    scope.launch {
                       val user = Firebase.auth.signInWithCredential(authCredential).await().user
                        user?.let {
                            if(it.isAnonymous.not()){
                                withContext(Dispatchers.Main){
                                    Toast.makeText(context, "Sign in successful", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }

                    Log.d(TAG, "Received google id token: ${googleIdTokenCredential.id}")


                } catch (e: GoogleIdTokenParsingException) {
                    Log.e(TAG, "Received an invalid google id token response", e)
                }
            } else {
                Log.e(TAG, "Unexpected type of credential")
            }
        }

        else -> {
            Log.e(TAG, "Unexpected type of credential")
        }
    }
}

fun generateNonce(): String {
    val bytes = ByteArray(16)
    SecureRandom().nextBytes(bytes)
    return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
}


@Composable
fun SettingsPage(
    navHostController: NavHostController,
    viewModel: NotesViewModel,
    todoViewModel: TodoViewModel
) {
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp
    val cardHeightDp = screenHeightDp * 0.15f
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    var name by remember { mutableStateOf(preferencesManager.getName() ?: "") }
    val image by remember {
        mutableStateOf(
            preferencesManager.getString(Constants.GET_IMAGE) ?: ""
        )
    }

    var previousName by remember { mutableStateOf(name) }
    var isEditing by remember { mutableStateOf(false) }
    var toastShown by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploadPhotoClicked by remember { mutableStateOf(false) }
    val pagingNotes = viewModel.notesFlow.collectAsLazyPagingItems()
    val notes = pagingNotes.itemSnapshotList.items
    val noteSize = notes.size
    val totalPinnedNotes = notes.filter { it.isPinned }.size
    var showDialog by remember { mutableStateOf(false) }
    val todoSize =
        todoViewModel.getAllTodo.collectAsState(initial = emptyList()).value.filter { !it.isTaskCompleted }.size
    val isFeedback = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
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

    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId("362785170949-4c4m2eekvelfa1f8bk0lhlai962gkoj2.apps.googleusercontent.com")
        .setAutoSelectEnabled(true)
        .setNonce("")
    .build()

    val signInWithGoogleOption: GetSignInWithGoogleOption = GetSignInWithGoogleOption.Builder(
        "362785170949-qketo5dlbtftq2h8pb2ikfaoigmi4m7m.apps.googleusercontent.com")
    .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(signInWithGoogleOption)
        .build()

    val credentialManager = CredentialManager.create(context)


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
                    } else {
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
                            } else if (!isEditing && isUploadPhotoClicked) {
                                isUploadPhotoClicked = false
                                Toast.makeText(
                                    context,
                                    "Click on Edit icon and tap the image to update",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }


                            if (image.isEmpty()) {
                                if (selectedImageUri == null) {
                                    Image(
                                        painter = painterResource(id = R.drawable.avatar),
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(RoundedCornerShape(50.dp))
                                            .clickable {
                                                isUploadPhotoClicked = true
                                            },
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
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
                                            cursorColor = MaterialTheme.colorScheme.onSurface,
                                            selectionColors = TextSelectionColors(
                                                handleColor = MaterialTheme.colorScheme.outline,
                                                backgroundColor = MaterialTheme.colorScheme.outline
                                            )
                                        ),
                                        textStyle = TextStyle(
                                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                                            fontSize = 36.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        ),
                                        placeholder = {
                                            Text(
                                                text = "Add Your Name",
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontSize = 36.sp,
                                                fontWeight = FontWeight.Normal,
                                                fontStyle = FontStyle.Italic,
                                                color = Color.Gray,
                                            )
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (name.isEmpty()) {
                                        Text(
                                            text = "Edit Your Name and Photo",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Normal,
                                            fontStyle = FontStyle.Italic,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                    } else {
                                        Text(
                                            text = name,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontSize = 36.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(cardHeightDp.dp)
                        ) {
                            OutlinedCard(
                                modifier = Modifier.fillMaxSize(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth()
                                ) {
                                    // Container for the first InfoCard
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                scope.launch {
                                                    try {
                                                        val result = credentialManager.getCredential(
                                                            request = request,
                                                            context = context,
                                                        )
                                                        handleSignIn(result,scope, context)
                                                    } catch (e: GetCredentialException) {
                                                        Log.e(TAG, "Error getting credential, check it", e)
                                                    }
                                                }
                                            }
                                            .fillMaxHeight(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        InfoCard(
                                            title = noteSize.toString(),
                                            subtitle = "Notes",
                                            modifier = Modifier
                                        )
                                    }

                                    // Divider
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(1.dp)
                                            .background(
                                                MaterialTheme.colorScheme.onSurface.copy(
                                                    alpha = 0.2f
                                                )
                                            )
                                    )

                                    // Container for the second InfoCard
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        InfoCard(
                                            title = todoSize.toString() ?: "0",
                                            subtitle = "TO-DOs",
                                            modifier = Modifier
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        BorderButton(navHostController, "About Us", onClick = {
                            navHostController.navigate(NavigationItem.AboutPage.route)
                        }, subtitle = "Know more about us")
                        BorderButton(
                            navHostController,
                            "FeedBack",
                            onClick = {
                                showDialog = true
                                isFeedback.value = true
                            },
                            subtitle = "Give us your valuable feedback"
                        )
                        BorderButton(navHostController, "Report A Bug",
                            onClick = {
                            showDialog = true
                            isFeedback.value = false
                        }, subtitle = "Report any bugs you find")
                    }
                }
            }
        }

        if (showDialog) {
            TextFieldDialogue(
                onDismissRequest = {
                    showDialog = false
                },
                onSubmit = { bugDescription ->
                },
                isFeedbackClicked = isFeedback.value
            )
        }
    }
}

@Composable
private fun BorderButton(
    navHostController: NavHostController,
    title: String,
    onClick: () -> Unit,
    subtitle: String
) {
    OutlinedButton(
        onClick = {
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
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
            Column {
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = TextStyle(
                        fontFamily = MaterialTheme.typography.bodySmall.fontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color.Gray
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

    }
}

fun Long.toFormattedDate(): String {
    val date = Date(this)
    val format = SimpleDateFormat("dd MMM yy", Locale.getDefault())
    return format.format(date)
}

/*
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewSettingsPage() {
    val context = LocalContext.current
    PagesTheme {
        Surface {
            SettingsPage(NavHostController(context))
        }
    }
}*/
