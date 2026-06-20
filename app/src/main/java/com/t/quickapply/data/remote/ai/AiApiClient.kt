package com.t.quickapply.data.remote.ai

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AiApiClient {

    val api: GroqApi by lazy {

        Retrofit.Builder()
            .baseUrl("https://api.groq.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GroqApi::class.java)
    }
}