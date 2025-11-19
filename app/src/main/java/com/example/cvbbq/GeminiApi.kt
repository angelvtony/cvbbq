package com.example.cvbbq

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GeminiApi {
    @POST("v1/engines/gemini-2.0/flash/completions")
    suspend fun generateRoast(
        @Body request: GeminiRequest,
        @Header("Authorization") apiKey: String
    ): GeminiResponse
}

data class GeminiRequest(
    val prompt: String,
    val max_tokens: Int = 1000,
    val temperature: Double = 0.7
)

data class GeminiResponse(
    val id: String,
    val choices: List<GeminiChoice>
)

data class GeminiChoice(
    val text: String
)
