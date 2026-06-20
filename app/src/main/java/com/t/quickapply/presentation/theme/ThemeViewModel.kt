package com.t.quickapply.presentation.theme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val themePreferences = ThemePreferences(application.applicationContext)

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    init {
        // Load saved preference on start
        viewModelScope.launch {
            themePreferences.isDarkTheme.collect { saved ->
                _isDarkTheme.value = saved
            }
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            val newValue = !_isDarkTheme.value
            _isDarkTheme.value = newValue
            themePreferences.setDarkTheme(newValue)  // persist it
        }
    }
}