package com.franklinharper.chess

import com.franklinharper.chess.Board.Companion.endGameSetupForTesting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class GameState(
    val board: Board = Board(),
    val autoPlay: Boolean = false,
)

class ViewController {

    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state

    fun onSquareClick(
        colIndex: Int,
        rowIndex: Int,
    ) {
        println("clicked: col: $colIndex, row: $rowIndex")
        val gameState = _state.value
        val newBoard = gameState.board.clickSquare(
            colIndex = colIndex,
            rowIndex = rowIndex,
        )
        _state.value = gameState.copy(board = newBoard)
    }

    fun onNewGameClick() {
        _state.value = GameState()
    }

    fun onSetEndGamePositionClick() {
        _state.value = GameState(
            board = Board(
                squares = endGameSetupForTesting,
                moveColor = PieceColor.White,
            )
        )
    }

    fun onPromotionClick(piece: Piece, square: Square) {
        val gameState = _state.value
        val newBoard = gameState.board.promotePawn(
            piece = piece,
            square = square,
        )
        _state.value = gameState.copy(board = newBoard)
    }
}

private fun Board.promotePawn(piece: Piece, square: Square): Board {
    return this.replacePiece(newPiece = piece, square = square)
}

fun createViewModel(): ViewController = ViewController()