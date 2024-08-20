package com.franklinharper.chess

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ViewModel {
    fun onSquareClick(colIndex: Int, rowIndex: Int) {
        println("clicked: col: $colIndex, row: $rowIndex")
        val newBoard = _state.value.clickSquare(
            colIndex = colIndex,
            rowIndex = rowIndex,
        )
        _state.value = newBoard
    }

    private val _state = MutableStateFlow(Board())
    val state: StateFlow<Board> = _state
}

fun createViewModel(): ViewModel = ViewModel()