package com.mainstation.app.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mainstation.app.data.DataSeeder
import com.mainstation.app.data.model.Booking
import com.mainstation.app.data.model.Console
import com.mainstation.app.data.model.Room
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
    private val roomRepository: RoomRepository,
    private val dataSeeder: DataSeeder
) : ViewModel() {

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings

    private val _consoles = MutableStateFlow<List<Console>>(emptyList())
    val consoles: StateFlow<List<Console>> = _consoles

    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms: StateFlow<List<Room>> = _rooms

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadAllData() {
        loadBookings()
        loadConsoles()
        loadRooms()
    }

    private fun loadBookings() {
        viewModelScope.launch {
            bookingRepository.getAllBookings().onSuccess {
                _bookings.value = it
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    private fun loadConsoles() {
        viewModelScope.launch {
            consoleRepository.getConsoles().onSuccess {
                _consoles.value = it
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    private fun loadRooms() {
        viewModelScope.launch {
            roomRepository.getRooms().onSuccess {
                _rooms.value = it
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun approveBooking(id: String) {
        viewModelScope.launch {
            bookingRepository.updateBookingStatus(id, "APPROVED").onSuccess {
                loadBookings()
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun rejectBooking(id: String) {
        viewModelScope.launch {
            bookingRepository.updateBookingStatus(id, "REJECTED").onSuccess {
                loadBookings()
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun addConsole(name: String, type: String, price: Double, stock: Int) {
        viewModelScope.launch {
            val console = Console(id = "", name = name, type = type, pricePerHour = price, stock = stock)
            consoleRepository.addConsole(console).onSuccess {
                loadConsoles()
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun updateConsole(console: Console) {
        viewModelScope.launch {
            consoleRepository.updateConsole(console).onSuccess {
                loadConsoles()
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun deleteConsole(id: String) {
        viewModelScope.launch {
            consoleRepository.deleteConsole(id).onSuccess {
                loadConsoles()
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun addRoom(name: String, capacity: Int, price: Double, description: String) {
        viewModelScope.launch {
            val room = Room(
                id = "",
                name = name,
                description = description,
                capacity = capacity,
                pricePerHour = price,
                isPrivate = false,
                imageUrl = null,
                facilities = emptyList()
            )
            roomRepository.addRoom(room).onSuccess {
                loadRooms()
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun updateRoom(room: Room) {
        viewModelScope.launch {
            roomRepository.updateRoom(room).onSuccess {
                loadRooms()
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun deleteRoom(id: String) {
        viewModelScope.launch {
            roomRepository.deleteRoom(id).onSuccess {
                loadRooms()
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun seedData() {
        dataSeeder.seedData { success ->
            if (success) {
                loadAllData()
                _error.value = "Data Seeded Successfully!"
            } else {
                _error.value = "Seed Failed: Check Permissions"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
