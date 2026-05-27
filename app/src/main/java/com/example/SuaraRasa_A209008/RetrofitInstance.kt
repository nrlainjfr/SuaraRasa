//untuk sediakan connection dengan API

package com.example.SuaraRasa_A209008

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

object RetrofitInstance {
    private const val BASE_URL = "https://favqs.com/api/"

    val api: QuoteApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuoteApiService::class.java)
    }

}

