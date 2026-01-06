package com.linguafranca.domain.usecase

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class TranslateWordUseCase @Inject constructor() {

    private val options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.RUSSIAN)
        .build()

    private val translator = Translation.getClient(options)

    suspend operator fun invoke(text: String): String? {
        return suspendCancellableCoroutine { continuation ->
            // First, ensure the model is downloaded
            val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()

            translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    // Model is available, translate the text
                    translator.translate(text)
                        .addOnSuccessListener { translatedText ->
                            if (continuation.isActive) {
                                continuation.resume(translatedText)
                            }
                        }
                        .addOnFailureListener {
                            if (continuation.isActive) {
                                continuation.resume(null)
                            }
                        }
                }
                .addOnFailureListener {
                    // Model download failed
                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                }

            continuation.invokeOnCancellation {
                // Cleanup if needed
            }
        }
    }
}

