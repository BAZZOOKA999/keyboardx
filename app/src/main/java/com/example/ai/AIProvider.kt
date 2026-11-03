package com.example.ai

import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class AITool(val title: String, val description: String, val promptInstruction: String) {
    GRAMMAR(
        "Corrigir Ortografia",
        "Corrige erros ortográficos e gramaticais mantendo o sentido original.",
        "Corrija apenas a ortografia, pontuação e gramática do texto a seguir. Retorne apenas o texto corrigido, sem explicações adicionais:"
    ),
    CLARITY(
        "Melhorar Clareza",
        "Torna o texto mais direto, compreensível e fluido.",
        "Reescreva o texto a seguir melhorando sua clareza, coesão e fluidez. Retorne apenas o texto aprimorado:"
    ),
    PROFESSIONAL(
        "Mais Profissional",
        "Ajusta o tom para um ambiente de trabalho ou formal.",
        "Reescreva o texto a seguir em um tom profissional, executivo e formal. Retorne apenas o texto resultante:"
    ),
    INFORMAL(
        "Mais Informal",
        "Transforma em tom descontraído e amigável.",
        "Reescreva o texto a seguir em um tom casual, amigável e descontraído. Retorne apenas o texto resultante:"
    ),
    SUMMARIZE(
        "Resumir",
        "Sintetiza os pontos principais de forma concisa.",
        "Resuma de forma concisa e direta os pontos principais do texto a seguir. Retorne apenas o resumo:"
    ),
    EXPAND(
        "Expandir",
        "Desenvolve e detalha melhor os argumentos e ideias.",
        "Expanda e detalhe as ideias do texto a seguir com argumentos complementares. Retorne apenas o texto expandido:"
    ),
    REWRITE(
        "Reescrever",
        "Reformula as frases preservando a mensagem central.",
        "Reescreva o texto a seguir com outras palavras, mantendo o mesmo significado e tamanho similar. Retorne apenas o texto:"
    ),
    POLITE(
        "Mais Educado",
        "Adiciona polidez, cordialidade e simpatia ao tom.",
        "Reescreva o texto a seguir com extremo respeito, cortesia e cordialidade. Retorne apenas o texto:"
    ),
    CONVINCING(
        "Mais Convincente",
        "Aumenta o poder de persuasão e convicção.",
        "Torne o texto a seguir mais persuasivo, convincente e impactante. Retorne apenas o texto:"
    ),
    BULLET_LIST(
        "Converter em Lista",
        "Estrutura o conteúdo em tópicos organizados.",
        "Converta o conteúdo e ideias do texto a seguir em uma lista organizada com marcadores. Retorne apenas a lista:"
    ),
    EXPLAIN(
        "Explicar Texto",
        "Explica o conceito ou conteúdo de maneira acessível.",
        "Explique o significado e ideias centrais do texto a seguir de maneira simples e didática. Retorne apenas a explicação:"
    ),
    AI_TRANSLATE(
        "Traduzir com IA",
        "Tradução contextual de alta fidelidade.",
        "Traduza o texto a seguir preservando o contexto e naturalidade das expressões. Retorne apenas a tradução:"
    ),
    REPLY(
        "Gerar Resposta",
        "Gera uma resposta adequada ao conteúdo informado.",
        "Gere uma resposta adequada, prestativa e bem formulada para a seguinte mensagem ou contexto. Retorne apenas a resposta sugerida:"
    ),
    CUSTOM(
        "Instrução Personalizada",
        "Executa seu comando personalizado sobre o texto.",
        ""
    )
}

interface AIProvider {
    fun isConfigured(): Boolean
    fun getConfigurationMessage(): String
    suspend fun processText(tool: AITool, text: String, customPrompt: String? = null): Result<String>
}

class FirebaseAIProvider : AIProvider {

    private var generativeModel: GenerativeModel? = null

    init {
        try {
            // Check if Firebase AI can be initialized
            generativeModel = Firebase.ai.generativeModel(modelName = "gemini-2.5-flash")
        } catch (e: Exception) {
            generativeModel = null
        }
    }

    override fun isConfigured(): Boolean {
        return generativeModel != null
    }

    override fun getConfigurationMessage(): String {
        return if (isConfigured()) {
            "Firebase AI Logic conectado e pronto para processar requisições sob demanda com consentimento explícito."
        } else {
            "Configuração de IA pendente: O Firebase AI Logic requer provisionamento do projeto com App Check para ativação em nuvem. Para garantir total privacidade, nenhum texto é enviado automaticamente e resultados falsos nunca são simulados."
        }
    }

    override suspend fun processText(
        tool: AITool,
        text: String,
        customPrompt: String?
    ): Result<String> = withContext(Dispatchers.IO) {
        val model = generativeModel
        if (model == null) {
            return@withContext Result.failure(
                IllegalStateException(getConfigurationMessage())
            )
        }

        val prompt = if (tool == AITool.CUSTOM) {
            "${customPrompt ?: "Analise o texto"}:\n\n$text"
        } else {
            "${tool.promptInstruction}\n\n$text"
        }

        try {
            val response = model.generateContent(prompt)
            val output = response.text?.trim()
            if (!output.isNullOrBlank()) {
                Result.success(output)
            } else {
                Result.failure(Exception("Resposta vazia retornada pelo modelo."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
