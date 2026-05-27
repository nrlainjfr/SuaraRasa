package com.example.SuaraRasa_A209008

import retrofit2.http.GET
import retrofit2.http.Headers

interface QuoteApiService {
    @Headers("Authorization: Token token=\"2b937bae0867a1aabf1df0410ae4ab99\"")
    @GET("qotd")
    suspend fun getQuoteOfTheDay(): QuoteResponse
}
