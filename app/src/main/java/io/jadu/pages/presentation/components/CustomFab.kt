package io.jadu.pages.presentation.components

import android.media.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.jadu.pages.ui.theme.LightGray
import io.jadu.pages.ui.theme.White

@Composable
fun CustomFab(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    backgroundColor: Color = LightGray,
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = backgroundColor,
        elevation = elevation,
        modifier = Modifier.background(
            color = backgroundColor,
            shape = RoundedCornerShape(50.dp) // Ensures the FAB is circular
        )
    ) {
        Icon(
            icon,
            contentDescription = contentDescription
        )
    }
}


@Composable
fun SaveFab(onClick : () -> Unit, icon:ImageVector = Icons.Filled.Save, containerColor:Color = Color.White,
            tintColor:Color = Color.Black) {
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
        ) {
            FloatingActionButton(
                onClick = {
                    onClick()
                },
                modifier = Modifier
                    .padding(horizontal = 0.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(White),
                containerColor = containerColor,
                elevation = FloatingActionButtonDefaults.elevation(5.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Save Note",
                    tint = tintColor
                )
            }
        }
    }
}
