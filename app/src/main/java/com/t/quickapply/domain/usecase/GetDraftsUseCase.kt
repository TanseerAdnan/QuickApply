package com.t.quickapply.domain.usecase

import com.t.quickapply.data.model.Draft
import com.t.quickapply.domain.repository.DraftRepository
import kotlinx.coroutines.flow.Flow

class GetDraftsUseCase(private val repository: DraftRepository) {
    operator fun invoke(userId: String): Flow<List<Draft>> {
        return repository.getDrafts(userId)
    }
}