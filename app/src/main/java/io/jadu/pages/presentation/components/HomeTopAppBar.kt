package io.jadu.pages.presentation.components

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(onSearchClick: () -> Unit, onMenuClick: () -> Unit, title:String = "Hi Rohit!", isHome:Boolean = true) {
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
        actions = {
            if(isHome){
                IconButton(onClick = onSearchClick) {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
                }
                IconButton(onClick = onMenuClick) {
                    Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu")
                }
            }

        },
    )
}
