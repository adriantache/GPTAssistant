package com.adriantache.gptassistant.data.retrofit

import com.adriantache.gptassistant.data.model.StabilityAi3Response
import com.adriantache.gptassistant.data.model.StabilityAiRequest
import com.adriantache.gptassistant.data.model.StabilityAiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Streaming

private const val BASE_URL = "https://api.stability.ai"
private const val MODEL = "stable-diffusion-xl-beta-v2-2-2"
private const val STABILITY_3 = "v2beta/stable-image/generate/sd3"

@Suppress("kotlin:S6517")
interface StabilityAiApi {
    // Currently keeping this for legacy purposes.
    @Suppress("unused")
    @Streaming
    @Headers("Content-Type: application/json")
    @POST("$BASE_URL/v1/generation/$MODEL/text-to-image")
    suspend fun getImageGeneration(
        @Header("Authorization") authHeader: String,
        @Body request: StabilityAiRequest,
    ): Response<StabilityAiResponse>

    @Streaming
    @Multipart
    @Headers("Accept: application/json")
    @POST("$BASE_URL/$STABILITY_3")
    suspend fun getImageGeneration3(
        @Header("Authorization") authHeader: String,
        @Part request: String,
    ): Response<StabilityAi3Response>
}
