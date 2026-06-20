package com.t.quickapply.data.remote.firebase

import android.content.Context
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.common.api.Scope

class GoogleAuthClient(private val context: Context) {

    private val auth = FirebaseAuth.getInstance()

    private val googleSignInClient: GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(com.t.quickapply.R.string.default_web_client_id))
            .requestScopes(Scope(com.google.api.services.gmail.GmailScopes.GMAIL_SEND))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getSignInIntent() = googleSignInClient.signInIntent

    fun handleSignInResult(data: android.content.Intent?, onResult: (Boolean) -> Unit) {

        val task = GoogleSignIn.getSignedInAccountFromIntent(data)

        try {
            val account = task.getResult(ApiException::class.java)

            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { authTask ->

                    if (authTask.isSuccessful) {
                        onResult(true)
                    } else {
                        onResult(false)
                    }
                }

        } catch (e: Exception) {
            onResult(false)
        }
    }

    fun signOut(onComplete: () -> Unit) {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            onComplete()
        }
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getUserName(): String {
        return auth.currentUser?.displayName ?: "User"
    }

    fun getUserEmail(): String {
        return auth.currentUser?.email ?: "No email"
    }
}