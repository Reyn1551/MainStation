package com.mainstation.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getUserPoints(userId: String): Int {
        return try {
            val snapshot = firestore.collection("users").document(userId).get().await()
            snapshot.getLong("points")?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }

    suspend fun getUserFullName(userId: String): String {
        return try {
            val snapshot = firestore.collection("users").document(userId).get().await()
            snapshot.getString("fullName") ?: "Unknown User"
        } catch (e: Exception) {
            "Unknown User"
        }
    }
    
    // Additional methods to sync user profile data if needed
}
