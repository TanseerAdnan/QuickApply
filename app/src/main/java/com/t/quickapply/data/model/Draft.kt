package com.t.quickapply.data.model

import com.google.firebase.firestore.PropertyName

data class Draft(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val body: String = "",

    @get:PropertyName("isSent")
    @set:PropertyName("isSent")
    var isSent: Boolean = false,

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)