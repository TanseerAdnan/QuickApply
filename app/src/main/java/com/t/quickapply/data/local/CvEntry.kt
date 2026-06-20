package com.t.quickapply.data.local

import kotlinx.serialization.Serializable

@Serializable
data class CvEntry(
    val id: String,
    val fileName: String,
    val uriString: String,
    val addedAt: Long,
    val enhancedFilePath: String? = null,
    val showingEnhanced: Boolean = false
)