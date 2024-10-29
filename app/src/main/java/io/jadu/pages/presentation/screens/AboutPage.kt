package io.jadu.pages.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import io.jadu.pages.presentation.components.CustomTopAppBar

@Composable
fun AboutPage(navHostController: NavHostController) {
    androidx.compose.material3.Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "About",
                navHostController = navHostController
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Pages",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = """
                Welcome to the Pages! This application is designed to help you keep track of your thoughts, ideas, and important tasks in a simple and intuitive way. 
                With features like creating, editing, and deleting notes, you can manage your information efficiently. 
                Your notes are stored securely and persistently, thanks to the use of local storage. 
                Whether you need a quick note for a grocery list or a more detailed entry for your thoughts, this app provides the flexibility you need. 
                Thank you for using our Notes App!
                """.trimIndent(),
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Row(
                        modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Made With ❤️ by Jadu",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Version 1.0",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }
        }
    }
}
