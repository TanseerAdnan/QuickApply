package com.t.quickapply.domain.repository

interface AuthRepository {
    fun getUserName(): String?
    fun getUserEmail(): String?
    fun isUserLoggedIn(): Boolean
    fun signOut(onComplete: () -> Unit)
}