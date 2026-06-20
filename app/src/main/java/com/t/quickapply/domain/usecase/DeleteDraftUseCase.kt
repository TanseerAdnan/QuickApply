package com.t.quickapply.domain.usecase

import com.t.quickapply.domain.repository.DraftRepository

class DeleteDraftUseCase(private val repository: DraftRepository) {
    suspend operator fun invoke(userId: String, draftId: String): Result<Unit> {
        return repository.deleteDraft(userId, draftId)
    }
}