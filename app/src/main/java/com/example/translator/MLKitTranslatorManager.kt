package com.example.translator

import com.google.android.gms.tasks.Tasks
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class TranslationLanguage(
    val code: String, // e.g. "pt", "en", "es"
    val displayName: String,
    val flagEmoji: String
)

data class ModelStatus(
    val languageCode: String,
    val isDownloaded: Boolean,
    val isDownloading: Boolean = false,
    val estimatedSize: String = "~30 MB"
)

class MLKitTranslatorManager {

    val supportedLanguages = listOf(
        TranslationLanguage(TranslateLanguage.PORTUGUESE, "Português", "🇧🇷"),
        TranslationLanguage(TranslateLanguage.ENGLISH, "Inglês", "🇺🇸"),
        TranslationLanguage(TranslateLanguage.SPANISH, "Espanhol", "🇪🇸"),
        TranslationLanguage(TranslateLanguage.FRENCH, "Francês", "🇫🇷"),
        TranslationLanguage(TranslateLanguage.GERMAN, "Alemão", "🇩🇪"),
        TranslationLanguage(TranslateLanguage.ITALIAN, "Italiano", "🇮🇹"),
        TranslationLanguage(TranslateLanguage.JAPANESE, "Japonês", "🇯🇵"),
        TranslationLanguage(TranslateLanguage.CHINESE, "Chinês", "🇨🇳"),
        TranslationLanguage(TranslateLanguage.RUSSIAN, "Russo", "🇷🇺")
    )

    private val modelManager = RemoteModelManager.getInstance()

    suspend fun getDownloadedModels(): Set<String> = withContext(Dispatchers.IO) {
        try {
            val models = Tasks.await(modelManager.getDownloadedModels(TranslateRemoteModel::class.java))
            models.map { it.language }.toSet()
        } catch (e: Exception) {
            emptySet()
        }
    }

    suspend fun isModelDownloaded(languageCode: String): Boolean = withContext(Dispatchers.IO) {
        val downloaded = getDownloadedModels()
        downloaded.contains(languageCode)
    }

    suspend fun downloadModel(languageCode: String, requireWifi: Boolean = false): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val model = TranslateRemoteModel.Builder(languageCode).build()
            val conditionsBuilder = DownloadConditions.Builder()
            if (requireWifi) {
                conditionsBuilder.requireWifi()
            }
            Tasks.await(modelManager.download(model, conditionsBuilder.build()))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteModel(languageCode: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val model = TranslateRemoteModel.Builder(languageCode).build()
            Tasks.await(modelManager.deleteDownloadedModel(model))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun translateText(
        text: String,
        sourceLanguage: String,
        targetLanguage: String
    ): Result<String> = withContext(Dispatchers.IO) {
        if (text.isBlank()) return@withContext Result.success("")
        if (sourceLanguage == targetLanguage) return@withContext Result.success(text)

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage)
            .setTargetLanguage(targetLanguage)
            .build()
        val translator: Translator = Translation.getClient(options)

        try {
            // First ensure models are downloaded or available
            val conditions = DownloadConditions.Builder().build()
            Tasks.await(translator.downloadModelIfNeeded(conditions))
            val translated = Tasks.await(translator.translate(text))
            translator.close()
            Result.success(translated)
        } catch (e: Exception) {
            translator.close()
            Result.failure(e)
        }
    }
}
