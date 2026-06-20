package com.t.quickapply.domain.model

data class SentApplication(

    val id: String = "",

    val userId: String = "",

    val hrName: String = "",

    val companyName: String = "",

    val hrEmail: String = "",

    val subject: String = "",

    val sentTime: Long = System.currentTimeMillis()
)