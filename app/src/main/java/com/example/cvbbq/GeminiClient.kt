package com.example.cvbbq

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer ${BuildConfig.GEMINI_API_KEY}")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            )
            .build()
    }

    val api: GeminiApi by lazy {
        retrofit.create(GeminiApi::class.java)
    }
}
