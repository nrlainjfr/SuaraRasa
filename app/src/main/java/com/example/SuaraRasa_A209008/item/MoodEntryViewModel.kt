package com.example.SuaraRasa_A209008.item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.SuaraRasa_A209008.data.MoodEntry
import com.example.SuaraRasa_A209008.data.MoodRepository

class MoodEntryViewModel(private val moodRepository: MoodRepository) : ViewModel() {

    var moodUiState by mutableStateOf(MoodUiState())
        private set

    fun updateUiState(moodDetails: MoodDetails) {
        moodUiState =
            MoodUiState(moodDetails = moodDetails, isEntryValid = validateInput(moodDetails))
    }

    private fun validateInput(uiState: MoodDetails = moodUiState.moodDetails): Boolean {
        return with(uiState) {
            emotion.isNotBlank() && date.isNotBlank() && time.isNotBlank() &&
                    activity.isNotBlank() && note.isNotBlank()
        }
    }

    suspend fun saveMood() {
        if (validateInput()) {
            moodRepository.insertMoodEntry(moodUiState.moodDetails.toMoodEntry())
        }
    }
}

// -------------------- UI STATE & CONVERTERS --------------------

data class MoodUiState(
        val moodDetails: MoodDetails = MoodDetails(),
        val isEntryValid: Boolean = false
                      )

data class MoodDetails(
        val id: Int = 0,
        val emotion: String = "",
        val date: String = "",
        val time: String = "",
        val activity: String = "",
        val note: String = ""
                      )

fun MoodDetails.toMoodEntry(): MoodEntry = MoodEntry(
        id = id,
        emotion = emotion,
        date = date,
        time = time,
        activity = activity,
        note = note
                                                    )

fun MoodEntry.toMoodUiState(isEntryValid: Boolean = false): MoodUiState = MoodUiState(
        moodDetails = this.toMoodDetails(),
        isEntryValid = isEntryValid
                                                                                     )

fun MoodEntry.toMoodDetails(): MoodDetails = MoodDetails(
        id = id,
        emotion = emotion,
        date = date,
        time = time,
        activity = activity,
        note = note
)
