package io.jadu.pages.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import io.jadu.pages.domain.model.TodoModel
import io.jadu.pages.presentation.components.CustomFab
import io.jadu.pages.presentation.components.HomeTopAppBar
import io.jadu.pages.presentation.viewmodel.TodoViewModel
import io.jadu.pages.ui.theme.ButtonBlue
import io.jadu.pages.ui.theme.LightGray
import io.jadu.pages.ui.theme.White
import io.jadu.pages.ui.theme.onBackground
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoPage(todoViewModel: TodoViewModel, navHostController: NavHostController) {
    val todos = todoViewModel.getAllTodo.collectAsState(initial = emptyList())
    val bottomSheetState = androidx.compose.material.rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val scope = rememberCoroutineScope()

    var newTodoText by remember { mutableStateOf("") }


    fun showBottomSheet() {
        scope.launch {
            bottomSheetState.show()
        }
    }

    fun saveTodo() {
        if (newTodoText.isNotBlank()) {
            val newTodo = TodoModel(
                task = newTodoText,
                isTaskCompleted = false,
            )
            todoViewModel.addTodo(newTodo)
            newTodoText = ""
            scope.launch {
                bottomSheetState.hide()
            }
        }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetBackgroundColor = onBackground,
        sheetContent = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .imePadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Add TODO",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(
                        modifier = Modifier.clickable {
                            scope.launch {
                                bottomSheetState.hide()
                            }
                        },
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Add TODO",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.OutlinedTextField(
                    value = newTodoText,
                    onValueChange = { newTodoText = it },
                    label = { Text("TODO") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = LightGray,
                        focusedContainerColor = White,
                        focusedLabelColor = Color.White,
                        focusedTextColor = Color.Black,
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
                ElevatedButton(
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = ButtonBlue,
                    ),
                    onClick = { saveTodo() },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp) // 10dp rounded corners
                ) {
                    Text(
                        "Save",
                        style = TextStyle(
                            color = White,
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                            fontWeight = FontWeight.Black
                        )
                    )
                }

            }
        }
    ) {
        Scaffold(
            topBar = {
                HomeTopAppBar(
                    onSearchClick = {},
                    onMenuClick = {

                    },
                    title = "To-dos",
                    isHome = false,
                    onSearchTextChange = { searchText -> }
                )
            },
            contentWindowInsets = WindowInsets(
                top = 0.dp,
                bottom = 0.dp,
                left = 0.dp,
                right = 0.dp
            ),
            floatingActionButton = {
                CustomFab(
                    onClick = { showBottomSheet() },
                    icon = Icons.Default.Add,
                    contentDescription = "Add TODO",
                    backgroundColor = ButtonBlue
                )
            }
        ) { padding ->
            Column(
                Modifier.padding(padding).padding(horizontal = 4.dp)
            ) {
                if(todos.value.isEmpty()){
                    Column(
                        Modifier.fillMaxSize().padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "      No To-dos 🤒, Click the + Icon to get started",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }else{
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(8.dp)
                    ) {
                        todos.value.forEach {
                            item {
                                TodoListItem(
                                    todoText = it.task.toString(),
                                    isTaskCompleted = it.isTaskCompleted,
                                    onClick = {
                                        todoViewModel.updateTodo(
                                            it.copy(isTaskCompleted = !it.isTaskCompleted,)
                                        )
                                    },
                                    viewModel = todoViewModel,
                                    id = it.id
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TodoListItem(
    todoText: String,
    isTaskCompleted: Boolean,
    onClick: () -> Unit,
    viewModel: TodoViewModel,
    id: Long
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 8.dp),
        color = Color(0xFF333333),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                }
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isTaskCompleted,
                onClick = { onClick() },
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = todoText,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textDecoration = if (isTaskCompleted) TextDecoration.LineThrough else TextDecoration.None // Line through if completed
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete",
                tint = Color.White,
                modifier = Modifier.padding(end = 8.dp).clickable {
                    viewModel.deleteTodo(id)
                }
            )
        }
    }
}




