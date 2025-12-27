package com.mainstation.app.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mainstation.app.data.model.Booking
import com.mainstation.app.data.repository.BookingRepository
import com.mainstation.app.data.repository.ConsoleRepository
import com.mainstation.app.data.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val consoleRepository: ConsoleRepository,
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings

    private val _consoles = MutableStateFlow<List<com.mainstation.app.data.model.Console>>(emptyList())
    val consoles: StateFlow<List<com.mainstation.app.data.model.Console>> = _consoles

    private val _rooms = MutableStateFlow<List<com.mainstation.app.data.model.Room>>(emptyList())
    val rooms: StateFlow<List<com.mainstation.app.data.model.Room>> = _rooms

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadAllData()
    }

    fun loadAllData() {
        loadAllBookings()
        loadConsoles()
        loadRooms()
    }

    fun loadAllBookings() {
        viewModelScope.launch {
            val result = bookingRepository.getAllBookings()
            if (result.isSuccess) {
                _bookings.value = result.getOrDefault(emptyList())
            }
        }
    }

    fun loadConsoles() {
        viewModelScope.launch {
            val result = consoleRepository.getConsoles()
            if (result.isSuccess) _consoles.value = result.getOrDefault(emptyList())
        }
    }

    fun loadRooms() {
        viewModelScope.launch {
            val result = roomRepository.getRooms()
            if (result.isSuccess) _rooms.value = result.getOrDefault(emptyList())
        }
    }

    fun addConsole(name: String, type: String, price: Double, stock: Int) {
        viewModelScope.launch {
            val console = com.mainstation.app.data.model.Console(
                id = "",
                name = name,
                type = type,
                pricePerHour = price,
                stock = stock
            )
            consoleRepository.addConsole(console)
            loadConsoles()
        }
    }

    fun seedData() {
        viewModelScope.launch {
            // 1. Wipe Database
            bookingRepository.deleteAllBookings()
            consoleRepository.deleteAllConsoles()
            roomRepository.deleteAllRooms()
            
            // 2. Seed Consoles
            val consoles = listOf(
                com.mainstation.app.data.model.Console(id = "ps5_standard", name = "PlayStation 5", type = "Standard", pricePerHour = 15000.0, stock = 5, status = "Available"),
                com.mainstation.app.data.model.Console(id = "ps5_digital", name = "PlayStation 5 Digital", type = "Digital", pricePerHour = 12000.0, stock = 3, status = "Available"),
                com.mainstation.app.data.model.Console(id = "ps4_pro", name = "PlayStation 4 Pro", type = "Pro", pricePerHour = 10000.0, stock = 4, status = "Available"),
                com.mainstation.app.data.model.Console(id = "xbox_series_x", name = "Xbox Series X", type = "Standard", pricePerHour = 15000.0, stock = 2, status = "Available")
            )
            
            consoles.forEach { consoleRepository.updateConsole(it) } // Use update (set) to overwrite/create

            // 3. Seed Rooms
            val rooms = listOf(
                com.mainstation.app.data.model.Room(id = "room_vip_a", name = "VIP Room A", capacity = 4, pricePerHour = 50000.0, description = "Private Room with 4K TV", isPrivate = true, imageUrl = null, facilities = listOf("AC", "Sofa")), 
                com.mainstation.app.data.model.Room(id = "room_regular", name = "Regular Open Space", capacity = 10, pricePerHour = 25000.0, description = "Open space gaming area", isPrivate = false, imageUrl = null, facilities = emptyList())
            )
            
            rooms.forEach { roomRepository.updateRoom(it) }
            
            // Reload
            loadAllData()
        }
    }

    fun updateConsole(console: com.mainstation.app.data.model.Console) {
        viewModelScope.launch {
            consoleRepository.updateConsole(console)
            loadConsoles()
        }
    }

    fun deleteConsole(id: String) {
        viewModelScope.launch {
            consoleRepository.deleteConsole(id)
            loadConsoles()
        }
    }

    fun addRoom(name: String, capacity: Int, price: Double, description: String) {
        viewModelScope.launch {
            val room = com.mainstation.app.data.model.Room(
                name = name, 
                capacity = capacity, 
                pricePerHour = price, 
                description = description, 
                id = "",
                isPrivate = false,
                imageUrl = null,
                facilities = emptyList()
            )
            roomRepository.addRoom(room)
            loadRooms()
        }
    }
    
    fun updateRoom(room: com.mainstation.app.data.model.Room) {
        viewModelScope.launch {
            roomRepository.updateRoom(room)
            loadRooms()
        }
    }

    fun deleteRoom(id: String) {
        viewModelScope.launch {
            roomRepository.deleteRoom(id)
            loadRooms()
        }
    }

    fun approveBooking(bookingId: String) {
        updateStatus(bookingId, "APPROVED")
    }

    fun rejectBooking(bookingId: String) {
        updateStatus(bookingId, "REJECTED")
    }

    private fun updateStatus(bookingId: String, status: String) {
        viewModelScope.launch {
            val result = bookingRepository.updateBookingStatus(bookingId, status)
            if (result.isSuccess) {
                loadAllBookings()
            } else {
                _error.value = "Failed to update status: ${result.exceptionOrNull()?.message}"
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}
