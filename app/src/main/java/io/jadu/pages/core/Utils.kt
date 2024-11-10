package io.jadu.pages.core


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color

class Utils {
    fun colorToHex(color: Color, includeAlpha: Boolean = false): String {
        val red = (color.red * 255).toInt()
        val green = (color.green * 255).toInt()
        val blue = (color.blue * 255).toInt()
        val alpha = (color.alpha * 255).toInt()
        return if (includeAlpha) {
            String.format("#%02X%02X%02X%02X", alpha, red, green, blue)
        } else {
            String.format("#%02X%02X%02X", red, green, blue)
        }
    }
}