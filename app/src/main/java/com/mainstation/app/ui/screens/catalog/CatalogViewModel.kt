package com.mainstation.app.ui.screens.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mainstation.app.data.model.Console
import com.mainstation.app.data.repository.ConsoleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val consoleRepository: ConsoleRepository
) : ViewModel() {

    private val _consoles = MutableStateFlow<List<Console>>(emptyList())
    val consoles: StateFlow<List<Console>> = _consoles
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadConsoles()
    }

    fun loadConsoles() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = consoleRepository.getConsoles()
            if (result.isSuccess) {
                _consoles.value = result.getOrDefault(emptyList())
            }
            _isLoading.value = false
        }
    }
}
