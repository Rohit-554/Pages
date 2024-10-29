package io.jadu.pages.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import io.jadu.pages.presentation.components.CustomTopAppBar

@Composable
fun ProfilePage(navHostController: NavHostController) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Profile",
                navHostController = navHostController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {

        }
    }
}