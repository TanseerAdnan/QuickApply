package com.t.quickapply.domain.usecase

import com.t.quickapply.data.model.Draft
import com.t.quickapply.domain.repository.DraftRepository

class AddDraftUseCase(private val repository: DraftRepository) {
    suspend operator fun invoke(draft: Draft): Result<Unit> {
        return repository.addDraft(draft)
    }
}