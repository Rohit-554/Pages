package io.jadu.pages.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import io.jadu.pages.core.Utils
import io.jadu.pages.domain.repository.OcrRepository

class OcrRepositoryImpl(
    private val model: GenerativeModel,
    private val utils: Utils
): OcrRepository {
    override suspend fun generateTextFromImage(imageUri: Uri, context: Context): String? {
        return try {
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
        } catch (e: Exception) {
            Log.e("ContentRepository", "Error generating text", e)
            null
        }
    }
}