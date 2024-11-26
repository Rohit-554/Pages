package io.jadu.pages.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
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
import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter
import coil3.request.CachePolicy
import coil3.request.crossfade
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.presentation.screens.parseColor
import io.jadu.pages.presentation.viewmodel.NotesViewModel
import io.jadu.pages.ui.theme.LightGray
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: Notes,
    navHostController: NavHostController,
    onLongPress: (Notes) -> Unit,
    isSelected: Boolean,
    onClick: () -> Unit,
    multipleSelectedForDelete: Boolean,
    viewmodel: NotesViewModel,
) {
    val context = LocalContext.current
    val screenHeight = LocalConfiguration.current.screenHeightDp
    val borderColor = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = screenHeight.dp / 3)
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(2.dp))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = screenHeight.dp / 3)
                .combinedClickable(
                    onClick = {
                        if (multipleSelectedForDelete) {
                            onClick()
                        } else {
                            navHostController.navigate("note/${note.id}")
                        }
                    },
                    onLongClick = {
                        onLongPress(note)
                    },
                ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = note.color?.let { parseColor(it) }
                    ?: LightGray
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row {
                    Text(
                        text = note.title.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.ROOT
                            ) else it.toString()
                        },
                        style = TextStyle(
                            fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                            fontSize = 20.sp,
                        ),
                        fontWeight = FontWeight.Black,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    if(note.isPinned){
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Filled.PushPin,
                            contentDescription = "Pinned",
                            modifier = Modifier.padding(start = 4.dp).size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.description ?: "",
                    style = TextStyle(
                        fontFamily = MaterialTheme.typography.bodyLarge.fontFamily,
                        fontSize = 16.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontWeight = FontWeight.W600,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(4.dp))


                if (!note.imageUri.isNullOrEmpty() && note.imageUri!!.isNotEmpty()) {
                    key(note.imageUri!!.first()) {
                        note.imageUri!!.first().let { uri ->
                            if(uri.toString().contains("drawing")){
                                Box(
                                    modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp))
                                ){
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            model = uri,
                                            contentScale = ContentScale.Crop
                                        ),
                                        contentDescription = "Note Image",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                    )
                                }
                            }else{
                                Image(
                                    painter = rememberAsyncImagePainter(
                                        model = uri,
                                    ) ,
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
        }
    }
}