package com.t.quickapply.domain.usecase

import com.t.quickapply.domain.model.SentApplication
import com.t.quickapply.domain.repository.ApplicationsRepository

class AddSentApplicationUseCase(
    private val repository: ApplicationsRepository
) {

    suspend operator fun invoke(
        application: SentApplication
    ) {
        repository.addApplication(application)
    }
}