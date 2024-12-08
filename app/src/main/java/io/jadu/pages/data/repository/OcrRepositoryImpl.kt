package io.jadu.pages.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import io.jadu.pages.core.Utils
import io.jadu.pages.domain.repository.OcrRepository

import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException

class OcrRepositoryImpl(
    private val model: GenerativeModel,
    private val utils: Utils
): OcrRepository {
    override suspend fun generateTextFromImage(imageUri: Uri, context: Context): String? {
        return try {
            withTimeout(10000) { // 10 seconds timeout
                val bitmap = utils.uriToBitmap(context, imageUri)
                if (bitmap != null) {
                    val response = model.generateContent(
                        content {
                            image(bitmap)
                            text("scan the texts in the image and generate the text to the maximum token allowed by the model and also arrange the texts by observing the context")
                        }
                    )
                    response.text
                } else null
            }
        } catch (e: TimeoutCancellationException) {
            Log.e("OcrRepository", "Timeout: Something went wrong", e)
            "Something went wrong"
        } catch (e: Exception) {
            Log.e("OcrRepository", "Error generating text", e)
            "Something went wrong"
        }
    }
}