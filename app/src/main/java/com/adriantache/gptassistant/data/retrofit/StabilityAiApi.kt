package com.adriantache.gptassistant.data.retrofit

import com.adriantache.gptassistant.data.model.StabilityAiRequest
import com.adriantache.gptassistant.data.model.StabilityAiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Streaming

private const val BASE_URL = "https://api.stability.ai"
private const val MODEL = "stable-diffusion-xl-beta-v2-2-2"

@Suppress("kotlin:S6517")
interface StabilityAiApi {
    @Streaming
    @Headers("Content-Type: application/json")
    @POST("$BASE_URL/v1/generation/$MODEL/text-to-image")
    suspend fun getImageGeneration(
        @Header("Authorization") authHeader: String,
        @Body request: StabilityAiRequest,
    ): Response<StabilityAiResponse>
}
