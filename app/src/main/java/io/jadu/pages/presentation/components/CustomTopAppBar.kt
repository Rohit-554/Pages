package io.jadu.pages.presentation.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.PushPin
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
import androidx.compose.material3.TopAppBarDefaults
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
    onScanClick: () -> Unit = {},
    isPinned: Boolean = false,
    isBackPressed: (Boolean) -> Unit = { _ -> }
) {
    var isMenuExpanded by remember { mutableStateOf(false) }
    var isPinnedClicked by remember { mutableStateOf(isPinned) }
    val context = androidx.compose.ui.platform.LocalContext.current
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
            IconButton(onClick = {
                isBackPressed(true)
                navHostController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = {
            if (isDrawMenu) {
                IconButton(
                    onClick = {
                        onScanClick()
                    }
                ) {
                    Icon(
                        imageVector =  Icons.Filled.DocumentScanner,
                        contentDescription = "scan",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                /*IconButton(
                    onClick = {
                        onPinClick()
                    }
                ) {
                    Icon(
                        imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                        contentDescription = "Pin",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }*/

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
                ) {
                    DropdownMenuItem(
                        text = {
                            CustomMenuItem(
                                title = "Pin",
                                icons = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            onPinClick()
                            isMenuExpanded = false
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
                            Toast.makeText(context, "Coming Soon!", Toast.LENGTH_SHORT).show()
                            isMenuExpanded = false
                        }
                    )
                }
            }
        },



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

