package io.jadu.pages.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.jadu.pages.ui.theme.LightGray

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
