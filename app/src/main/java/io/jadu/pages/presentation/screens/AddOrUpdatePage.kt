import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.mr0xf00.easycrop.CropError
import com.mr0xf00.easycrop.CropResult
import com.mr0xf00.easycrop.crop
import com.mr0xf00.easycrop.images.ImageSrc
import com.mr0xf00.easycrop.rememberImageCropper
import com.mr0xf00.easycrop.ui.ImageCropperDialog
import io.jadu.pages.core.Utils
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.domain.model.PathProperties
import io.jadu.pages.presentation.components.ColorPickerDialog
import io.jadu.pages.presentation.components.CustomDialog
import io.jadu.pages.presentation.components.CustomInputFields
import io.jadu.pages.presentation.components.CustomSnackBar
import io.jadu.pages.presentation.components.CustomTopAppBar
import io.jadu.pages.presentation.components.EditPageBottomAppBar
import io.jadu.pages.presentation.components.ImagePickerDialog
import io.jadu.pages.presentation.navigation.NavigationItem
import io.jadu.pages.presentation.screens.parseColor
import io.jadu.pages.presentation.viewmodel.NotesViewModel
import io.jadu.pages.ui.theme.Black
import io.jadu.pages.ui.theme.LightGray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddNewPage(
    viewModel: NotesViewModel,
    navHostController: NavHostController,
    notesId: Long? = 0L,
    drawPath: List<Pair<Path, PathProperties>>,
    bitmap: Bitmap?,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val pagingNotes = viewModel.notesFlow.collectAsLazyPagingItems()
    val notes = pagingNotes.itemSnapshotList.items
    val toolBarText = if (notesId != 0L) "Update Note" else "Add New Note"
    val defaultColor = MaterialTheme.colorScheme.background
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var selectedColor by remember { mutableStateOf(defaultColor) }
    var areFieldEmpty by remember { mutableStateOf(false) }
    var showColorPickerDialog by remember { mutableStateOf(false) }
    var isPinned by remember { mutableStateOf(false) }
    val selectedImageUriList = viewModel.imageUriList.collectAsState(initial = emptyList()).value
    val notsState = viewModel.notesState.collectAsState().value
    val scrollState = rememberScrollState()
    val isKeyboardOpen by keyboardAsState()
    var shouldScrollToBottom by remember { mutableStateOf(true) }
    var isNoteDeleteClicked by remember { mutableStateOf(false) }
    var showImagePickerDialog by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Pair<Uri?, Long>?>(null to 0L) }
    var isLoading by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState
    val scannedText by viewModel.scannedText.collectAsState()
    val imageCropper = rememberImageCropper()
    val scope = rememberCoroutineScope()
    val cropState = imageCropper.cropState
    if (cropState != null) ImageCropperDialog(state = cropState)
    var isBackPressed by remember { mutableStateOf(false) }

    LaunchedEffect(notesId, notes) {
        viewModel.onSearchTextChanged("")
        if (notesId != 0L) {
            val note = notes.find { it.id == notesId }
            if (note != null) {
                title = TextFieldValue(note.title)
                description = TextFieldValue(note.description ?: "")
                selectedColor = note.color?.let { parseColor(it) } ?: defaultColor
                note.imageUri?.forEach { uri ->
                    if (uri !in viewModel.imageUriList.value) {
                        viewModel.addImageUris(uri)
                    }
                }
                isPinned = note.isPinned
            }
        }
        if (notsState.title.isNotEmpty() || notsState.description != "" || notsState.color != Color.Black || notsState.isPinned) {
            title = TextFieldValue(notsState.title)
            description = TextFieldValue(notsState.description ?: "")
            selectedColor = notsState.color ?: defaultColor
            isPinned = notsState.isPinned
            shouldScrollToBottom = notsState.shouldScroll
        }
    }

    LaunchedEffect(shouldScrollToBottom) {
        if (shouldScrollToBottom) {
            coroutineScope.launch {
                delay(250)
                shouldScrollToBottom = false
                scrollState.animateScrollTo(scrollState.maxValue)
            }
        }
    }

    LaunchedEffect(scannedText) {
        if (scannedText.isNotEmpty()) {
            description = TextFieldValue(description.text + "\n" + scannedText)
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
                    coroutineScope.launch {
                        scrollState.animateScrollTo(0)
                    }
                } else {
                    Toast.makeText(context, "Image already added", Toast.LENGTH_SHORT).show()
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

    LaunchedEffect(imageUri) {
        if (imageUri!!.first != null) {
            scope.launch {
                val result = Utils().uriToBitmap(context, imageUri!!.first!!)?.let {
                    imageCropper.crop(
                        bmp = it.asImageBitmap(),
                    )
                }

                when (result) {
                    CropResult.Cancelled -> {
                        Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
                    }
                    is CropError -> {
                        Toast.makeText(context, "Error: ${CropError.SavingError}", Toast.LENGTH_SHORT).show()
                    }
                    is CropResult.Success -> {
                        val bmp = result.bitmap.asAndroidBitmap()
                        Log.d("AddNewPage", "Image bmp: $bmp")
                        val uri = Utils().convertBitmapToUri(context, bmp)
                        Log.d("AddNewPage", "Image Uri: $uri")
                        viewModel.generateText(uri, context)
                    }

                    null -> {
                        Toast.makeText(context, "Image not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    when (uiState) {
        is UIState.IsIdle -> {
            isLoading = false
        }

        is UIState.Loading -> {
            isLoading = true
        }

        is UIState.Content -> {
            isLoading = false
        }

        is UIState.Error -> {
            val errorMessage = (uiState as UIState.Error).message
            Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show()
            viewModel.clearState()
            isLoading = false
        }

        else -> {}
    }


    Scaffold(containerColor = if (selectedColor != Black) selectedColor else MaterialTheme.colorScheme.background,
        topBar = {
            CustomTopAppBar(
                title = toolBarText, navHostController = navHostController, isDrawMenu = true,
                onSaveClick = {
                    if (checkIfFieldEmpty(title.text)) {
                        coroutineScope.launch {
                            Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT)
                                .show()
                        }
                        areFieldEmpty = true
                        return@CustomTopAppBar
                    }
                    val newNote = Notes(
                        id = System.currentTimeMillis(),
                        title = title.text.trim(),
                        description = description.text.trim(),
                        color = if (selectedColor != defaultColor) selectedColor.toString() else null,
                        imageUri = selectedImageUriList,
                        drawingPaths = null,
                        isPinned = isPinned
                    )
                    if (notesId != 0L && notesId != null) {
                        viewModel.updateNotes(
                            title = title.text.trim(),
                            description = description.text.trim(),
                            imageUri = selectedImageUriList,
                            notesId = notesId,
                            drawingPaths = null,
                            color = if (selectedColor != defaultColor) selectedColor.toString() else null,
                            isPinned = isPinned
                        )
                        Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
                        coroutineScope.launch {
                            withContext(Dispatchers.Main) {
                                navHostController.popBackStack()
                            }
                        }
                    } else {
                        viewModel.addNotes(newNote)
                        Toast.makeText(context, "Saved Successfully", Toast.LENGTH_SHORT).show()
                        coroutineScope.launch {
                            withContext(Dispatchers.Main) {
                                navHostController.popBackStack()
                            }
                        }
                    }
                },
                onDeleteClick = {
                    isNoteDeleteClicked = true
                },
                onPinClick = {
                    isPinned = !isPinned
                    if (notesId != null) {
                        updateNote(
                            viewModel = viewModel,
                            title = title.text.trim(),
                            description = description.text.trim(),
                            imageUri = selectedImageUriList,
                            notesId = notesId,
                            color = if (selectedColor != defaultColor) selectedColor.toString() else null,
                            isPinned = true
                        )
                    }
                    Toast.makeText(
                        context,
                        if (isPinned) "Pinned Successfully" else "Unpinned Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                isPinned = isPinned,
                onScanClick = {
                    showImagePickerDialog = true
                    //launcher.launch("image/*")
                },
                isBackPressed = {
                        isBackPressed = it
                }
            )
        },
        floatingActionButton = {
            Column(
                Modifier.padding(16.dp)
            ) {
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
                    },
                    onDrawClick = {
                        viewModel.addNotesState(
                            notsState.copy(
                                title = title.text,
                                description = description.text,
                                color = selectedColor,
                                isPinned = isPinned,
                                shouldScroll = shouldScrollToBottom
                            )
                        )
                        navHostController.navigate(NavigationItem.DrawPage.route)
                    },
                    scrollState = scrollState
                )
            }
        },
        content = { padding ->

            if (showImagePickerDialog) {
                ImagePickerDialog(
                    onImagePicked = { uri ->
                        showImagePickerDialog = false
                        imageUri = uri to System.currentTimeMillis()
                    }
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .imePadding()
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp)
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .verticalScroll(scrollState, reverseScrolling = true),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    LaunchedEffect(Unit) {
                        scrollState.scrollTo(scrollState.maxValue)
                    }
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
                        selectedImageUriList.forEach { uri ->
                            if (uri.toString().contains("drawing")) {
                                GraphicsItem(
                                    imageUri = uri,
                                    onCancel = {
                                        viewModel.removeImageUri(uri)
                                    },
                                    isDrawing = true,
                                    modifier = Modifier
                                )

                            } else {
                                GraphicsItem(
                                    imageUri = uri,
                                    onCancel = {
                                        viewModel.removeImageUri(uri)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            if (isNoteDeleteClicked) {
                if (notesId != 0L) {
                    CustomDialog(
                        title = "Delete Note",
                        description = "Are you sure you want to delete this note?",
                        onConfirm = {
                            isNoteDeleteClicked = false
                            if (notesId != null) {
                                viewModel.deleteNotes(notesId)
                                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT)
                                    .show()
                                coroutineScope.launch {
                                    withContext(Dispatchers.Main) {
                                        navHostController.popBackStack()
                                    }
                                }
                            }
                        },
                        onCancel = {
                            isNoteDeleteClicked = false
                        }
                    )
                } else {
                    isNoteDeleteClicked = false
                    Toast.makeText(context, "Note is not added yet", Toast.LENGTH_SHORT).show()
                }
            }
        },

        snackbarHost = {
            CustomSnackBar(
                snackBarHostState = snackBarHostState,
                icon = if (areFieldEmpty) Icons.Filled.Error else Icons.Filled.Check,
                isError = areFieldEmpty
            )
        })

    BackHandler {
        /*if (title.text.trim().isEmpty()) {
            Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
            return@BackHandler
        }*/


        handleBackPress(
            title = title,
            description = description,
            selectedImageUriList = selectedImageUriList,
            drawPath = drawPath,
            selectedColor = selectedColor,
            defaultColor = defaultColor,
            isPinned = isPinned,
            notesId = notesId,
            viewModel = viewModel,
            coroutineScope = coroutineScope,
            navHostController = navHostController,
            isbackHandler = true
        )

    }

    if(isBackPressed){
        handleBackPress(
            title = title,
            description = description,
            selectedImageUriList = selectedImageUriList,
            drawPath = drawPath,
            selectedColor = selectedColor,
            defaultColor = defaultColor,
            isPinned = isPinned,
            notesId = notesId,
            viewModel = viewModel,
            coroutineScope = coroutineScope,
            navHostController = navHostController
        )
        isBackPressed = false
    }

}

fun handleBackPress(
    title: TextFieldValue,
    description: TextFieldValue,
    selectedImageUriList: List<Uri>,
    drawPath: List<Pair<Path, PathProperties>>,
    selectedColor: Color,
    defaultColor: Color,
    isPinned: Boolean,
    notesId: Long?,
    viewModel: NotesViewModel,
    coroutineScope: CoroutineScope,
    navHostController: NavHostController,
    isbackHandler: Boolean = false
) {
    if (title.text.trim().isNotEmpty() || description.text.trim().isNotEmpty() || selectedImageUriList.isNotEmpty() || drawPath.isNotEmpty()) {
        val newNote = Notes(
            id = System.currentTimeMillis(),
            title = title.text.trim(),
            description = description.text.trim(),
            color = if (selectedColor != defaultColor) selectedColor.toString() else null,
            imageUri = selectedImageUriList,
            drawingPaths = null,
            isPinned = isPinned,
            isNoteSaved = false
        )

        coroutineScope.launch {
            withContext(Dispatchers.Main) {
                if (notesId != 0L && notesId != null) {
                    viewModel.updateNotes(
                        title = newNote.title,
                        description = newNote.description,
                        imageUri = newNote.imageUri,
                        notesId = notesId,
                        drawingPaths = newNote.drawingPaths,
                        color = newNote.color,
                        isPinned = newNote.isPinned
                    )
                    Toast.makeText(navHostController.context, "Updated Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.addNotes(newNote)
                    Toast.makeText(navHostController.context, "Draft Saved Successfully", Toast.LENGTH_SHORT).show()
                }
                if(isbackHandler) navHostController.popBackStack()
            }
        }
    } else {
        if(isbackHandler) navHostController.popBackStack()
    }
}

fun updateNote(
    viewModel: NotesViewModel,
    title: String,
    description: String,
    imageUri: List<Uri>,
    notesId: Long,
    color: String?,
    isPinned: Boolean
) {
    viewModel.updateNotes(
        title = title,
        description = description,
        imageUri = imageUri,
        notesId = notesId,
        drawingPaths = null,
        color = color,
        isPinned = isPinned
    )
}

@Composable
fun GraphicsItem(
    imageUri: Uri,
    onCancel: () -> Unit,
    isDrawing: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
            .takeIf { isDrawing } ?: Modifier
            .wrapContentHeight()
    ) {
        val screenHeight = LocalConfiguration.current.screenHeightDp
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = "Selected Image",
            modifier = modifier
                .heightIn(max = screenHeight.dp / 3),
            contentScale = if (isDrawing) ContentScale.None else ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = CircleShape
                )
                .clickable { onCancel() }
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cancel Image",
                modifier = Modifier
                    .size(24.dp)
                    .padding(4.dp),
                tint = Color.White
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
                top = minOf(acc.top, pathBounds.top - 32),
                right = maxOf(acc.right, pathBounds.right),
                bottom = maxOf(acc.bottom + 32, pathBounds.bottom + 32)
            )
        }
    }

    val pathOffset = Offset(pathBounds.left - 32, pathBounds.top)

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
            Box(
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = CircleShape
                    )
                    .clickable { onClose() }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier
                        .size(24.dp)
                        .padding(4.dp),
                    tint = Color.White
                )
            }
        }

        val canvasWidth = pathBounds.width
        val canvasHeight = pathBounds.height

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clipToBounds()
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((canvasHeight / LocalDensity.current.density).dp)
            ) {
                drawRect(
                    color = Color.White,
                    size = Size(canvasWidth, canvasHeight),
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

fun checkIfFieldEmpty(fieldKey: String): Boolean {
    return fieldKey.isEmpty()
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}


sealed class UIState<out T> {
    data object IsIdle : UIState<Nothing>()
    data object Loading : UIState<Nothing>()
    data class Content<T>(val data: T) : UIState<T>()
    data class Error(val message: String) : UIState<Nothing>()
}
