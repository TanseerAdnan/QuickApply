package com.t.quickapply.presentation.applications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.t.quickapply.domain.usecase.GetSentApplicationsUseCase

class ApplicationsViewModelFactory(
    private val getApplicationsUseCase: GetSentApplicationsUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ApplicationsViewModel::class.java)) {

            return ApplicationsViewModel(
                getApplicationsUseCase
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}