package com.t.quickapply.presentation.home

import com.t.quickapply.data.local.CvEntry

data class SendApplicationDialogState(
    val isVisible: Boolean = false,
    val draftId: String = "",

    val hrName: String = "",
    val hrCompany: String = "",
    val hrEmail: String = "",

    val hrNameError: String? = null,
    val hrCompanyError: String? = null,
    val hrEmailError: String? = null,

    val isSending: Boolean = false,
    val sendError: String? = null,

    // CV attachment
    val attachCv: Boolean = false,              // toggle state
    val savedCvList: List<CvEntry> = emptyList(), // loaded from CvRepository
    val selectedCvId: String? = null,           // which CV is selected
    val cvError: String? = null,            // shown if toggle on but no CVs saved

    val useEnhancedVersion: Boolean = true  // ← new
)