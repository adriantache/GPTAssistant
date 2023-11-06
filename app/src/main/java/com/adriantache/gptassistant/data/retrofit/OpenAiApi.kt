package com.adriantache.gptassistant.data.retrofit

import com.adriantache.gptassistant.data.model.OpenAiRequest
import com.adriantache.gptassistant.data.model.OpenAiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

fun interface OpenAiApi {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun getCompletions(
        @Header("Authorization") authHeader: String,
        @Body request: OpenAiRequest,
    ): Response<OpenAiResponse>
}
