package com.t.quickapply.presentation.applications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.t.quickapply.domain.usecase.GetSentApplicationsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ApplicationsViewModel(

    private val getSentApplicationsUseCase: GetSentApplicationsUseCase

) : ViewModel() {

    private val userId =
        FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _uiState =
        MutableStateFlow(ApplicationsUiState())

    val uiState: StateFlow<ApplicationsUiState> =
        _uiState.asStateFlow()

    init {

        loadApplications()
    }

    private fun loadApplications() {

        viewModelScope.launch {

            getSentApplicationsUseCase(userId)
                .collect { applications ->

                    _uiState.update {

                        it.copy(
                            applications = applications,
                            isLoading = false
                        )
                    }
                }
        }
    }
}