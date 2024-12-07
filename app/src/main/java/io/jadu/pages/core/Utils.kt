package io.jadu.pages.core


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.core.content.FileProvider
import io.jadu.pages.domain.model.PathProperties
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

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

    fun captureDrawingCompose(
        paths: List<Pair<Path, PathProperties>>
    ): ImageBitmap {
        // Calculate the bounds of the drawn paths
        val pathBounds = paths.fold(
            Rect(
                Float.POSITIVE_INFINITY,
                Float.POSITIVE_INFINITY,
                Float.NEGATIVE_INFINITY,
                Float.NEGATIVE_INFINITY
            )
        ) { acc, (path, _) ->
            val pathBounds = path.getBounds()
            Rect(
                left = minOf(acc.left, pathBounds.left),
                top = minOf(acc.top, pathBounds.top),
                right = maxOf(acc.right, pathBounds.right),
                bottom = maxOf(acc.bottom, pathBounds.bottom)
            )
        }

        // Add padding around the drawing area
        val padding = 32f
        val adjustedBounds = Rect(
            left = pathBounds.left - padding,
            top = pathBounds.top - padding,
            right = pathBounds.right + padding,
            bottom = pathBounds.bottom + padding
        )

        // Use the adjusted bounds to calculate the canvas size
        val drawingWidth = (adjustedBounds.right - adjustedBounds.left).toInt().coerceAtLeast(1)
        val drawingHeight = (adjustedBounds.bottom - adjustedBounds.top).toInt().coerceAtLeast(1)

        // Create ImageBitmap with calculated size
        val imageBitmap = ImageBitmap(drawingWidth, drawingHeight)
        val canvas = Canvas(imageBitmap)

        // Offset to make sure the drawn content is correctly positioned
        val offset = Offset(-adjustedBounds.left, -adjustedBounds.top)

        // Draw each path on the canvas respecting their positions
        paths.forEach { (path, properties) ->
            val adjustedPath = path.translated(offset)  // Translate path by the calculated offset
            val paint = Paint().apply {
                color = properties.color.copy(alpha = properties.alpha)
                strokeWidth = properties.strokeWidth
                style = if (properties.eraseMode) PaintingStyle.Stroke else PaintingStyle.Stroke
                strokeCap = properties.strokeCap
                strokeJoin = properties.strokeJoin
            }
            canvas.drawPath(adjustedPath, paint)
        }

        return imageBitmap
    }


    fun saveBitmapToUri(context: Context, bitmap: Bitmap, fileName: String): Uri? {
        return try {
            val imageDir = File(context.filesDir, "images")
            if (!imageDir.exists()) imageDir.mkdirs()

            val imageFile = File(imageDir, fileName)

            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) // Change format if needed
            }

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider", // FileProvider authority defined in the manifest
                imageFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun convertBitmapToUri(context: Context, bitmap: Bitmap): Uri? {
        return try {
            // Create a temporary file in the cache directory
            val tempFile = File.createTempFile(
                "temp_image_${System.currentTimeMillis()}", // File prefix
                ".jpg",                                    // File extension
                context.cacheDir                          // Directory: Cache
            )

            // Write the bitmap to the file
            val outputStream = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream) // Compress to JPEG
            outputStream.flush()
            outputStream.close()

            // Return a content URI for the file using FileProvider
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider", // Match authority in manifest
                tempFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream).also {
                inputStream?.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}

fun Path.translated(offset: Offset): Path {
    val androidPath = this.asAndroidPath()
    val matrix = android.graphics.Matrix().apply {
        setTranslate(offset.x, offset.y)
    }
    androidPath.transform(matrix)

    return Path().apply {
        this.asAndroidPath().set(androidPath)
    }
}
