package io.jadu.pages.presentation.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.jadu.pages.ui.theme.Black

@Composable
fun CustomSnackBar(snackBarHostState: SnackbarHostState, icon:ImageVector, isError:Boolean) {
    SnackbarHost(
        hostState = snackBarHostState,
        snackbar = { snackBarData ->

            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isError) Color.Red.copy(alpha = 0.8f) else Color.Green.copy(alpha = 0.8f),
                    modifier = Modifier.padding(
                        end = 0.dp,
                        start = 4.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                )
                Text(
                    text = snackBarData.visuals.message,
                    color = Black,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                )
            }

        }
    )
}
