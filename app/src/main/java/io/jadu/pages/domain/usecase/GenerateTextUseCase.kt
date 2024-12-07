package io.jadu.pages.domain.usecase

import android.content.Context
import android.net.Uri
import io.jadu.pages.domain.repository.OcrRepository

class GenerateTextUseCase(
    private val repository: OcrRepository
) {
    suspend operator fun invoke(imageUri: Uri, context: Context): String? {
        return repository.generateTextFromImage(imageUri, context)
    }
}
