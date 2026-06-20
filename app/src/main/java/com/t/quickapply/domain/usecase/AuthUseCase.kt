package com.t.quickapply.domain.usecase

import com.t.quickapply.domain.repository.AuthRepository

class AuthUseCase(private val repository: AuthRepository) {

    fun getUserName(): String? = repository.getUserName()

    fun getUserEmail(): String = repository.getUserEmail() ?: ""

    fun isUserLoggedIn(): Boolean = repository.isUserLoggedIn()

    fun signOut(onComplete: () -> Unit) = repository.signOut(onComplete)
}