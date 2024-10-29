package io.jadu.pages.presentation.screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.presentation.components.CustomTopAppBar
import io.jadu.pages.presentation.components.DraggableNoteCard
import io.jadu.pages.presentation.viewmodel.NotesViewModel
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
    val selectedNotes by remember { mutableStateOf(mutableSetOf<Notes>()) }
    LaunchedEffect(key1 = notes) {
        Log.d("HomePagereload", "Notes: $notes")
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


    Scaffold(
        topBar = {
            CustomTopAppBar(onSearchClick = {}) { }
        },
        contentWindowInsets = WindowInsets(
            top = 0.dp,
            bottom = 0.dp
        )
    ) { innerPadding ->
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
                            DraggableNoteCard(
                                note = note,
                                notes = notes,
                                onSwapNotes = { draggedNote, targetNote ->
                                    swapNotes(draggedNote, targetNote)
                                },
                                onLongPress = { handleLongPress(note) }, // Pass long press handler
                                modifier = Modifier.fillMaxWidth(),
                                navHostController = navHostController,
                                notePositions = notePositions,
                                isSelected = selectedNotes.contains(note) // Highlight based on selection
                            )
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

                    if (isLoading) {
                        item {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            } else {
                Text(text = "No notes available.", style = MaterialTheme.typography.bodyLarge)
            }

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