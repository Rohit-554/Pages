package io.jadu.pages.domain.repository

import android.content.Context
import android.net.Uri

interface OcrRepository {
    suspend fun generateTextFromImage(imageUri: Uri, context: Context): String?
}