package com.t.quickapply.domain.repository

import com.t.quickapply.data.model.Draft
import kotlinx.coroutines.flow.Flow

interface DraftRepository {
    fun getDrafts(userId: String): Flow<List<Draft>>
    suspend fun addDraft(draft: Draft): Result<Unit>
    suspend fun updateDraft(draft: Draft): Result<Unit>
    suspend fun deleteDraft(userId: String, draftId: String): Result<Unit>
}