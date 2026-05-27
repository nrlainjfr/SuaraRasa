package com.example.SuaraRasa_A209008.item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.SuaraRasa_A209008.data.MoodRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel untuk mendapatkan dan mengemas kini entri mood dari sumber data [MoodRepository].
 */
class MoodEditViewModel(
        savedStateHandle: SavedStateHandle,
        private val moodRepository: MoodRepository
                       ) : ViewModel() {

    /**
     * Menyimpan keadaan semasa UI entri mood.
     */
    var moodUiState by mutableStateOf(MoodUiState())
        private set

    private val moodId: Int = checkNotNull(savedStateHandle[MoodEditDestination.moodIdArg])

    init {
        viewModelScope.launch {
            moodUiState = moodRepository.getMoodEntryStream(moodId)
                .filterNotNull()
                .first()
                .toMoodUiState(true)
        }
    }

    /**
     * Mengemas kini entri mood dalam sumber data [MoodRepository].
     */
    suspend fun updateMood() {
        if (validateInput(moodUiState.moodDetails)) {
            moodRepository.updateMoodEntry(moodUiState.moodDetails.toMoodEntry())
        }
    }

    /**
     * Mengemas kini [moodUiState] dengan nilai baru. Fungsi ini juga menyemak kesahan input.
     */
    fun updateUiState(moodDetails: MoodDetails) {
        moodUiState = MoodUiState(
                moodDetails = moodDetails,
                isEntryValid = validateInput(moodDetails)
                                 )
    }

    private fun validateInput(uiState: MoodDetails = moodUiState.moodDetails): Boolean {
        return with(uiState) {
            emotion.isNotBlank() && date.isNotBlank() &&
                    time.isNotBlank() && activity.isNotBlank() && note.isNotBlank()
        }
    }
}
