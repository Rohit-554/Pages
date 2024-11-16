package io.jadu.pages.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.content.contentReceiver
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.unit.dp
import io.jadu.pages.ui.theme.ButtonBlue
import io.jadu.pages.ui.theme.LightGray
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EditPageBottomAppBar(
    onImagePickClick: () -> Unit,
    onColorPickClick: () -> Unit,
    onDrawClick: () -> Unit,
    scrollState: ScrollState = remember { ScrollState(0) }
) {
    var isExpanded by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    /*LaunchedEffect(Unit) {
        delay(250)
        scope.launch {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }*/
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(16.dp),

                )
            .fillMaxWidth()
    ) {

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnimatedVisibility(visible = isExpanded) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomFab(
                        onClick = {
                            onImagePickClick()
                        },
                        icon = Icons.Filled.Image,
                        contentDescription = "Camera",
                    )
                    CustomFab(
                        onClick = {
                            onDrawClick()
                        },
                        icon = Icons.Filled.Draw,
                        contentDescription = "Draw",
                    )
                    CustomFab(
                        onClick = {
                            onColorPickClick()
                        },
                        icon = Icons.Filled.ColorLens,
                        contentDescription = "Edit",
                    )
                }
            }
            // Main FAB
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(
                        brush = Brush.linearGradient(
                            colors = listOf(Color.Magenta, Color.Cyan, Color.Blue)
                        ),
                        size = size,
                        style = Stroke(width = 4.dp.toPx()),
                        cornerRadius = CornerRadius(16.dp.toPx())
                    )
                }
                FloatingActionButton(
                    modifier = Modifier.background(
                        color = LightGray,
                        shape = RoundedCornerShape(50.dp),
                    ),
                    onClick = { isExpanded = !isExpanded },
                    containerColor = if (isExpanded) Color.DarkGray else ButtonBlue,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                ) {
                    Icon(
                        if (isExpanded) Icons.Filled.Close else Icons.Filled.Image,
                        contentDescription = if (isExpanded) "Close menu" else "Open menu"
                    )
                }
            }

        }
    }
}