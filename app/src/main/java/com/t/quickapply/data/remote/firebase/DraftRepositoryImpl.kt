package com.t.quickapply.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.t.quickapply.data.model.Draft
import com.t.quickapply.domain.repository.DraftRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class DraftRepositoryImpl : DraftRepository {

    private val firestore = FirebaseFirestore.getInstance()

    private fun userDraftsCollection(userId: String) =
        firestore.collection("drafts").document(userId).collection("userDrafts")

    override fun getDrafts(userId: String): Flow<List<Draft>> = callbackFlow {
        val listener = userDraftsCollection(userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close()
                    return@addSnapshotListener
                }
                val drafts = snapshot?.documents?.mapNotNull {
                    it.toObject(Draft::class.java)
                } ?: emptyList()
                trySend(drafts)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addDraft(draft: Draft): Result<Unit> {
        return try {
            userDraftsCollection(draft.userId)
                .document(draft.id)
                .set(draft)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateDraft(draft: Draft): Result<Unit> {
        return try {
            userDraftsCollection(draft.userId)
                .document(draft.id)
                .set(draft)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteDraft(userId: String, draftId: String): Result<Unit> {
        return try {
            userDraftsCollection(userId)
                .document(draftId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}