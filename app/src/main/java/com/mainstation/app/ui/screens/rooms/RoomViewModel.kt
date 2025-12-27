package com.mainstation.app.ui.screens.rooms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mainstation.app.data.DataSeeder
import com.mainstation.app.data.model.Room
import com.mainstation.app.data.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val repository: RoomRepository,
    private val dataSeeder: DataSeeder
) : ViewModel() {

    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms: StateFlow<List<Room>> = _rooms

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadRooms()
    }

    fun loadRooms() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getRooms()
            if (result.isSuccess) {
                val list = result.getOrDefault(emptyList())
                if (list.isEmpty()) {
                    // Auto-seed if empty
                    dataSeeder.seedData { success ->
                        if (success) loadRooms() // Retry
                        else _rooms.value = emptyList()
                    }
                } else {
                    _rooms.value = list
                }
            } else {
                // Handle error
            }
            _isLoading.value = false
        }
    }
}
