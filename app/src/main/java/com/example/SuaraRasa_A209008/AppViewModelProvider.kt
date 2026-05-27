package com.example.SuaraRasa_A209008

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.SuaraRasa_A209008.item.MoodEditViewModel
import com.example.SuaraRasa_A209008.item.MoodEntryViewModel
import com.example.SuaraRasa_A209008.item.MoodDetailsViewModel
import com.example.SuaraRasa_A209008.home.HomeViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for MoodEditViewModel
        initializer {
            MoodEditViewModel(
                    this.createSavedStateHandle(),
                    moodTrackerApplication().container.moodRepository
                             )
        }

        // Initializer for MoodEntryViewModel
        initializer {
            MoodEntryViewModel(moodTrackerApplication().container.moodRepository)
        }

        // Initializer for MoodDetailsViewModel
        initializer {
            MoodDetailsViewModel(
                    this.createSavedStateHandle(),
                    moodTrackerApplication().container.moodRepository
                                )
        }

        // Initializer for HomeViewModel
        initializer {
            HomeViewModel(moodTrackerApplication().container.moodRepository)
        }
    }
}

/**
 * Extension function to retrieve [MoodTrackerApplication] from [CreationExtras]
 */
fun CreationExtras.moodTrackerApplication(): MoodTrackerApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MoodTrackerApplication)
