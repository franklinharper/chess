package com.franklinharper.chess

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ViewModel {
    fun onSquareClick(rowIndex: Int, colIndex: Int) {
//        _state.value = newState
        println("clicked: col: $colIndex, row: $rowIndex")
    }

    private val _state = MutableStateFlow(Board())
    val state: StateFlow<Board> = _state
}

fun createViewModel(): ViewModel = ViewModel()