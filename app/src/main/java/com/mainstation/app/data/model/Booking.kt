package com.mainstation.app.data.model

import java.util.Date

data class Booking(
    val id: String,
    val userId: String,
    val consoleId: String?,
    val roomId: String?,
    val startTime: Date,
    val endTime: Date,
    val duration: Int,
    val total: Double,
    val status: String, // PENDING, CONFIRMED, CANCELLED
    val location: String
)
