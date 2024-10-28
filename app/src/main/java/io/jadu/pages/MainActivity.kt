package io.jadu.pages

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.jadu.pages.presentation.screens.HomePage
import io.jadu.pages.ui.theme.PagesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PagesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        Modifier.padding(innerPadding)
                    ) {  HomePage() }
                }
            }
        }
    }
}



/*
@Composable
fun NotesScreen(viewModel: NotesViewModel = hiltViewModel()) {
    // Collect the notes from the ViewModel
    val notesState by viewModel.notes.collectAsState()

    // Display notes in a LazyColumn
    LazyColumn {
        items(notesState) { note ->
            Text(text = note.title)
            // Add more UI components to display note details
        }
    }

    // Example of fetching notes when the screen is launched
    LaunchedEffect(Unit) {
        viewModel.getNotesPaginated(limit = 10, offset = 0) // Adjust limit and offset as needed
    }
}*/
