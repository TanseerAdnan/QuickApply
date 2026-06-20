package com.t.quickapply.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.t.quickapply.domain.model.SentApplication
import com.t.quickapply.domain.repository.ApplicationsRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ApplicationsRepositoryImpl(
    private val firestore: FirebaseFirestore
) : ApplicationsRepository {

    override suspend fun addApplication(
        application: SentApplication
    ) {

        val collection = firestore
            .collection("users")
            .document(application.userId)
            .collection("sent_applications")

        // Keep max 20
        val snapshot = collection
            .orderBy("sentTime", Query.Direction.ASCENDING)
            .get()
            .await()

        if (snapshot.size() >= 20) {

            snapshot.documents.firstOrNull()
                ?.reference
                ?.delete()
                ?.await()
        }

        collection
            .document(application.id)
            .set(application)
            .await()
    }

    override fun getApplications(
        userId: String
    ): Flow<List<SentApplication>> = callbackFlow {

        val listener = firestore
            .collection("users")
            .document(userId)
            .collection("sent_applications")
            .orderBy("sentTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->

                val applications =
                    snapshot?.documents?.mapNotNull {
                        it.toObject(SentApplication::class.java)
                    } ?: emptyList()

                trySend(applications)
            }

        awaitClose {
            listener.remove()
        }
    }
}