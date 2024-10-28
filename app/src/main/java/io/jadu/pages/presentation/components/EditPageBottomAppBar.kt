package io.jadu.pages.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.unit.dp
import io.jadu.pages.ui.theme.ButtonBlue
import io.jadu.pages.ui.theme.LightGray

@Composable
fun EditPageBottomAppBar() {
    var isExpanded by remember { mutableStateOf(false) }

    BottomAppBar(
        containerColor = Color.Transparent,
        actions = {},
        floatingActionButton = {
            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .background(
                        color = LightGray,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 2.dp, vertical = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AnimatedVisibility(visible = isExpanded) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CustomFab(
                                onClick = {},
                                icon = Icons.Filled.Image,
                                contentDescription = "Camera",
                            )
                            CustomFab(
                                onClick = {},
                                icon = Icons.Filled.ColorLens,
                                contentDescription = "Edit",
                            )
                        }
                    }

                    // Main FAB
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
                            if (isExpanded) Icons.Filled.Close else Icons.Filled.Add,
                            contentDescription = if (isExpanded) "Close menu" else "Open menu"
                        )
                    }
                }
            }
        }
    )
}