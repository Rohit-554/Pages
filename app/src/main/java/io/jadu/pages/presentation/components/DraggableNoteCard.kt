package io.jadu.pages.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavHostController
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.presentation.screens.findTargetIndex
import kotlin.math.roundToInt

/*@Composable
fun DraggableNoteCard(
    note: Notes,
    notes: List<Notes>,
    onSwapNotes: (Notes, Notes) -> Unit,
    onLongPress: (Notes) -> Unit,
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    notePositions: SnapshotStateList<Rect>,
    isSelected: Boolean
) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var dragNoteIndex by remember { mutableStateOf(-1) }

    Box(
        modifier = modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                        dragNoteIndex = notes.indexOf(note)
                    },
                    onDragEnd = {
                        isDragging = false
                        offsetX = 0f
                        offsetY = 0f
                    },
                    onDragCancel = {
                        isDragging = false
                        offsetX = 0f
                        offsetY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y

                        val targetNoteIndex = findTargetIndex(notes, note, Offset(offsetX, offsetY), notePositions)

                        if (targetNoteIndex != -1 && targetNoteIndex != dragNoteIndex) {
                            onSwapNotes(note, notes[targetNoteIndex])
                            dragNoteIndex = targetNoteIndex
                        }
                    }
                )

                detectTapGestures(
                    onLongPress = {
                        onLongPress(note)
                    }
                )
            }
            .background(if (isSelected) Color.LightGray else Color.Transparent)
    ) {
        NoteCard(
            note = note,
            navHostController = navHostController,
            onLongPress = {},
            isSelected = false,
            onClick = {},
            multipleSelectedForDelete = false,
            viewmodel = viewModel
        )
    }
}*/
