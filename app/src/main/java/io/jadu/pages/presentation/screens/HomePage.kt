package io.jadu.pages.presentation.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import io.jadu.pages.R
import io.jadu.pages.core.noRippleClickable
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.presentation.components.CustomDialog
import io.jadu.pages.presentation.components.CustomFab
import io.jadu.pages.presentation.components.HomeTopAppBar
import io.jadu.pages.presentation.components.NoteCard
import io.jadu.pages.presentation.navigation.NavigationItem
import io.jadu.pages.presentation.viewmodel.NotesViewModel
import io.jadu.pages.ui.theme.ButtonBlue
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    viewModel: NotesViewModel,
    navHostController: NavHostController,
    onCardSelected: (Boolean) -> Unit,
    note: List<Notes>,
) {
    val context = LocalContext.current
    // val notes = viewModel.notes.collectAsState(initial = emptyList()).value
    val lazyStaggeredGridState = rememberLazyStaggeredGridState()
    var offset by remember { mutableIntStateOf(0) }
    val limit = 15
    var isLoading by remember { mutableStateOf(false) }
    val notePositions = remember { mutableStateListOf<Rect>() }
    val selectedNotes = remember { mutableStateListOf<Notes>() }
    val updatedNotes = viewModel.updatedNotes.collectAsState(initial = emptyList()).value
    var isMenuExpanded by remember { mutableStateOf(false) }
    var multipleSelectedForDelete by remember { mutableStateOf(false) }
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    var backPressHandled by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val isDeletePressed = remember { mutableStateOf(false) }
    val pagingNotes = viewModel.notesFlow.collectAsLazyPagingItems()
    val notes = pagingNotes.itemSnapshotList.items                              //todo make sure to check paging issue for searching
    val isSearchedClicked = remember { mutableStateOf(false) }
    val isSearching by viewModel.isSearching.collectAsState()
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.sleepingbear))
    val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.bear))
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    LaunchedEffect(selectedNotes) {
        onCardSelected(selectedNotes.isNotEmpty())
        multipleSelectedForDelete = selectedNotes.isNotEmpty()
    }
    var isBearTouched by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(2000)
        isLoading = false
    }


    // Function to delete selected notes
    fun deleteSelectedNotes() {
        selectedNotes.forEach { note ->
            viewModel.deleteNotes(note.id)
        }
        selectedNotes.clear()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (selectedNotes.isNotEmpty()) {
                TopAppBar(
                    title = {
                        Text(
                            text = "${selectedNotes.size} Selected",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    actions = {
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .background(
                                    MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedNotes.clear()
                                    selectedNotes.addAll(notes)
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                modifier = Modifier.padding(4.dp),
                                text = "Select all",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                            )
                        }
                    }
                )
            } else {
                HomeTopAppBar(
                    onSearchClick = {
                        isSearchedClicked.value = true
                    },
                    onMenuClick = {
                        navHostController.navigate(NavigationItem.SettingsPage.route)
                    },
                    onSearchTextChange = { searchText ->
                        viewModel.onSearchTextChanged(searchText)
                    },
                    scrollBehavior = scrollBehavior,
                )
            }
        },
        contentWindowInsets = WindowInsets(
            top = 0.dp,
            bottom = 0.dp
        ),
        floatingActionButton = {
            Row {
                if (selectedNotes.isNotEmpty()) {
                    multipleSelectedForDelete = true
                    val allSelectedPinned = selectedNotes.all { it.isPinned }
                    CustomFab(
                        onClick = {
                            selectedNotes.forEach { note ->
                                viewModel.updateNotes(
                                    note.title,
                                    note.description,
                                    note.imageUri,
                                    note.drawingPaths,
                                    note.id,
                                    note.color,
                                    !allSelectedPinned
                                )
                            }
                            selectedNotes.clear()
                            multipleSelectedForDelete = false
                            Toast.makeText(
                                context,
                                if (allSelectedPinned) "Unpinned Successfully" else "Pinned Sucessfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        icon = if (allSelectedPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                        contentDescription = "Pinned notes",
                        backgroundColor = MaterialTheme.colorScheme.onSurface,
                        tintColor = Color.Black
                    )
                    CustomFab(
                        onClick = {
                            isDeletePressed.value = true
                        },
                        icon = Icons.Outlined.Delete,
                        contentDescription = "Delete Note",
                        backgroundColor = MaterialTheme.colorScheme.onSurface,
                        tintColor = MaterialTheme.colorScheme.errorContainer
                    )
                } else {
                    CustomFab(
                        onClick = { navHostController.navigate(NavigationItem.CreateNotes.route) },
                        icon = Icons.Default.Add,
                        contentDescription = "Add Note",
                        backgroundColor = ButtonBlue
                    )
                }

                if (isDeletePressed.value) {
                    CustomDialog(
                        title = "Delete Notes",
                        description = "Are you sure you want to delete the selected notes?",
                        onCancel = { isDeletePressed.value = false },
                        onConfirm = {
                            isDeletePressed.value = false
                            multipleSelectedForDelete = false
                            deleteSelectedNotes()
                            Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT)
                                .show()
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        if (isMenuExpanded) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.TopEnd
            ) {
                FloatingActionMenu(isMenuExpanded, navHostController) { isMenuExpanded = false }
            }
        }
        if (isSearching) {
            Log.d("isSearchingxx", "isSearching: $isSearching")
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp),
            ) {
                if (notes.isNotEmpty()) {
                    val pinnedNotes = notes.filter { it.isPinned }
                    val unpinnedNotes = notes.filter { !it.isPinned }
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        state = lazyStaggeredGridState,
                        modifier = Modifier.fillMaxSize(),
                        verticalItemSpacing = 8.dp,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (pinnedNotes.isNotEmpty()) {
                            itemsIndexed(pinnedNotes) { index, note ->
                                ShowNotes(
                                    note,
                                    navHostController,
                                    selectedNotes,
                                    multipleSelectedForDelete,
                                    viewModel
                                )
                            }
                        }
                        if (unpinnedNotes.isNotEmpty()) {
                            itemsIndexed(unpinnedNotes) { index, note ->
                                ShowNotes(
                                    note,
                                    navHostController,
                                    selectedNotes,
                                    multipleSelectedForDelete,
                                    viewModel
                                )
                            }
                        }
                        if (selectedNotes.isNotEmpty()) {
                            item {
                                Column(
                                    Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                }

                            }
                        }

                        pagingNotes.apply {
                            when {
                                loadState.refresh is LoadState.Loading -> {
                                    item {
                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        )
                                    }
                                }

                                loadState.append is LoadState.Loading -> {
                                    item {
                                        CircularProgressIndicator(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        )
                                    }
                                }

                                loadState.append is LoadState.Error -> {
                                    item {
                                        Text(
                                            text = "Error loading more notes. Please try again.",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .wrapContentHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isBearTouched) {
                            LottieAnimation(
                                lottieComposition,
                                isPlaying = true,
                                iterations = LottieConstants.IterateForever,
                                modifier = Modifier
                                    .size(250.dp)
                                    .noRippleClickable {
                                        isBearTouched = false
                                    }
                            )
                            LottieSection(
                                text1 = "I warned you !",
                                text2 = "Now, Touch him again to make him sleep"
                            )
                        } else {
                            LottieAnimation(
                                composition,
                                isPlaying = true,
                                iterations = LottieConstants.IterateForever,
                                modifier = Modifier
                                    .size(300.dp)
                                    .noRippleClickable {
                                        isBearTouched = true
                                    }
                            )
                            LottieSection(
                                text1 = "Nothing found here!",
                                text2 = "& Beware of Bear! don't touch him"
                            )
                        }
                    }
                }
            }
        }
    }

    BackHandler(enabled = !backPressHandled || multipleSelectedForDelete) {
        if (multipleSelectedForDelete) {
            multipleSelectedForDelete = false
            selectedNotes.clear()
        } else {
            backPressHandled = true
            /*navHostController.currentBackStackEntry?.let {
                android.util.Log.d("NavBackStack", "Route: ${it.destination.route}")
            }*/
            coroutineScope.launch {
                awaitFrame()
                onBackPressedDispatcher?.onBackPressed()
                backPressHandled = false
            }
        }
    }
}

@Composable
private fun LottieSection(
    text1: String,
    text2: String
) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        modifier = Modifier.padding(bottom = 10.dp),
        text = text1,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center
    )
    Text(
        text = text2,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center
    )
}

@Composable
fun ShowNotes(
    note: Notes,
    navHostController: NavHostController,
    selectedNotes: MutableList<Notes>,
    multipleSelectedForDelete: Boolean,
    viewModel: NotesViewModel,
) {
    NoteCard(
        note = note,
        navHostController = navHostController,
        onLongPress = { selectedNote ->
            selectedNotes.clear()
            selectedNotes.add(selectedNote)
        },
        onClick = {
            if (selectedNotes.contains(note)) {
                selectedNotes.remove(note)
            } else {
                selectedNotes.add(note)
            }
        },
        isSelected = selectedNotes.contains(note),
        multipleSelectedForDelete = multipleSelectedForDelete,
        viewmodel = viewModel
    )
}

@Composable
fun FloatingActionMenu(
    isMenuExpanded: Boolean,
    navHostController: NavHostController,
    onDismiss: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier.padding(16.dp)
    ) {
        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = onDismiss
        ) {
            DropdownMenuItem(
                text = { Text("Profile", style = MaterialTheme.typography.bodyLarge) },
                onClick = {
                    navHostController.navigate(NavigationItem.ProfilePage.route)
                    onDismiss()
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
            DropdownMenuItem(
                text = { Text("About", style = MaterialTheme.typography.bodyLarge) },
                onClick = {
                    navHostController.navigate(NavigationItem.AboutPage.route)
                    onDismiss()
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "About",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            )
        }
    }
}

fun findTargetIndex(
    notes: List<Notes>,
    draggedNote: Notes,
    dragAmount: Offset,
    notePositions: List<Rect>
): Int {
    val draggedIndex = notes.indexOf(draggedNote)
    if (draggedIndex == -1) return draggedIndex

    val draggedNoteRect = notePositions[draggedIndex]

    val draggedRectWithOffset = Rect(
        draggedNoteRect.left + dragAmount.x,
        draggedNoteRect.top + dragAmount.y,
        draggedNoteRect.right + dragAmount.x,
        draggedNoteRect.bottom + dragAmount.y
    )

    var closestIndex = draggedIndex
    var minDistance = Float.MAX_VALUE

    for (i in notes.indices) {
        if (i == draggedIndex) continue

        val otherNoteRect = notePositions[i]

        val distance = draggedRectWithOffset.center.distanceTo(otherNoteRect.center)

        if (distance < minDistance) {
            minDistance = distance
            closestIndex = i
        }
    }

    return closestIndex
}

val Rect.center: Offset
    get() = Offset((left + right) / 2, (top + bottom) / 2)


fun Offset.distanceTo(other: Offset): Float {
    val dx = x - other.x
    val dy = y - other.y
    return sqrt(dx * dx + dy * dy)
}

fun parseColor(colorString: String): Color {
    val colorValues = colorString
        .substringAfter("Color(")
        .substringBefore(", sRGB")
        .split(",")
        .map { it.trim().toFloat() }
    return Color(colorValues[0], colorValues[1], colorValues[2], colorValues[3])
}

fun stringToUri(input: String): Uri {
    return Uri.parse(input)
}