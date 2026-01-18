package com.example.unick.api

import retrofit2.http.Body
import retrofit2.http.POST

// --- Legacy Groq Service (Deprecated - Use GroqApiService instead) ---
// This interface is kept for backward compatibility with SchoolSearchService
// TODO: Migrate SchoolSearchService to use GroqApiService
interface GroqService {
    @POST("openai/v1/chat/completions")
    suspend fun createChatCompletion(
        @Body request: LegacyGroqRequest
    ): LegacyGroqResponse
}

// --- Legacy Groq Data Classes (Deprecated) ---
data class LegacyGroqRequest(
    val model: String,
    val messages: List<LegacyGroqMessage>
)

data class LegacyGroqMessage(
    val role: String,
    val content: String
)

data class LegacyGroqResponse(
    val choices: List<LegacyGroqChoice>
)

data class LegacyGroqChoice(
    val message: LegacyGroqMessage
)

