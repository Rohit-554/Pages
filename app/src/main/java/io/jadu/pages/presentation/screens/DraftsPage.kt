package io.jadu.pages.presentation.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.presentation.components.CustomTopAppBar
import io.jadu.pages.presentation.viewmodel.NotesViewModel

@Composable
fun DraftsPage(navHostController: NavHostController, viewModel: NotesViewModel) {

    val pagingNotes = viewModel.notesFlow.collectAsLazyPagingItems()
    val notes = pagingNotes.itemSnapshotList.items.filter { !it.isNoteSaved }
    val lazyStaggeredGridState = rememberLazyStaggeredGridState()
    val selectedNotes = remember { mutableStateListOf<Notes>() }
    var multipleSelectedForDelete by remember { mutableStateOf(false) }

   /* Scaffold(
        topBar = {
            CustomTopAppBar(
                "Drafts",
                navHostController
            )
        }
    ) { padding->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(start = 16.dp, end = 16.dp, top = 8.dp),
        ) {
            if (notes.isNotEmpty()) {
                val pinnedNotes = notes.filter { it.isPinned }
                val unpinnedNotes = notes.filter { !it.isPinned }
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    state = lazyStaggeredGridState,
                    modifier = Modifier.fillMaxSize(),
                    verticalItemSpacing = 8.dp,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(notes) { index, note ->
                        ShowNotes(
                            note,
                            navHostController,
                            selectedNotes,
                            multipleSelectedForDelete,
                            viewModel
                        )
                    }
                }
            }
        }

    }*/

    HomePage(
        viewModel = viewModel,
        navHostController = navHostController,
        onCardSelected = {},
        note = notes,
        isDraftsPage = true
    )
}











