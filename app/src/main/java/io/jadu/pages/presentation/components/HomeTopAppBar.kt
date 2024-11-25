package io.jadu.pages.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.jadu.pages.core.PreferencesManager
import io.jadu.pages.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    onSearchClick: () -> Unit,
    onMenuClick: () -> Unit,
    onSearchTextChange: (String) -> Unit,
    title: String = "Hi Rohit!",
    isHome: Boolean = true,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    var isSearchMode by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var searchText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val name by remember { mutableStateOf(preferencesManager.getName() ?: "") }
    LaunchedEffect(isSearchMode) {
        if (isSearchMode) {
            focusRequester.requestFocus()
        }
    }
    MediumTopAppBar(
        title = {
            if (isSearchMode) {
                TextField(
                    modifier = Modifier.focusRequester(focusRequester),
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
                if(isHome){
                    Text(
                        modifier = Modifier.fillMaxWidth(0.7f),
                        text = if (name.isNotEmpty()) "Hey, $name!" else "Hey, Toby Doodle!",
                        style = TextStyle(
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis

                    )
                }else{
                    Text(
                        modifier = Modifier.fillMaxWidth(0.7f),
                        text = "TO-DOs",
                        style = TextStyle(
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis

                    )
                }


            }
        },
        actions = {
            IconButton(onClick = onMenuClick) {
                Icon(imageVector = Icons.Filled.Settings, contentDescription = "Menu")
            }
            if (isHome) {
               /* if (isSearchMode) {
                    IconButton(onClick = {
                        isSearchMode = false
                        searchText = ""
                        onSearchTextChange("")
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

                }*/

            }
        },
        scrollBehavior = scrollBehavior
    )
}

