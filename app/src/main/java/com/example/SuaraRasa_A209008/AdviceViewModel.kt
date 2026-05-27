package com.example.SuaraRasa_A209008

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.collections.minus
import kotlin.collections.plus
import android.app.Application
import androidx.lifecycle.*

class AdviceViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = DataStoreManager(application.applicationContext)

    var advice = mutableStateOf("Fetching advice...")
        private set

    var favorites = mutableStateOf(setOf<String>())
        private set

    init {
        loadFavorites()
        fetchAdvice()
    }
    private fun loadFavorites() {
        viewModelScope.launch {
            dataStore.favoritesFlow.collect {
                favorites.value = it
            }
        }
    }
    fun removeFavorite(quote: String) {
        val updated = favorites.value - quote
        favorites.value = updated
        saveFavorites(updated) // <-- ini WAJIB
    }

    fun toggleFavorite() {
        val current = advice.value
        val updated = if (favorites.value.contains(current)) {
            favorites.value - current
        } else {
            favorites.value + current
        }
        favorites.value = updated
        saveFavorites(updated) // <-- ini WAJIB
    }

    fun isFavorite(): Boolean {
        return favorites.value.contains(advice.value)
    }

    private fun saveFavorites(quotes: Set<String>) {
        viewModelScope.launch {
            dataStore.saveFavorites(quotes)
        }
    }

    fun fetchAdvice() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getQuoteOfTheDay()
                advice.value = "\"${response.quote.body}\" — ${response.quote.author}"
            } catch (e: Exception) {
                advice.value = "Error: ${e.message}"
            }
        }
    }
}

