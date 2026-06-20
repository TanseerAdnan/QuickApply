package com.t.quickapply.domain.repository

import com.t.quickapply.domain.model.SentApplication
import kotlinx.coroutines.flow.Flow

interface ApplicationsRepository {

    suspend fun addApplication(
        application: SentApplication
    )

    fun getApplications(
        userId: String
    ): Flow<List<SentApplication>>
}