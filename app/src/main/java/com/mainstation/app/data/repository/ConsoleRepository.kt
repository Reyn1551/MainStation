package com.mainstation.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mainstation.app.data.model.Console
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConsoleRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getConsoles(): Result<List<Console>> {
        return try {
            val snapshot = firestore.collection("consoles").get().await()
            val consoles = snapshot.documents.mapNotNull { doc ->
                try {
                    Console(
                        id = doc.id,
                        name = doc.getString("name") ?: "Unknown Console",
                        type = doc.getString("type") ?: "PS5",
                        pricePerHour = doc.getDouble("pricePerHour") ?: 0.0,
                        status = doc.getString("status") ?: "Available",
                        imageUrl = doc.getString("imageUrl"),
                        stock = doc.getLong("stock")?.toInt() ?: 5
                    )
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(consoles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addConsole(console: Console): Result<Unit> {
        return try {
            val consoleMap = hashMapOf(
                "name" to console.name,
                "type" to console.type,
                "pricePerHour" to console.pricePerHour,
                "status" to console.status,
                "imageUrl" to console.imageUrl,
                "stock" to console.stock
            )
            firestore.collection("consoles").add(consoleMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateConsole(console: Console): Result<Unit> {
        return try {
            val consoleMap = hashMapOf(
                "name" to console.name,
                "type" to console.type,
                "pricePerHour" to console.pricePerHour,
                "status" to console.status,
                "imageUrl" to console.imageUrl,
                "stock" to console.stock
            )
            firestore.collection("consoles").document(console.id).set(consoleMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteConsole(consoleId: String): Result<Unit> {
        return try {
            firestore.collection("consoles").document(consoleId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAllConsoles(): Result<Unit> {
        return try {
            val snapshot = firestore.collection("consoles").get().await()
            val batch = firestore.batch()
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
