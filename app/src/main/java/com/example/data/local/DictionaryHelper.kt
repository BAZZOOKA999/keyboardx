package com.example.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DictionaryHelper(private val personalWordDao: PersonalWordDao) {

    private val ptWords = listOf(
        "você", "como", "para", "está", "muito", "obrigado", "obrigada", "quando", "fazer",
        "agora", "porque", "então", "também", "depois", "vamos", "podemos", "grande",
        "sempre", "tempo", "trabalho", "projeto", "mensagem", "teclado", "inteligente",
        "conectar", "sistema", "configuração", "segurança", "privacidade", "desenvolvimento",
        "aplicativo", "código", "terminal", "sucesso", "amigo", "família", "informação",
        "resposta", "pergunta", "verdade", "certeza", "preciso", "precisamos", "importante",
        "problema", "solução", "reunião", "hoje", "amanhã", "ontem", "bom", "boa",
        "dia", "tarde", "noite", "tudo", "nada", "quem", "onde", "qual", "quais"
    )

    private val enWords = listOf(
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "i", "it", "for", "not",
        "on", "with", "he", "as", "you", "do", "at", "this", "but", "his", "by", "from",
        "they", "we", "say", "her", "she", "or", "an", "will", "my", "one", "all", "would",
        "there", "their", "what", "so", "up", "out", "if", "about", "who", "get", "which",
        "go", "me", "when", "make", "can", "like", "time", "no", "just", "him", "know",
        "take", "people", "into", "year", "your", "good", "some", "could", "them", "see",
        "other", "than", "then", "now", "look", "only", "come", "its", "over", "think",
        "also", "back", "after", "use", "two", "how", "our", "work", "first", "well",
        "way", "even", "new", "want", "because", "any", "these", "give", "day", "most", "us"
    )

    private val esWords = listOf(
        "hola", "gracias", "por", "favor", "cuando", "donde", "porque", "como", "está",
        "estoy", "bueno", "buena", "amigo", "trabajo", "tiempo", "ahora", "después",
        "siempre", "nunca", "grande", "nuevo", "nueva", "hacer", "podemos", "proyecto",
        "teclado", "sistema", "mensaje", "pregunta", "respuesta", "seguro", "información"
    )

    suspend fun getSuggestions(prefix: String, language: String): List<String> = withContext(Dispatchers.Default) {
        if (prefix.isBlank()) return@withContext emptyList()
        val lowerPrefix = prefix.lowercase()

        // 1. Check personal dictionary first
        val personalMatches = try {
            personalWordDao.getMatchingWords(lowerPrefix).map { it.word }
        } catch (e: Exception) {
            emptyList()
        }

        // 2. Built-in language list
        val builtInList = when (language) {
            "en_US" -> enWords
            "es_ES" -> esWords
            else -> ptWords
        }

        val builtInMatches = builtInList.filter {
            it.lowercase().startsWith(lowerPrefix) && !it.equals(prefix, ignoreCase = true)
        }

        // Combine and distinct
        val results = mutableListOf<String>()
        // Include exact prefix capitalized or as entered if not in list
        results.addAll(personalMatches)
        results.addAll(builtInMatches)

        // If prefix itself is close, capitalize appropriately
        val isFirstUpper = prefix.firstOrNull()?.isUpperCase() == true
        val formattedResults = results.distinct().take(5).map { word ->
            if (isFirstUpper && word.isNotEmpty()) {
                word.replaceFirstChar { it.uppercase() }
            } else {
                word
            }
        }

        formattedResults
    }

    suspend fun learnWord(word: String, language: String) = withContext(Dispatchers.IO) {
        if (word.length in 2..30 && word.all { it.isLetter() || it == '-' }) {
            try {
                personalWordDao.insertOrUpdate(
                    com.example.data.model.PersonalWord(
                        word = word.lowercase(),
                        frequency = 1,
                        language = language
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
