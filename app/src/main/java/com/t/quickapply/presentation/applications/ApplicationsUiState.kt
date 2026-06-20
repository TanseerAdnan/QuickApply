package com.t.quickapply.presentation.applications

import com.t.quickapply.domain.model.SentApplication

data class ApplicationsUiState(

    val isLoading: Boolean = true,

    val applications: List<SentApplication> = emptyList()
)