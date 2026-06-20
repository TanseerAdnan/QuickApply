package com.t.quickapply.data.remote.ai

class AiRepository {

    private val api = AiApiClient.api

    suspend fun enhanceResume(apiKey: String, resumeText: String): String {
        val prompt = """
            You are a resume editor. Rewrite the resume below.
            
            STRICT RULES:
            - Output ONLY the improved resume content.
            - Do NOT add any commentary, questions, or meta-text.
            - Do NOT say things like "Here is your improved resume" or "Let me know if you need changes".
            - Do NOT ask if the user wants more or offer further help.
            - Do NOT include any text that is not part of the resume itself.
            - Keep all facts, dates, company names, and job titles exactly as provided.
            - Improve wording, grammar, and ATS optimization.

            Resume:
            $resumeText
        """.trimIndent()

        val response = api.chat(
            auth = "Bearer $apiKey",
            body = ChatRequest(
                model = "llama-3.3-70b-versatile",
                messages = listOf(Message(role = "user", content = prompt))
            )
        )

        return response.choices.firstOrNull()?.message?.content ?: ""
    }

    suspend fun enhanceDraft(apiKey: String, draftText: String): String {
        val prompt = """
            You are an email editor. Rewrite the email draft below.
            
            STRICT RULES:
            - Output ONLY the improved email text.
            - Do NOT add any commentary, questions, or meta-text.
            - Do NOT say things like "Here is your improved email" or "Let me know if you need changes".
            - Do NOT ask if the user wants more or offer further help.
            - Do NOT include any text that is not part of the email itself.
            - Keep [HR Name] and [Company Name] placeholders exactly as-is, do not remove or replace them.
            - Improve wording, grammar, tone, and professionalism.
            - Keep it concise and impactful.
            
            Draft:
            $draftText
        """.trimIndent()

        val response = api.chat(
            auth = "Bearer $apiKey",
            body = ChatRequest(
                model = "llama-3.3-70b-versatile",
                messages = listOf(Message(role = "user", content = prompt))
            )
        )

        return response.choices.firstOrNull()?.message?.content ?: ""
    }
}