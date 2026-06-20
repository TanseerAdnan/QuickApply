package com.t.quickapply.data.repository

import com.t.quickapply.data.remote.firebase.GoogleAuthClient
import com.t.quickapply.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val googleAuthClient: GoogleAuthClient
) : AuthRepository {

    override fun getUserName(): String? {
        return googleAuthClient.getUserName()
    }

    override fun getUserEmail(): String? {
        return googleAuthClient.getUserEmail()
    }

    override fun isUserLoggedIn(): Boolean {
        return googleAuthClient.isUserLoggedIn()
    }

    override fun signOut(onComplete: () -> Unit) = googleAuthClient.signOut(onComplete)
}