package io.jadu.pages.presentation.screens.draw

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.smarttoolfactory.composedrawingapp.DrawMode
import io.jadu.pages.domain.model.PathProperties
import io.jadu.pages.presentation.components.CustomTopAppBar
import io.jadu.pages.presentation.screens.draw.gesture.MotionEvent
import io.jadu.pages.presentation.screens.draw.gesture.dragMotionEvent
import io.jadu.pages.presentation.screens.draw.menu.DrawingPropertiesMenu
import io.jadu.pages.ui.theme.backgroundColor

@Composable
fun DrawingApp(paddingValues: PaddingValues,navHostController: NavHostController) {

    val context = LocalContext.current
    val paths = remember { mutableStateListOf<Pair<Path, PathProperties>>() }
    val pathsUndone = remember { mutableStateListOf<Pair<Path, PathProperties>>() }
    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }
    var drawMode by remember { mutableStateOf(DrawMode.Draw) }
    var currentPath by remember { mutableStateOf(Path()) }
    var currentPathProperty by remember { mutableStateOf(PathProperties()) }
    val canvasText = remember { StringBuilder() }
    val paint = remember {
        Paint().apply {
            textSize = 40f
            color = Color.Black.toArgb()
        }
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Draw",
                navHostController = navHostController,
                )
        }
    )  { padValue->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padValue)
                .background(backgroundColor)
        ) {

            val drawModifier = Modifier
                .padding(8.dp)
                .shadow(1.dp)
                .fillMaxWidth()
                .weight(1f)
                .background(Color.White)
                //            .background(getRandomColor())
                .dragMotionEvent(
                    onDragStart = { pointerInputChange ->
                        motionEvent = MotionEvent.Down
                        currentPosition = pointerInputChange.position
                        pointerInputChange.consumeDownChange()

                    },
                    onDrag = { pointerInputChange ->
                        motionEvent = MotionEvent.Move
                        currentPosition = pointerInputChange.position

                        if (drawMode == DrawMode.Touch) {
                            val change = pointerInputChange.positionChange()
                            println("DRAG: $change")
                            paths.forEach { entry ->
                                val path: Path = entry.first
                                path.translate(change)
                            }
                            currentPath.translate(change)
                        }
                        pointerInputChange.consumePositionChange()

                    },
                    onDragEnd = { pointerInputChange ->
                        motionEvent = MotionEvent.Up
                        pointerInputChange.consumeDownChange()
                    }
                )

            Canvas(modifier = drawModifier) {

                when (motionEvent) {

                    MotionEvent.Down -> {
                        if (drawMode != DrawMode.Touch) {
                            currentPath.moveTo(currentPosition.x, currentPosition.y)
                        }

                        previousPosition = currentPosition

                    }

                    MotionEvent.Move -> {

                        if (drawMode != DrawMode.Touch) {
                            currentPath.quadraticBezierTo(
                                previousPosition.x,
                                previousPosition.y,
                                (previousPosition.x + currentPosition.x) / 2,
                                (previousPosition.y + currentPosition.y) / 2

                            )
                        }

                        previousPosition = currentPosition
                    }

                    MotionEvent.Up -> {
                        if (drawMode != DrawMode.Touch) {
                            currentPath.lineTo(currentPosition.x, currentPosition.y)
                            paths.add(Pair(currentPath, currentPathProperty))

                            currentPath = Path()

                            currentPathProperty = PathProperties(
                                strokeWidth = currentPathProperty.strokeWidth,
                                color = currentPathProperty.color,
                                strokeCap = currentPathProperty.strokeCap,
                                strokeJoin = currentPathProperty.strokeJoin,
                                eraseMode = currentPathProperty.eraseMode
                            )
                        }
                        pathsUndone.clear()
                        currentPosition = Offset.Unspecified
                        previousPosition = currentPosition
                        motionEvent = MotionEvent.Idle
                    }

                    else -> Unit
                }

                with(drawContext.canvas.nativeCanvas) {

                    val checkPoint = saveLayer(null, null)

                    paths.forEach {

                        val path = it.first
                        val property = it.second

                        if (!property.eraseMode) {
                            drawPath(
                                color = property.color,
                                path = path,
                                style = Stroke(
                                    width = property.strokeWidth,
                                    cap = property.strokeCap,
                                    join = property.strokeJoin
                                )
                            )
                        } else {

                            // Source
                            drawPath(
                                color = Color.Transparent,
                                path = path,
                                style = Stroke(
                                    width = currentPathProperty.strokeWidth,
                                    cap = currentPathProperty.strokeCap,
                                    join = currentPathProperty.strokeJoin
                                ),
                                blendMode = BlendMode.Clear
                            )
                        }
                    }

                    if (motionEvent != MotionEvent.Idle) {

                        if (!currentPathProperty.eraseMode) {
                            drawPath(
                                color = currentPathProperty.color,
                                path = currentPath,
                                style = Stroke(
                                    width = currentPathProperty.strokeWidth,
                                    cap = currentPathProperty.strokeCap,
                                    join = currentPathProperty.strokeJoin
                                )
                            )
                        } else {
                            drawPath(
                                color = Color.Transparent,
                                path = currentPath,
                                style = Stroke(
                                    width = currentPathProperty.strokeWidth,
                                    cap = currentPathProperty.strokeCap,
                                    join = currentPathProperty.strokeJoin
                                ),
                                blendMode = BlendMode.Clear
                            )
                        }
                    }
                    restoreToCount(checkPoint)
                }
            }

            DrawingPropertiesMenu(
                modifier = Modifier
                    .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                    .shadow(1.dp, RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(4.dp),
                pathProperties = currentPathProperty,
                drawMode = drawMode,
                onUndo = {
                    if (paths.isNotEmpty()) {

                        val lastItem = paths.last()
                        val lastPath = lastItem.first
                        val lastPathProperty = lastItem.second
                        paths.remove(lastItem)

                        pathsUndone.add(Pair(lastPath, lastPathProperty))

                    }
                },
                onRedo = {
                    if (pathsUndone.isNotEmpty()) {

                        val lastPath = pathsUndone.last().first
                        val lastPathProperty = pathsUndone.last().second
                        pathsUndone.removeAt(pathsUndone.lastIndex)
                        paths.add(Pair(lastPath, lastPathProperty))
                    }
                },
                onPathPropertiesChange = {
                    motionEvent = MotionEvent.Idle
                },
                onDrawModeChanged = {
                    motionEvent = MotionEvent.Idle
                    drawMode = it
                    currentPathProperty.eraseMode = (drawMode == DrawMode.Erase)
                }
            )
        }
    }
}


private fun DrawScope.drawText(text: String, x: Float, y: Float, paint: Paint) {

    val lines = text.split("\n")
    val nativeCanvas = drawContext.canvas.nativeCanvas

    lines.indices.withIndex().forEach { (posY, i) ->
        nativeCanvas.drawText(lines[i], x, posY * 40 + y, paint)
    }
}
