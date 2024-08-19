package com.franklinharper.chess

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ViewModel {
    private val _state = MutableStateFlow(value = 0)
    val state: StateFlow<Int> = _state

    fun updateState(newState: Int) {
        _state.value = newState
    }
}

fun createViewModel(): ViewModel = ViewModel()