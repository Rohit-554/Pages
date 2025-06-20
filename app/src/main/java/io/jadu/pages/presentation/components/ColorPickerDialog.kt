package io.jadu.pages.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.jadu.pages.ui.theme.LightGray
import io.jadu.pages.ui.theme.White

@Composable
fun ColorPickerDialog(
    showDialog: Boolean,
    selectedColor: Color,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit,
    onResetToDefaultSelected: () -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = LightGray,
                modifier = Modifier.wrapContentWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .wrapContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Color Lens",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                   onDismiss()
                                }
                        )
                    }
                    Text(
                        text = "Pick a color",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "This color will be used as the background color for the Note",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )


                    val colorPalette = listOf(
                        Color(0xFFEF9A9A).copy(alpha = 0.7f), // Muted Coral Red
                        Color(0xFFFFAB91).copy(alpha = 0.7f), // Soft Orange
                        Color(0xFFFFCC80).copy(alpha = 0.7f), // Light Amber
                        Color(0xFFE6EE9C).copy(alpha = 0.4f), // Pale Lime Green
                        Color(0xFFA5D6A7).copy(alpha = 0.2f), // Soft Mint Green
                        Color(0xFF80CBC4).copy(alpha = 0.7f), // Light Teal
                        Color(0xFF81D4FA).copy(alpha = 0.7f), // Soft Sky Blue
                        Color(0xFF90CAF9).copy(alpha = 0.7f), // Pale Light Blue
                        Color(0xFFB39DDB).copy(alpha = 0.7f), // Muted Lavender
                        Color(0xFFFFF59D).copy(alpha = 0.7f), // Soft Yellow
                        Color(0xFFCE93D8).copy(alpha = 0.7f)  // Light Purple
                    )



                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        items(colorPalette) { color ->
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(65.dp)
                                    .padding(8.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable {
                                        onColorSelected(color)
                                    }
                            ) {
                                if (color == selectedColor) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.Black,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.padding(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                onResetToDefaultSelected()
                            },
                            modifier = Modifier
                                .padding(end = 8.dp),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(0.5.dp, Color.Gray)
                        ) {
                            Text("Reset", style = TextStyle(color = Color.White.copy(alpha = 0.7f), fontFamily = MaterialTheme.typography.bodyLarge.fontFamily))
                        }

                        Button(
                            onClick = {

                                onDismiss()
                            },
                            border = BorderStroke(0.5.dp, Color.White),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("Confirm", style = TextStyle(fontFamily = MaterialTheme.typography.bodyLarge.fontFamily))
                        }
                    }
                }
            }
        }
    }
}


