package io.jadu.pages.presentation.screens.draw

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.jadu.pages.ui.theme.gradientColors
import kotlin.math.atan2

/**
 * Simple circle with stroke to show rainbow colors as [Brush.sweepGradient]
 */
@Composable
fun ColorWheel(modifier: Modifier = Modifier) {

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        require(canvasWidth == canvasHeight,
            lazyMessage = {
                print("Canvas dimensions should be equal to each other")
            }
        )
        val cX = canvasWidth / 2
        val cY = canvasHeight / 2
        val canvasRadius = canvasWidth.coerceAtMost(canvasHeight) / 2f
        val center = Offset(cX, cY)
        val strokeWidth = canvasRadius * .3f
        // Stroke is drawn out of the radius, so it's required to subtract stroke width from radius
        val radius = canvasRadius - strokeWidth
        drawCircle(
            brush = Brush.sweepGradient(colors = gradientColors, center = center),
            radius = radius,
            center = center,
            style = Stroke(
                width = strokeWidth
            )
        )
    }
}

/**
 * Composable that shows a title as initial letter, title color and a Slider to pick color
 */
@Composable
fun ColorSlider(
    modifier: Modifier,
    title: String,
    titleColor: Color,
    valueRange: ClosedFloatingPointRange<Float> = 0f..255f,
    rgb: Float,
    onColorChanged: (Float) -> Unit
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {

        Text(text = title.substring(0, 1), color = titleColor, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(8.dp))
        Slider(
            modifier = Modifier.weight(1f),
            value = rgb,
            onValueChange = { onColorChanged(it) },
            valueRange = valueRange,
            onValueChangeFinished = {}
        )

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = rgb.toInt().toString(),
            color = Color.LightGray,
            fontSize = 12.sp,
            modifier = Modifier.width(30.dp)
        )

    }
}
