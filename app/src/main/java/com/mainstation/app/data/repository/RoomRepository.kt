package com.mainstation.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mainstation.app.data.model.Room
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getRooms(): Result<List<Room>> {
        return try {
            val snapshot = firestore.collection("rooms").get().await()
            val rooms = snapshot.documents.mapNotNull { doc ->
                // Mapping Firestore doc to Room object. 
                // Assuming properly structured data in Firestore.
                // Handling rudimentary manual mapping for safety.
                try {
                    Room(
                        id = doc.id,
                        name = doc.getString("name") ?: "Unknown Room",
                        description = doc.getString("description") ?: "",
                        capacity = doc.getLong("capacity")?.toInt() ?: 0,
                        pricePerHour = doc.getDouble("pricePerHour") ?: 0.0,
                        isPrivate = doc.getBoolean("isPrivate") ?: false,
                        imageUrl = doc.getString("imageUrl"),
                        facilities = (doc.get("facilities") as? List<String>) ?: emptyList()
                    )
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(rooms)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addRoom(room: Room): Result<Unit> {
        return try {
            val roomMap = hashMapOf(
                "name" to room.name,
                "description" to room.description,
                "capacity" to room.capacity,
                "pricePerHour" to room.pricePerHour,
                "isPrivate" to room.isPrivate,
                "imageUrl" to room.imageUrl,
                "facilities" to room.facilities
            )

            val documentReference = if (room.id.isEmpty()) {
                firestore.collection("rooms").document()
            } else {
                firestore.collection("rooms").document(room.id)
            }

            documentReference.set(roomMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateRoom(room: Room): Result<Unit> {
        return try {
            val roomMap = hashMapOf(
                "name" to room.name,
                "description" to room.description,
                "capacity" to room.capacity,
                "pricePerHour" to room.pricePerHour,
                "isPrivate" to room.isPrivate,
                "imageUrl" to room.imageUrl,
                "facilities" to room.facilities
            )
            firestore.collection("rooms").document(room.id).set(roomMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRoom(roomId: String): Result<Unit> {
        return try {
            firestore.collection("rooms").document(roomId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAllRooms(): Result<Unit> {
        return try {
            val snapshot = firestore.collection("rooms").get().await()
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