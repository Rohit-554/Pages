import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.domain.model.PathProperties
import io.jadu.pages.presentation.components.ColorPickerDialog
import io.jadu.pages.presentation.components.CustomInputFields
import io.jadu.pages.presentation.components.CustomSnackBar
import io.jadu.pages.presentation.components.CustomTopAppBar
import io.jadu.pages.presentation.components.EditPageBottomAppBar
import io.jadu.pages.presentation.components.SaveFab
import io.jadu.pages.presentation.components.imeListener
import io.jadu.pages.presentation.navigation.NavigationItem
import io.jadu.pages.presentation.screens.parseColor
import io.jadu.pages.presentation.viewmodel.NotesViewModel
import io.jadu.pages.ui.theme.Black
import io.jadu.pages.ui.theme.LightGray
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class, ExperimentalFoundationApi::class
)
@Composable
fun AddNewPage(
    viewModel: NotesViewModel,
    navHostController: NavHostController,
    notesId: Long? = 0L,
    drawPath: List<Pair<Path, PathProperties>>
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
    var areFieldEmpty by remember { mutableStateOf(false) }
    var showColorPickerDialog by remember { mutableStateOf(false) }
    var isPinned by remember { mutableStateOf(false) }
    var drawPathLines by remember { mutableStateOf(drawPath) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val selectedImageUriList = viewModel.imageUriList.collectAsState(initial = emptyList()).value
    //var drawPathLines = viewModel.drawingPathList.collectAsState(initial = emptyList()).value

    /*if(!drawPathLines.contains(drawPath)){
       viewModel.addDrawingPath(drawPath)
    }*/


    LaunchedEffect(selectedImageUriList) {
        Log.d("AddNewPageselect", "Selected Image Uri List: $selectedImageUriList")
    }
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
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        uris?.let {
            val contentResolver = context.contentResolver
            for (uri in uris) {
                contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                if (!selectedImageUriList.contains(uri)) {
                    viewModel.addImageUris(uri)
                }
            }
        }
    }

    var cancelAttempts by remember { mutableIntStateOf(0) }

    val requestPermissions = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        if (results.values.all { it }) {
            openDocumentLauncher.launch(arrayOf("image/*"))
        } else {
            cancelAttempts++
            if (cancelAttempts > 2) {
                Toast.makeText(
                    context,
                    "Permissions denied multiple times, please grant to access media files",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "Permissions denied, please grant to access media files",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



    LaunchedEffect(
        key1 = imeState.value
    ) {
        if (imeState.value) {
            scrollState.scrollTo(scrollState.maxValue)
        }
    }
    Scaffold(containerColor = if (selectedColor != Black) selectedColor else MaterialTheme.colorScheme.background,
        topBar = {
            CustomTopAppBar(
                title = toolBarText, navHostController = navHostController
            )
        },
        floatingActionButton = {
            Column {
                if (notesId != 0L && notesId != null) {
                    SaveFab(icon = Icons.Default.Delete,
                        containerColor = Color(0xffff474c),
                        tintColor = Color.White,
                        onClick = {
                            viewModel.deleteNotes(notesId)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    "Deleted Successfully", duration = SnackbarDuration.Short
                                )
                            }
                            coroutineScope.launch {
                                delay(250)
                                navHostController.popBackStack()
                            }
                        })
                }
                Spacer(modifier = Modifier.height(8.dp))
                EditPageBottomAppBar(onImagePickClick = {
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
                }, onColorPickClick = {
                    showColorPickerDialog = true
                }, onDrawClick = {
                    navHostController.navigate(NavigationItem.DrawPage.route)
                })
                Spacer(modifier = Modifier.height(8.dp))
                SaveFab(onClick = {
                    if (checkIfFieldEmpty(title.text)) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                "Title cannot be empty", duration = SnackbarDuration.Short
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
                    if (notesId != 0L && notesId != null) {
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
                                "Updated Successfully", duration = SnackbarDuration.Short
                            )
                        }
                        coroutineScope.launch {
                            delay(250)
                            navHostController.popBackStack()
                        }
                    } else {
                        viewModel.addNotes(newNote)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                "Saved Successfully", duration = SnackbarDuration.Short
                            )
                        }
                        coroutineScope.launch {
                            delay(250)
                            navHostController.popBackStack()
                        }
                    }
                })
            }
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .imePadding()

            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState(), reverseScrolling = true)
                        .imePadding(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CustomInputFields(
                        text = title.text,
                        onTitleChange = { title = TextFieldValue(it) },
                        hintText = "Your Title",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )

                    HorizontalDivider(
                        color = LightGray, thickness = 1.dp, modifier = Modifier.fillMaxWidth()
                    )

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

                    ColorPickerDialog(showDialog = showColorPickerDialog,
                        onDismiss = { showColorPickerDialog = false },
                        onColorSelected = { color ->
                            selectedColor = color
                        },
                        selectedColor = selectedColor,
                        onResetToDefaultSelected = {
                            selectedColor = defaultColor
                        })


                    if (selectedImageUriList.isNotEmpty()) {
                        selectedImageUriList.forEach { imageUri ->
                            if (imageUri != null) {
                                ImageItem(imageUri = imageUri) {
                                    viewModel.removeImageUri(imageUri)
                                }
                            }
                        }
                    }

                    if (drawPathLines.isNotEmpty()) {
                        Box(
                            modifier = Modifier.background(Color.Gray).wrapContentHeight()
                        ){
                            DisplayPaths(drawPathLines, onClose = {
                                drawPathLines = emptyList()
                            })
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
        })
}


@Composable
fun ImageItem(imageUri: Uri, onCancel: () -> Unit) {
    Box {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel Image",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onCancel() }
                )
            }
            val screenHeight = LocalConfiguration.current.screenHeightDp
            Image(
                painter = rememberAsyncImagePainter(imageUri),
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





@Composable
fun DisplayPaths(paths: List<Pair<Path, PathProperties>>, onClose: () -> Unit) {
    val pathBounds = remember(paths) {
        paths.fold(
            Rect(
                Float.POSITIVE_INFINITY,
                Float.POSITIVE_INFINITY,
                Float.NEGATIVE_INFINITY,
                Float.NEGATIVE_INFINITY
            )
        ) { acc, pair ->
            val pathBounds = pair.first.getBounds()
            Rect(
                left = minOf(acc.left, pathBounds.left),
                top = minOf(acc.top, pathBounds.top),
                right = maxOf(acc.right, pathBounds.right),
                bottom = maxOf(acc.bottom, pathBounds.bottom)
            )
        }
    }

    val pathOffset = Offset(pathBounds.left, pathBounds.top)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        onClose()
                    }
            )
        }

        val canvasWidth = pathBounds.width
        val canvasHeight = pathBounds.height
        val screenWidth = (LocalConfiguration.current.screenWidthDp) * LocalDensity.current.density

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clipToBounds()
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(canvasHeight.dp)
            ) {
                drawRect(
                    color = Color.White,
                    size = Size(screenWidth.toFloat(), canvasHeight),
                    topLeft = Offset.Zero
                )

                withTransform({
                    translate(left = -pathOffset.x, top = -pathOffset.y)
                }) {
                    paths.forEach { (path, properties) ->
                        drawPath(
                            path = path,
                            color = properties.color,
                            style = Stroke(width = properties.strokeWidth)
                        )
                    }
                }
            }
        }
    }
}




@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}


fun checkIfFieldEmpty(fieldKey: String): Boolean {
    return fieldKey.isEmpty()
}




