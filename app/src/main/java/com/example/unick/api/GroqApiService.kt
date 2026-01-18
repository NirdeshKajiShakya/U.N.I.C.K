package com.example.unick.api

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import com.google.gson.annotations.SerializedName

// Request/Response models for Grok API
data class GroqChatRequest(
    @SerializedName("messages") val messages: List<GroqMessage>,
    @SerializedName("model") val model: String = "llama-3.3-70b-versatile",
    @SerializedName("temperature") val temperature: Double = 0.7,
    @SerializedName("max_tokens") val maxTokens: Int = 1024,
    @SerializedName("stream") val stream: Boolean = false
)

data class GroqMessage(
    @SerializedName("role") val role: String, // "system", "user", or "assistant"
    @SerializedName("content") val content: String
)

data class GroqChatResponse(
    @SerializedName("id") val id: String,
    @SerializedName("choices") val choices: List<GroqChoice>,
    @SerializedName("usage") val usage: GroqUsage?
)

data class GroqChoice(
    @SerializedName("message") val message: GroqMessage,
    @SerializedName("finish_reason") val finishReason: String?
)

data class GroqUsage(
    @SerializedName("prompt_tokens") val promptTokens: Int,
    @SerializedName("completion_tokens") val completionTokens: Int,
    @SerializedName("total_tokens") val totalTokens: Int
)

interface GroqApiService {
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Body request: GroqChatRequest
    ): GroqChatResponse
}

