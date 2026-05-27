package com.example.SuaraRasa_A209008

data class QuoteResponse(
    val quote: QuoteDetail
)

data class QuoteDetail(
    val body: String,
    val author: String
)
