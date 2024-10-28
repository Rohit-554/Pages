import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.presentation.components.CustomSnackBar
import io.jadu.pages.presentation.components.EditPageBottomAppBar
import io.jadu.pages.presentation.components.imeListener
import io.jadu.pages.presentation.viewmodel.NotesViewModel
import io.jadu.pages.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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

    LaunchedEffect(
        key1 = imeState.value
    ) {
        if (imeState.value) {
            scrollState.scrollTo(scrollState.maxValue)
        }
    }
    Scaffold(
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
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Transparent,
                content = {
                    Box(
                        contentAlignment = Alignment.CenterEnd,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.End,
                        ) {
                            FloatingActionButton(
                                onClick = {
                                    if (checkIfFieldEmpty(title.text)) {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(
                                                "Title cannot be empty",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                        areFieldEmpty = true
                                        return@FloatingActionButton
                                    }
                                    val newNote = Notes(
                                        id = System.currentTimeMillis(),
                                        title = title.text,
                                        description = description.text,
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
                                },
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(White),
                                containerColor = White,
                                elevation = FloatingActionButtonDefaults.elevation(5.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Save,
                                    contentDescription = "Save Note",
                                    tint = Color.Black
                                )
                            }
                        }
                    }
                }
            )


        },
        floatingActionButton = {
            EditPageBottomAppBar()
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(scrollState)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CustomInputFields(
                        text = title.text,
                        onTitleChange = { title = TextFieldValue(it) },
                        hintText = "Your Title"
                    )
                    CustomInputFields(
                        text = description.text,
                        onTitleChange = { description = TextFieldValue(it) },
                        hintText = "Write your note here...",
                        textStyle = TextStyle(
                            color = Color.White,
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                            fontSize = 16.sp
                        ),
                        singleLine = false
                    )
                }
            }


        },
        snackbarHost = {
            CustomSnackBar(
                snackBarHostState = snackbarHostState,
                icon = if (areFieldEmpty) Icons.Filled.Error else Icons.Filled.Check,  // Pass error or info icon
                isError = areFieldEmpty // Set true for error message
            )
        }
    )
}

@Composable
fun CustomInputFields(
    text: String,
    onTitleChange: (String) -> Unit,
    textStyle: TextStyle = TextStyle(
        color = White,
        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold
    ),
    hintText: String = "Your Title...",
    singleLine: Boolean = true
) {
    BasicTextField(
        value = text,
        cursorBrush = SolidColor(Color.White),
        onValueChange = onTitleChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        singleLine = singleLine,
        textStyle = textStyle,
        decorationBox = { innerTextField ->
            if (text.isEmpty()) {
                Text(
                    text = hintText,
                    color = Color.Gray,
                    style = textStyle
                )
            }
            innerTextField()
        }
    )
}

fun checkIfFieldEmpty(fieldKey: String): Boolean {
    return fieldKey.isEmpty()
}

