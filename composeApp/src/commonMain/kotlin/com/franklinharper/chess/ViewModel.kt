package com.franklinharper.chess

import com.franklinharper.chess.Board.Companion.endGameSetupForTesting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ViewModel {
    fun onSquareClick(
        colIndex: Int,
        rowIndex: Int,
    ) {
        println("clicked: col: $colIndex, row: $rowIndex")
        val newBoard = _state.value.clickSquare(
            colIndex = colIndex,
            rowIndex = rowIndex,
        )
        _state.value = newBoard
    }

    fun onNewGameClick() {
        _state.value = Board()
    }

    fun onSetEndGamePositionClick() {
        _state.value = Board(
            squares = endGameSetupForTesting,
            moveColor = PieceColor.White,
        )
    }

    fun onPromotionClick(piece: Piece, square: Square) {
        val newBoard = _state.value.promotePawn(
            piece = piece,
            square = square,
        )
        _state.value = newBoard
    }

    private val _state = MutableStateFlow(Board())
    val state: StateFlow<Board> = _state
}

private fun Board.promotePawn(piece: Piece, square: Square): Board {
    return this.replacePiece(newPiece = piece, square = square)
}

fun createViewModel(): ViewModel = ViewModel()