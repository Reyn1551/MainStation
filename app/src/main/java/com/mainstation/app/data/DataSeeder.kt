package com.mainstation.app.data

import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataSeeder @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun seedData(onComplete: (Boolean) -> Unit) {
        val rooms = listOf(
            hashMapOf(
                "name" to "VIP Suite A",
                "description" to "Private room with large TV, Sofa, and PS5",
                "capacity" to 4,
                "pricePerHour" to 50000.0,
                "isPrivate" to true,
                "imageUrl" to "https://example.com/vip_a.jpg",
                "facilities" to listOf("AC", "Sofa", "70 inch TV", "PS5")
            ),
            hashMapOf(
                "name" to "VIP Suite B",
                "description" to "Cozy private room for couples",
                "capacity" to 2,
                "pricePerHour" to 40000.0,
                "isPrivate" to true,
                "imageUrl" to "https://example.com/vip_b.jpg",
                "facilities" to listOf("AC", "Bean Bags", "55 inch TV", "PS5")
            ),
            hashMapOf(
                "name" to "Regular Open Space",
                "description" to "Public gaming area with high-refresh rate monitors",
                "capacity" to 20,
                "pricePerHour" to 20000.0,
                "isPrivate" to false,
                "imageUrl" to "https://example.com/regular.jpg",
                "facilities" to listOf("AC", "Fast Wifi", "Gaming Monitor", "PS4/PS5")
            )
        )

        val batch = firestore.batch()

        rooms.forEach { room ->
            val docRef = firestore.collection("rooms").document()
            batch.set(docRef, room)
        }

        // Add Consoles with STOCK field
        val consoles = listOf(
             hashMapOf(
                "name" to "PlayStation 5",
                "type" to "Console",
                "status" to "Available",
                "pricePerHour" to 15000.0,
                "stock" to 10 // Added Stock
            ),
             hashMapOf(
                "name" to "PlayStation 4 Pro",
                "type" to "Console",
                "status" to "Available",
                "pricePerHour" to 10000.0,
                "stock" to 15 // Added Stock
            ) 
        )
        
        consoles.forEach { console ->
            val docRef = firestore.collection("consoles").document()
            batch.set(docRef, console)
        }

        batch.commit()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }
}
