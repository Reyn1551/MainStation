package com.mainstation.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mainstation.app.data.model.Booking
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun createBooking(booking: Booking): Result<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                // 1. Stock Check Logic
                if (!booking.consoleId.isNullOrEmpty()) {
                    val consoleRef = firestore.collection("consoles").document(booking.consoleId)
                    val snapshot = transaction.get(consoleRef)
                    val currentStock = snapshot.getLong("stock")?.toInt() ?: 0
                    
                    if (currentStock <= 0) {
                        throw Exception("Console Out of Stock!")
                    }
                    
                    // Decrement Stock
                    transaction.update(consoleRef, "stock", currentStock - 1)
                } 
                else if (!booking.roomId.isNullOrEmpty()) {
                    // Room Logic - Check Availability status
                    // Note: Ideally rooms are timeslot based, but for simple "status" check:
                    val roomRef = firestore.collection("rooms").document(booking.roomId)
                    val snapshot = transaction.get(roomRef)
                    // If room has "status" or "isAvailable" logic, check here. 
                    // Assuming strictly "Available" for now if status field exists.
                    // For now, we trust the booking flow, or you can add specific room checks.
                }

                // 2. Create Booking
                val bookingRef = firestore.collection("bookings").document()
                val finalBooking = booking.copy(id = bookingRef.id)
                
                val bookingMap = hashMapOf(
                    "userId" to finalBooking.userId,
                    "consoleId" to finalBooking.consoleId,
                    "roomId" to finalBooking.roomId,
                    "startTime" to finalBooking.startTime,
                    "endTime" to finalBooking.endTime,
                    "duration" to finalBooking.duration,
                    "total" to finalBooking.total,
                    "status" to finalBooking.status,
                    "location" to finalBooking.location
                )
                
                transaction.set(bookingRef, bookingMap)
            }.await()
                
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserBookings(userId: String): Result<List<Booking>> {
        return try {
            val snapshot = firestore.collection("bookings")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val bookings = snapshot.documents.mapNotNull { doc ->
                try {
                    Booking(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        consoleId = doc.getString("consoleId"),
                        roomId = doc.getString("roomId"),
                        startTime = doc.getDate("startTime") ?: Date(),
                        endTime = doc.getDate("endTime") ?: Date(),
                        duration = doc.getLong("duration")?.toInt() ?: 0,
                        total = doc.getDouble("total") ?: 0.0,
                        status = doc.getString("status") ?: "PENDING",
                        location = doc.getString("location") ?: "Unknown"
                    )
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(bookings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllBookings(): Result<List<Booking>> {
        return try {
            val snapshot = firestore.collection("bookings")
                .orderBy("startTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val bookings = snapshot.documents.mapNotNull { doc ->
                try {
                    Booking(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        consoleId = doc.getString("consoleId"),
                        roomId = doc.getString("roomId"),
                        startTime = doc.getDate("startTime") ?: Date(),
                        endTime = doc.getDate("endTime") ?: Date(),
                        duration = doc.getLong("duration")?.toInt() ?: 0,
                        total = doc.getDouble("total") ?: 0.0,
                        status = doc.getString("status") ?: "PENDING",
                        location = doc.getString("location") ?: "Unknown"
                    )
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(bookings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBookingStatus(bookingId: String, status: String): Result<Unit> {
        return try {
            firestore.collection("bookings")
                .document(bookingId)
                .update("status", status)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAllBookings(): Result<Unit> {
        return try {
            val snapshot = firestore.collection("bookings").get().await()
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