package com.example.SuaraRasa_A209008

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AdviceViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdviceViewModel::class.java)) {
            return AdviceViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
