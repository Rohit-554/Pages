package io.jadu.pages.presentation.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import io.jadu.pages.core.PreferencesManager
import io.jadu.pages.ui.theme.White
import java.lang.reflect.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    onSearchClick: () -> Unit,
    onMenuClick: () -> Unit,
    onSearchTextChange: (String) -> Unit,
    title: String = "Hi Rohit!",
    isHome: Boolean = true
) {
    var isSearchMode by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    var name by remember { mutableStateOf(preferencesManager.getName() ?: "Master") }
    TopAppBar(
        title = {
            if (isSearchMode) {
                TextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                        onSearchTextChange(it) // Pass updated text to HomePage
                    },
                    placeholder = { Text("Search notes...") },
                    textStyle = TextStyle(
                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    ),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = White
                    )
                )
            } else {
                Text(
                    text = "Hi! ${name}",
                    style = TextStyle(
                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                    )
                )
            }
        },
        actions = {
            if (isHome) {
                if (isSearchMode) {
                    IconButton(onClick = {
                        isSearchMode = false
                        searchText = ""
                        onSearchTextChange("") // Clear search text in HomePage
                    }) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close Search")
                    }
                } else {
                    IconButton(onClick = {
                        isSearchMode = true
                        onSearchClick()
                    }) {
                        Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = onMenuClick) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Menu")
                    }
                }
            }
        },
    )
}

