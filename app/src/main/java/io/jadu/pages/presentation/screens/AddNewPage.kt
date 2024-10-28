import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import io.jadu.pages.presentation.components.EditPageBottomAppBar
import io.jadu.pages.presentation.components.imeListener
import io.jadu.pages.presentation.viewmodel.NotesViewModel
import io.jadu.pages.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewPage(viewModel: NotesViewModel, navHostController: NavHostController) {
    var title by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    val selectedImage by remember { mutableStateOf<String?>(null) }
    val imeState = imeListener()
    val scrollState = rememberScrollState()
    LaunchedEffect(
        key1 = imeState.value
    ) {
        if(imeState.value){
            scrollState.scrollTo(scrollState.maxValue)
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Note") },
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
            EditPageBottomAppBar()
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
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
                        color = White,
                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                        fontSize = 16.sp
                    ),
                    singleLine = false
                )

            }
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
    singleLine:Boolean = true
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

