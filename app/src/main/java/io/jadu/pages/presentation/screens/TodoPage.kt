package io.jadu.pages.presentation.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import io.jadu.pages.ComposableLifecycle
import io.jadu.pages.R
import io.jadu.pages.TodoActivity
import io.jadu.pages.domain.model.TodoModel
import io.jadu.pages.presentation.components.AddTodoBottomSheet
import io.jadu.pages.presentation.components.CustomDialog
import io.jadu.pages.presentation.components.CustomFab
import io.jadu.pages.presentation.components.HomeTopAppBar
import io.jadu.pages.presentation.home_widget.TodoWidgetWorker
import io.jadu.pages.presentation.viewmodel.TodoViewModel
import io.jadu.pages.ui.theme.ButtonBlue
import io.jadu.pages.ui.theme.LightGray
import io.jadu.pages.ui.theme.White
import io.jadu.pages.ui.theme.onBackground
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TodoPage(
    todoViewModel: TodoViewModel,
    navHostController: NavHostController,
    shouldOpenTodoPage: Boolean = false,
    bottomBarHeight: Dp = 0.dp
) {
    val context = LocalContext.current
    val todoList = todoViewModel.getAllTodo.collectAsState(initial = emptyList())
    val todos = todoList.value.sortedByDescending { it.date }
    val focusRequester = remember { FocusRequester() }
    val bottomSheetState = androidx.compose.material.rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )
    val keyboardController = LocalSoftwareKeyboardController.current
    var backPressHandled by remember { mutableStateOf(false) }
    val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.bear2))
    LaunchedEffect(bottomSheetState) {
        if(!bottomSheetState.isVisible){
            keyboardController?.hide()
        }
    }

    ComposableLifecycle { source, event ->
        if (event == Lifecycle.Event.ON_RESUME) {
            keyboardController?.hide()
        }
    }

    val scope = rememberCoroutineScope()
    var newTodoText by remember { mutableStateOf("") }
    if (!bottomSheetState.isVisible) keyboardController?.hide()


    fun showBottomSheet() {
        scope.launch {
            bottomSheetState.show()
            focusRequester.requestFocus()
        }
    }

    fun saveTodo() {
        if (newTodoText.isNotBlank()) {
            val newTodo = TodoModel(
                task = newTodoText,
                isTaskCompleted = false,
            )
            todoViewModel.addTodo(newTodo)
            scope.launch {
                todoViewModel.updateWidget(context)
            }
            newTodoText = ""
            scope.launch {
                bottomSheetState.hide()
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .positionAwareImePadding(),
        topBar = {
            HomeTopAppBar(
                onSearchClick = {},
                onMenuClick = {

                },
                onSearchTextChange = { searchText -> },
                title = "To-dos",
                isHome = false,
            )
        },
        contentWindowInsets = WindowInsets(
            top = 0.dp,
            bottom = 0.dp,
            left = 0.dp,
            right = 0.dp
        ),
        floatingActionButton = {
            if (!bottomSheetState.isVisible) {
                CustomFab(
                    onClick = { showBottomSheet() },
                    icon = Icons.Default.Add,
                    contentDescription = "Add TODO",
                    backgroundColor = ButtonBlue
                )
            }
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(horizontal = 4.dp),
        ) {
            if (todos.isEmpty()) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LottieAnimation(
                        lottieComposition,
                        isPlaying = true,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier
                            .size(300.dp)
                    )
                    Text(
                        text = "Wohoo! You got no Tasks to do!",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 8.dp, end = 8.dp)
                ) {
                    val incompleteTasks = todos.filter { !it.isTaskCompleted }
                    if (incompleteTasks.isNotEmpty()) {
                        incompleteTasks.forEach {
                            item {
                                TodoListItem(
                                    todoText = it.task.toString(),
                                    isTaskCompleted = it.isTaskCompleted,
                                    onClick = {
                                        scope.launch {
                                            todoViewModel.updateWidget(context)
                                        }
                                        todoViewModel.updateTodo(
                                            it.copy(isTaskCompleted = !it.isTaskCompleted)
                                        )
                                    },
                                    viewModel = todoViewModel,
                                    id = it.id,
                                    scope
                                )
                            }
                        }
                    }

                    val completedTasks = todos.filter { it.isTaskCompleted }
                    if (completedTasks.isNotEmpty()) {
                        item {
                            Text(
                                text = "Completed (${completedTasks.size})",
                                style = TextStyle(
                                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Normal
                                ),
                                modifier = Modifier.padding(
                                    top = 16.dp,
                                    start = 8.dp,
                                    end = 8.dp,
                                    bottom = 4.dp
                                )
                            )
                        }

                        completedTasks.forEach {
                            item {
                                TodoListItem(
                                    todoText = it.task.toString(),
                                    isTaskCompleted = it.isTaskCompleted,
                                    onClick = {
                                        todoViewModel.updateTodo(
                                            it.copy(isTaskCompleted = !it.isTaskCompleted)
                                        )
                                    },
                                    viewModel = todoViewModel,
                                    id = it.id,
                                    scope
                                )
                            }
                        }
                    }
                }

            }
        }

        ModalBottomSheetLayout(
            modifier = Modifier,
            sheetState = bottomSheetState,
            sheetGesturesEnabled = false,
            sheetBackgroundColor = onBackground,
            sheetContent = {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
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
                                    newTodoText = ""
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
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
                        onClick = {
                            saveTodo()
                        },
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
        ) {}
    }

    BackHandler(enabled = !backPressHandled && bottomSheetState.isVisible) {
        if (bottomSheetState.isVisible) {
            scope.launch {
                bottomSheetState.hide()
            }
        } else {
            keyboardController?.hide()
            backPressHandled = true
            navHostController.navigateUp()
        }
    }

}

