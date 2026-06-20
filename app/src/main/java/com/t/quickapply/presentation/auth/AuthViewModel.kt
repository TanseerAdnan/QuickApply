package com.t.quickapply.presentation.auth

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import com.t.quickapply.data.remote.firebase.GoogleAuthClient

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val googleAuth = GoogleAuthClient(application.applicationContext)

    val signInIntent = googleAuth.getSignInIntent()

    fun handleResult(
        data: Intent?,
        onSuccess: () -> Unit,
        onFail: () -> Unit
    ) {
        googleAuth.handleSignInResult(data) { success ->
            if (success) onSuccess() else onFail()
        }
    }
}