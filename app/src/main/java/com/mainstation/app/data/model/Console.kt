package com.mainstation.app.data.model

data class Console(
    val id: String = "",
    val name: String,
    val type: String, // "PS4", "PS5"
    val pricePerHour: Double,
    val status: String = "Available", // "Available", "Maintenance", "Booked"
    val imageUrl: String? = null,
    val stock: Int = 5 // Default stock
)
