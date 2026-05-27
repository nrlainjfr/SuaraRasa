package com.example.SuaraRasa_A209008

data class Card(
    val id: Int,
    val value: Int,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
) {
    // Renamed to be more explicit
    fun copyWithFaceUp(isFaceUp: Boolean): Card {
        return Card(id, value, isFaceUp, isMatched)
    }

    // Renamed to be more explicit
    fun copyWithMatched(isMatched: Boolean): Card {
        return Card(id, value, isFaceUp, isMatched)
    }
}