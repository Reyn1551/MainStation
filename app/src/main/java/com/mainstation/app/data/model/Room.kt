package com.mainstation.app.data.model

data class Room(
    val id: String,
    val name: String,
    val description: String,
    val capacity: Int,
    val pricePerHour: Double,
    val isPrivate: Boolean,
    val imageUrl: String?,
    val facilities: List<String>
)
