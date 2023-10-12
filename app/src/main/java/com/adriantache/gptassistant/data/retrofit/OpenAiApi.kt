package com.adriantache.gptassistant.data.retrofit

import com.adriantache.gptassistant.BuildConfig
import com.adriantache.gptassistant.data.model.OpenAiRequest
import com.adriantache.gptassistant.data.model.OpenAiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

private const val API_KEY = BuildConfig.OPENAI_SDK_KEY

fun interface OpenAiApi {
    @Headers("Content-Type: application/json", "Authorization: Bearer $API_KEY")
    @POST("v1/chat/completions")
    suspend fun getCompletions(@Body request: OpenAiRequest): Response<OpenAiResponse>
}
