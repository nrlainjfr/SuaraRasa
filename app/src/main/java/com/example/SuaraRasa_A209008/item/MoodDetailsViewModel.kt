package com.example.SuaraRasa_A209008.item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.SuaraRasa_A209008.data.MoodRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve, update and delete a mood from the [MoodRepository]'s data source.
 */
class MoodDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val moodRepository: MoodRepository,
) : ViewModel() {

    private val moodId: Int = checkNotNull(savedStateHandle[MoodDetailsDestination.moodIdArg])

    /**
     * Holds the mood details UI state. The data is retrieved from [MoodRepository] and mapped to
     * the UI state.
     */
    val uiState: StateFlow<MoodDetailsUiState> =
        moodRepository.getMoodEntryStream(moodId)
            .filterNotNull()
            .map {
                MoodDetailsUiState(moodDetails = it.toMoodDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = MoodDetailsUiState()
            )

    /**
     * Deletes the mood from the [MoodRepository]'s data source.
     */
    suspend fun deleteMood() {
        moodRepository.deleteMoodEntry(uiState.value.moodDetails.toMoodEntry())
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * UI state for MoodDetailsScreen
 */
data class MoodDetailsUiState(
    val moodDetails: MoodDetails = MoodDetails()
)

