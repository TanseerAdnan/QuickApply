package com.t.quickapply.presentation.profile

import androidx.lifecycle.ViewModel
import com.t.quickapply.domain.usecase.AuthUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            userName = authUseCase.getUserName() ?: "User",
            userEmail = authUseCase.getUserEmail()
        )
    )

    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun showSignOutDialog() {
        _uiState.update { it.copy(showSignOutDialog = true) }
    }

    fun hideSignOutDialog() {
        _uiState.update { it.copy(showSignOutDialog = false) }
    }

    fun signOut(onSignedOut: () -> Unit) {
        authUseCase.signOut {
            onSignedOut()
        }
    }
}