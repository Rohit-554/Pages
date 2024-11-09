import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.presentation.components.ColorPickerDialog
import io.jadu.pages.presentation.components.CustomInputFields
import io.jadu.pages.presentation.components.CustomSnackBar
import io.jadu.pages.presentation.components.CustomTopAppBar
import io.jadu.pages.presentation.components.EditPageBottomAppBar
import io.jadu.pages.presentation.components.SaveFab
import io.jadu.pages.presentation.components.imeListener
import io.jadu.pages.presentation.screens.parseColor
import io.jadu.pages.presentation.viewmodel.NotesViewModel
import io.jadu.pages.ui.theme.Black
import io.jadu.pages.ui.theme.LightGray
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddNewPage(
    viewModel: NotesViewModel,
    navHostController: NavHostController,
    notesId: Long? = 0L
) {
    val imeState = imeListener()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val notes = viewModel.notes.collectAsState(initial = emptyList()).value
    val toolBarText = if (notesId != 0L) "Update Note" else "Add New Note"
    val defaultColor = MaterialTheme.colorScheme.background

    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var selectedColor by remember { mutableStateOf(defaultColor) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImage by remember { mutableStateOf<String?>(null) }
    var areFieldEmpty by remember { mutableStateOf(false) }
    var showColorPickerDialog by remember { mutableStateOf(false) }
    var isPinned by remember { mutableStateOf(false) }


    LaunchedEffect(notesId, notes) {
        if (notesId != 0L) {
            val note = notes.find { it.id == notesId }
            if (note != null) {
                title = TextFieldValue(note.title)
                description = TextFieldValue(note.description ?: "")
                selectedColor = note.color?.let { parseColor(it) } ?: defaultColor
                selectedImageUri = note.imageUri?.let { Uri.parse(it) }
                isPinned = note.isPinned
            }
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



    LaunchedEffect(
        key1 = imeState.value
    ) {
        if (imeState.value) {
            scrollState.scrollTo(scrollState.maxValue)
        }
    }
    Scaffold(
        containerColor = if (selectedColor != Black) selectedColor else MaterialTheme.colorScheme.background,
        topBar = {
            CustomTopAppBar(
                title = toolBarText,
                navHostController = navHostController
            )
        },
        floatingActionButton = {
            Column {
                if(notesId!=0L && notesId!=null){
                    SaveFab(
                        icon = Icons.Default.Delete,
                        containerColor = Color(0xffff474c),
                        tintColor = Color.White,
                        onClick = {
                            viewModel.deleteNotes(notesId)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    "Deleted Successfully",
                                    duration = SnackbarDuration.Short
                                )
                            }
                            coroutineScope.launch {
                                delay(250)
                                navHostController.popBackStack()
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                EditPageBottomAppBar(
                    onImagePickClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                            requestPermissions.launch(
                                arrayOf(
                                    READ_MEDIA_IMAGES,
                                    READ_MEDIA_VIDEO,
                                    READ_MEDIA_VISUAL_USER_SELECTED
                                )
                            )
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            requestPermissions.launch(arrayOf(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO))
                        } else {
                            requestPermissions.launch(arrayOf(READ_EXTERNAL_STORAGE))
                        }
                    },
                    onColorPickClick = {
                        showColorPickerDialog = true
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                SaveFab(
                    onClick = {
                        if (checkIfFieldEmpty(title.text)) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    "Title cannot be empty",
                                    duration = SnackbarDuration.Short
                                )
                            }
                            areFieldEmpty = true
                            return@SaveFab
                        }
                        val newNote = Notes(
                            id = System.currentTimeMillis(),
                            title = title.text.trim(),
                            description = description.text.trim(),
                            color = if (selectedColor != defaultColor) selectedColor.toString() else null,
                            imageUri = selectedImageUri.toString()
                        )
                        if(notesId != 0L && notesId != null){
                            viewModel.updateNotes(
                                title = title.text.trim(),
                                description = description.text.trim(),
                                imageUri = selectedImageUri.toString(),
                                notesId = notesId,
                                color = if (selectedColor != defaultColor) selectedColor.toString() else null,
                                isPinned = isPinned
                            )
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    "Updated Successfully",
                                    duration = SnackbarDuration.Short
                                )
                            }
                            coroutineScope.launch {
                                delay(250)
                                navHostController.popBackStack()
                            }
                        }else{
                            viewModel.addNotes(newNote)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    "Saved Successfully",
                                    duration = SnackbarDuration.Short
                                )
                            }
                            coroutineScope.launch {
                                delay(250)
                                navHostController.popBackStack()
                            }
                        }
                    }
                )
            }
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .consumeWindowInsets(padding)

            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        CustomInputFields(
                            text = title.text,
                            onTitleChange = { title = TextFieldValue(it) },
                            hintText = "Your Title",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                    item {
                        HorizontalDivider(
                            color = LightGray,
                            thickness = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        CustomInputFields(
                            text = description.text,
                            onTitleChange = { description = TextFieldValue(it) },
                            hintText = "Write your note here...",
                            textStyle = TextStyle(
                                color = Color.White,
                                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                                fontSize = 20.sp
                            ),
                            singleLine = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                    item {
                        ColorPickerDialog(
                            showDialog = showColorPickerDialog,
                            onDismiss = { showColorPickerDialog = false },
                            onColorSelected = { color ->
                                selectedColor = color
                            },
                            selectedColor = selectedColor,
                            onResetToDefaultSelected = {
                                selectedColor = defaultColor
                            }
                        )
                    }
                    if(selectedImageUri!=null && selectedImageUri.toString() != "null"){
                        item {
                            Box {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = "Color Lens",
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clickable {
                                                    selectedImageUri = null
                                                }
                                        )
                                    }
                                    val screenHeight = LocalConfiguration.current.screenHeightDp
                                    Image(
                                        painter = rememberAsyncImagePainter(selectedImageUri),
                                        contentDescription = "Selected Image",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                            .then(Modifier.heightIn(max = screenHeight.dp / 3)),

                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        snackbarHost = {
            CustomSnackBar(
                snackBarHostState = snackbarHostState,
                icon = if (areFieldEmpty) Icons.Filled.Error else Icons.Filled.Check,
                isError = areFieldEmpty
            )
        }
    )
}


fun checkIfFieldEmpty(fieldKey: String): Boolean {
    return fieldKey.isEmpty()
}




