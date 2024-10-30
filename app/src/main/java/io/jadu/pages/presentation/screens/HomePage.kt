package io.jadu.pages.presentation.screens

import android.net.Uri
import android.util.Log
import android.widget.Space
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.presentation.components.CustomFab
import io.jadu.pages.presentation.components.HomeTopAppBar
import io.jadu.pages.presentation.components.DraggableNoteCard
import io.jadu.pages.presentation.components.NoteCard
import io.jadu.pages.presentation.navigation.NavigationItem
import io.jadu.pages.presentation.viewmodel.NotesViewModel
import io.jadu.pages.ui.theme.ButtonBlue
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(viewModel: NotesViewModel, navHostController: NavHostController) {
    val context = LocalContext.current
    val notes = viewModel.notes.collectAsState(initial = emptyList()).value
    val lazyStaggeredGridState = rememberLazyStaggeredGridState()
    var offset by remember { mutableIntStateOf(0) }
    val limit = 15
    var isLoading by remember { mutableStateOf(false) }
    val notePositions = remember { mutableStateListOf<Rect>() }
    val selectedNotes = remember { mutableStateListOf<Notes>() }
    val updatedNotes = viewModel.updatedNotes.collectAsState(initial = emptyList()).value
    var isMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = notes) {
        viewModel.getNotesPaginated(limit, offset)
    }

    fun swapNotes(note1: Notes, note2: Notes) {
        val index1 = notes.indexOf(note1)
        val index2 = notes.indexOf(note2)

        if (index1 != -1 && index2 != -1) {
            viewModel.swapNotes(index1, index2)
        }
    }

    fun handleLongPress(note: Notes) {
        if (selectedNotes.contains(note)) {
            selectedNotes.remove(note)
        } else {
            selectedNotes.add(note)
        }
    }

    // Function to delete selected notes
    fun deleteSelectedNotes() {
        selectedNotes.forEach { note ->
            viewModel.deleteNotes(note.id) // Call ViewModel to delete
        }
        selectedNotes.clear() // Clear the selection after deletion
    }

    Log.d("HomePage", "Notes: ${updatedNotes.size}")

    Scaffold(
        topBar = {
            HomeTopAppBar(
                onSearchClick = {},
                onMenuClick = {
                    isMenuExpanded = !isMenuExpanded
                },
                onSearchTextChange = { searchText ->
                    if(searchText.isEmpty()){
                        viewModel.getNotesPaginated(limit, offset)
                    }
                    viewModel.searchNotes(searchText)
                },
            )
        },
        contentWindowInsets = WindowInsets(
            top = 0.dp,
            bottom = 0.dp
        ),
        floatingActionButton = {
            Column() {
                if(selectedNotes.isNotEmpty()) {
                    CustomFab(
                        onClick = { deleteSelectedNotes() },
                        icon = Icons.Default.Delete,
                        contentDescription = "Delete Note",
                        backgroundColor = Color.Red
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                CustomFab(
                    onClick = { navHostController.navigate(NavigationItem.CreateNotes.route) },
                    icon = Icons.Default.Add,
                    contentDescription = "Add Note",
                    backgroundColor = ButtonBlue
                )

            }

        }
    ) { innerPadding ->
        if(isMenuExpanded){
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.TopEnd) {
                FloatingActionMenu(isMenuExpanded,navHostController) { isMenuExpanded = false}
            }
        }
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            if (notes.isNotEmpty()) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    state = lazyStaggeredGridState,
                    modifier = Modifier.fillMaxSize(),
                    verticalItemSpacing = 8.dp,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(notes) { index, note ->
                        Box(
                            modifier = Modifier
                                .onGloballyPositioned { layoutCoordinates ->
                                    val rect = layoutCoordinates.boundsInParent()
                                    if (notePositions.size > index) {
                                        notePositions[index] = rect
                                    } else {
                                        notePositions.add(rect)
                                    }
                                }
                        ) {
                            NoteCard(note = note, navHostController = navHostController, onLongPress = { selectednote->
                                if (selectedNotes.contains(selectednote)) {
                                    selectedNotes.remove(selectednote)
                                } else {
                                    Log.d("HomePage", "SelectedNote: $selectednote")
                                    selectedNotes.add(selectednote)
                                }
                            }, isSelected = selectedNotes.contains(note))

                            /*DraggableNoteCard(
                                note = note,
                                notes = notes,
                                onSwapNotes = { draggedNote, targetNote ->
                                    swapNotes(draggedNote, targetNote)
                                },
                                onLongPress = { handleLongPress(note) },
                                modifier = Modifier.fillMaxWidth(),
                                navHostController = navHostController,
                                notePositions = notePositions,
                                isSelected = selectedNotes.contains(note)
                            )*/
                        }
                    }
                    item {
                        LaunchedEffect(lazyStaggeredGridState) {
                            snapshotFlow { lazyStaggeredGridState.layoutInfo.visibleItemsInfo }
                                .collect { visibleItems ->
                                    if (visibleItems.isNotEmpty() &&
                                        visibleItems.last().index == notes.size - 1 && !isLoading
                                    ) {
                                        isLoading = true
                                        offset += limit
                                        viewModel.getNotesPaginated(limit, offset)
                                        isLoading = false
                                    }
                                }
                        }
                    }
                    if(selectedNotes.isNotEmpty()){
                        Log.d("HomePagex", "SelectedNotes: $selectedNotes")
                        item {
                            Column(
                                Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                            }

                        }
                    }

                    if (isLoading) {
                        item {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            } else {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = " You haven't created any note yet \uD83D\uDE44, Click the + Icon to get started",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

        }
    }
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