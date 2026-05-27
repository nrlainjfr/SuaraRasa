package com.example.SuaraRasa_A209008

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.all
import kotlin.collections.flatMap
import kotlin.collections.map
import kotlin.collections.mapIndexed
import kotlin.collections.shuffled

class GameState(
    private val gridSize: Int = 4,
    private val onGameComplete: () -> Unit = {},
    private val coroutineScope: CoroutineScope // Add this parameter
) {
    private var _cards by mutableStateOf(emptyList<Card>())
    val cards: List<Card> get() = _cards

    private var _moves by mutableStateOf(0)
    val moves: Int get() = _moves

    private var _isProcessing by mutableStateOf(false)
    val isProcessing: Boolean get() = _isProcessing

    private var _gameComplete by mutableStateOf(false)
    val gameComplete: Boolean get() = _gameComplete

    private var selectedCards = mutableListOf<Card>()

    init {
        startNewGame()
    }

    fun startNewGame() {
        _cards = createCards()
        _moves = 0
        _gameComplete = false
        selectedCards.clear()
    }

    private fun createCards(): List<Card> {
        val pairs = gridSize * gridSize / 2
        val values = (1..pairs).flatMap { listOf(it, it) }.shuffled()
        return values.mapIndexed { index, value ->
            Card(id = index, value = value)
        }
    }

    fun onCardClick(card: Card) { // Remove suspend modifier
        if (_isProcessing || card.isFaceUp || card.isMatched) return

        coroutineScope.launch {
            // Flip the card
            _cards = _cards.map {
                if (it.id == card.id) it.copy(isFaceUp = true) else it
            }

            selectedCards.add(card)

            if (selectedCards.size == 2) {
                _moves++
                _isProcessing = true
                checkForMatch()
                _isProcessing = false
            }
        }
    }

    private suspend fun checkForMatch() {
        delay(500) // Give player time to see the second card

        val (first, second) = selectedCards
        if (first.value == second.value) {
            // Match found
            _cards = _cards.map { card ->
                if (card.id == first.id || card.id == second.id) {
                    card.copy(isMatched = true)
                } else {
                    card
                }
            }

            // Check if game is complete
            if (_cards.all { it.isMatched }) {
                _gameComplete = true
                onGameComplete()
            }
        } else {
            // No match - flip cards back
            _cards = _cards.map { card ->
                if (card.id == first.id || card.id == second.id) {
                    card.copy(isFaceUp = false)
                } else {
                    card
                }
            }
        }

        selectedCards.clear()
    }
}