fun Modifier.positionAwareImePadding() = composed {
    var consumePadding by remember { mutableStateOf(0) }
    onGloballyPositioned { coordinates ->
        val rootCoordinate = coordinates.findRootCoordinates()
        val bottom = coordinates.positionInWindow().y + coordinates.size.height

        consumePadding = (rootCoordinate.size.height - bottom).toInt()
    }
        .consumeWindowInsets(PaddingValues(bottom = (consumePadding / LocalDensity.current.density).dp))
        .imePadding()
}


@Composable
fun ParentComposable(todoViewModel: TodoViewModel) {
    var isBottomSheetVisible by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val activity = context as TodoActivity
    val coroutineScope = rememberCoroutineScope()
    var backPressHandled by remember { mutableStateOf(false) }
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0


    LaunchedEffect(isBottomSheetVisible) {
        if (!isBottomSheetVisible) {
            activity.finishAffinity()
        }
    }

    BackHandler(enabled = !backPressHandled) {
        backPressHandled = true
        coroutineScope.launch {
            isBottomSheetVisible = false
            activity.finishAffinity()
            backPressHandled = false
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        color = Color.Transparent
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .imePadding()
                .background(Color.Transparent)
        ) {
            if (isBottomSheetVisible) {
                AddTodoBottomSheet(
                    isVisible = isBottomSheetVisible,
                    onDismiss = {
                        isBottomSheetVisible = false
                    },
                    onSave = { todoText ->
                        todoViewModel.addTodo(
                            TodoModel(
                                task = todoText,
                                isTaskCompleted = false,
                            )
                        )
                        todoViewModel.updateWidget(context)
                        Toast.makeText(context, "Saved Successfully", Toast.LENGTH_SHORT).show()
                        isBottomSheetVisible = false
                        activity.finish()
                    }
                )
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
    id: Long,
    scope: CoroutineScope
) {
    var isDeleteClicked by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 8.dp)
            .clickable {
                onClick()
            },
        color = MaterialTheme.colorScheme.surfaceBright,
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
            androidx.compose.material3.RadioButton(
                selected = isTaskCompleted,
                onClick = { onClick() },
                modifier = Modifier.padding(end = 8.dp),
                colors = RadioButtonColors(
                    selectedColor = MaterialTheme.colorScheme.tertiaryContainer,
                    unselectedColor = MaterialTheme.colorScheme.outline,
                    disabledSelectedColor = Color.White,
                    disabledUnselectedColor = Color.White
                )
            )
            Text(
                modifier = Modifier.fillMaxWidth(0.8f),
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
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable {
                        isDeleteClicked = true
                    }
            )
        }

        if (isDeleteClicked) {
            CustomDialog(
                title = "Delete Notes",
                description = "Are you sure you want to delete the selected Todo?",
                onCancel = { isDeleteClicked = false },
                onConfirm = {
                    isDeleteClicked = false
                    scope.launch {
                        viewModel.updateWidget(context)
                    }
                    viewModel.deleteTodo(id)
                    Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

fun enqueueWidgetUpdateTask(context: Context) {
    WorkManager.getInstance(context).enqueue(
        OneTimeWorkRequestBuilder<TodoWidgetWorker>().build()
    )
}





