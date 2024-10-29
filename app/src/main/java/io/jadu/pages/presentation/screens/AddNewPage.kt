import android.net.Uri
import android.util.Log
import android.widget.Space
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.presentation.components.ColorPickerDialog
import io.jadu.pages.presentation.components.CustomInputFields
import io.jadu.pages.presentation.components.CustomSnackBar
import io.jadu.pages.presentation.components.EditPageBottomAppBar
import io.jadu.pages.presentation.components.SaveFab
import io.jadu.pages.presentation.components.imeListener
import io.jadu.pages.presentation.viewmodel.NotesViewModel
import io.jadu.pages.ui.theme.Black
import io.jadu.pages.ui.theme.LightGray
import io.jadu.pages.ui.theme.PrimaryBackground
import io.jadu.pages.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddNewPage(viewModel: NotesViewModel, navHostController: NavHostController) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    val selectedImage by remember { mutableStateOf<String?>(null) }
    val imeState = imeListener()
    val scrollState = rememberScrollState()
    var areFieldEmpty by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var showColorPickerDialog by remember { mutableStateOf(false) }
    val defaultColor = MaterialTheme.colorScheme.background
    var selectedColor by remember { mutableStateOf(defaultColor) }
    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                Log.d("PhotoPicker", "Selected URI: $uri")
            } else {
                Log.d("PhotoPicker", "No media selected")
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
            TopAppBar(
                title = {
                    Text(
                        "Add New Note",
                        style = TextStyle(
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Column {
                EditPageBottomAppBar(
                    onImagePickClick = {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    onColorPickClick = {
                        showColorPickerDialog = true
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                SaveFab {
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
                        title = title.text,
                        description = description.text,
                        color = selectedColor.toString(),
                        imageUri = selectedImageUri.toString()
                    )
                    viewModel.addNotes(newNote)
                    areFieldEmpty = false
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
                                fontSize = 16.sp
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

                    selectedImageUri?.let {
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
                                    Image(
                                        painter = rememberAsyncImagePainter(it),
                                        contentDescription = "Selected Image",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(800.dp),
                                        contentScale = ContentScale.FillWidth
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        ,
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



