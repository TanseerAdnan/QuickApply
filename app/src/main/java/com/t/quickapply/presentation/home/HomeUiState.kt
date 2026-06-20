package com.t.quickapply.presentation.home

data class HomeUiState(
    val drafts: List<DraftCard> = emptyList(),
    val isLoading: Boolean = true,
    val sendDialogState: SendApplicationDialogState = SendApplicationDialogState(),
    val showEmailSentDialog: Boolean = false
)