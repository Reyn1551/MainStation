package com.mainstation.app.ui.screens.profile

import androidx.lifecycle.viewModelScope
import com.mainstation.app.data.model.Booking
import com.mainstation.app.data.repository.AuthRepository
import com.mainstation.app.data.repository.BookingRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mainstation.app.data.DataSeeder
import com.mainstation.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository,
    private val userRepository: UserRepository,
    private val dataSeeder: DataSeeder
) : ViewModel() {

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings
    
    private val _seedResult = MutableStateFlow<String?>(null)
    val seedResult: StateFlow<String?> = _seedResult
    
    private val _fullName = MutableStateFlow<String>("Loading...")
    val fullName: StateFlow<String> = _fullName

    init {
        loadProfileData()
    }

    fun logout() {
        authRepository.logout()
    }

    fun loadProfileData() {
        val user = authRepository.getCurrentUser()
        if (user != null) {
            viewModelScope.launch {
                // Fetch Name
                _fullName.value = userRepository.getUserFullName(user.id)
                
                val bookingResult = bookingRepository.getUserBookings(user.id)
                if (bookingResult.isSuccess) {
                    _bookings.value = bookingResult.getOrDefault(emptyList())
                }
            }
        }
    }
    

    
    fun seedData() {
        dataSeeder.seedData { success ->
            if (success) {
                _seedResult.value = "Data Seeded Successfully!"
                loadProfileData() // Reload if needed
            } else {
                _seedResult.value = "Failed to seed data."
            }
        }
    }
    
    fun clearSeedMessage() {
        _seedResult.value = null
    }
}
