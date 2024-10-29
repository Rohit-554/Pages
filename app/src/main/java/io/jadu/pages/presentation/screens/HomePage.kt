package io.jadu.pages.presentation.screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.presentation.components.CustomTopAppBar
import io.jadu.pages.presentation.viewmodel.NotesViewModel
import io.jadu.pages.ui.theme.LightGray
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(viewModel: NotesViewModel, navHostController: NavHostController) {
    val context = LocalContext.current
    val notes = viewModel.notes.collectAsState(initial = emptyList()).value
    val lazyStaggeredGridState = rememberLazyStaggeredGridState()
    var offset by remember { mutableIntStateOf(0) }
    val limit = 15
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = notes) {
        Log.d("HomePagereload", "Notes: $notes")
        viewModel.getNotesPaginated(limit, offset)
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(onSearchClick = {}) { }
        },
        contentWindowInsets = WindowInsets(
            top = 0.dp,
            bottom = 0.dp
        )
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            if (notes.isNotEmpty()) {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    state = lazyStaggeredGridState,
                    modifier = Modifier.fillMaxSize(),
                    verticalItemSpacing = 8.dp,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notes) { note ->
                        NoteCard(note, navHostController)
                    }

                    item {
                        LaunchedEffect(lazyStaggeredGridState) {
                            snapshotFlow { lazyStaggeredGridState.layoutInfo.visibleItemsInfo }
                                .collect { visibleItems ->
                                    if (visibleItems.isNotEmpty() &&
                                        visibleItems.last().index == notes.size - 1 && !isLoading
                                    ) {
                                        isLoading = true
                                        delay(100)
                                        offset += limit
                                        viewModel.getNotesPaginated(limit, offset)
                                        isLoading = false  // Reset loading
                                    }
                                }
                        }
                    }

                    if (isLoading) {
                        item {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            } else {
                Text(text = "No notes available.", style = MaterialTheme.typography.bodyLarge)
            }

        }
    }
}

@Composable
fun NoteCard(note: Notes, navHostController: NavHostController) {
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val hasImage = note.imageUri != null && note.imageUri!!.isNotEmpty()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = screenHeight.dp / 3)
            .clickable {
                navHostController.navigate("note/${note.id}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = note.color?.let { parseColor(it) } ?: LightGray
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = note.title.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                style = TextStyle(
                    fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                    fontSize = 20.sp,
                ),
                fontWeight = FontWeight.Black,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = note.description ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                overflow = TextOverflow.Ellipsis,
                maxLines = 4
            )
            Spacer(modifier = Modifier.height(4.dp))

            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(note.imageUri)
                    .build(),
            )
            Log.d("NoteCard", "ImageUri: ${note.imageUri}")
            if (note.imageUri != null && note.imageUri != "null") {
                key(note.imageUri) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = stringToUri(note.imageUri ?: "")
                        ),
                        contentDescription = "Note Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }



        }
    }
}


fun parseColor(colorString: String): Color {
    val colorValues = colorString
        .substringAfter("Color(")
        .substringBefore(", sRGB")
        .split(",")
        .map { it.trim().toFloat() }

    return Color(colorValues[0], colorValues[1], colorValues[2], colorValues[3])
}

fun stringToUri(input: String): Uri {
    return Uri.parse(input)
}