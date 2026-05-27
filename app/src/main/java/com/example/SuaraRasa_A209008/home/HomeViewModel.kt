package com.example.SuaraRasa_A209008.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.SuaraRasa_A209008.data.MoodEntry
import com.example.SuaraRasa_A209008.data.MoodRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve all mood entries in the Room database.
 */
class HomeViewModel(moodRepository: MoodRepository) : ViewModel() {

    /**
     * Holds home ui state. The list of mood entries is retrieved from [MoodRepository] and mapped to
     * [HomeUiState]
     */
    val homeUiState: StateFlow<HomeUiState> =
        moodRepository.getAllMoodEntriesStream()
            .map { HomeUiState(it) }
            .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = HomeUiState()
                    )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * Ui State for HomeScreen
 */
data class HomeUiState(val moodList: List<MoodEntry> = listOf())
