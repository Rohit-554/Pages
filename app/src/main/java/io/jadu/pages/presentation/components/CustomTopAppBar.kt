package io.jadu.pages.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.jadu.pages.ui.theme.TickColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    title: String,
    navHostController: NavHostController,
    isDrawMenu: Boolean = false,
    onSaveClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onPinClick : () -> Unit = {},
) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = title,
                style = TextStyle(
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = { navHostController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            if (isDrawMenu) {

                IconButton(
                    onClick = { onSaveClick() }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Edit",
                        modifier = Modifier.size(24.dp),
                        tint = TickColor
                    )
                }


                IconButton(onClick = { isMenuExpanded = true }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Menu"
                    )
                }
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false },
                    offset = DpOffset(x = (-16).dp, y = 0.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            CustomMenuItem(
                                title = "Pin",
                                icons = Icons.Outlined.PushPin,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            isMenuExpanded = false
                            onPinClick()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            CustomMenuItem(
                                title = "Delete",
                                icons = Icons.Filled.DeleteOutline,
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            isMenuExpanded = false
                            onDeleteClick()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            CustomMenuItem(
                                title = "Share",
                                icons = Icons.Outlined.Share,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            isMenuExpanded = false

                        }
                    )


                }
            }
        }
    )
}

@Composable
private fun CustomMenuItem(title: String, icons:ImageVector, color:Color = Color.Black) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StyledText(title)
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = icons,
            tint = color,
            contentDescription = "Edit",
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun StyledText(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize
        )
    )
}

