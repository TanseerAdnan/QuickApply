package com.t.quickapply.domain.usecase

import com.t.quickapply.domain.repository.ApplicationsRepository

class GetSentApplicationsUseCase(
    private val repository: ApplicationsRepository
) {

    operator fun invoke(userId: String) =
        repository.getApplications(userId)
}