package com.franklinharper.chess

import com.franklinharper.chess.PieceColor.*

enum class BoardStatus {
    WhitesMove,
    BlacksMove,
    WhiteWin,
    BlackWin,
    Stalemate,
    // TODO: add draw
}

fun BoardStatus.toColor() = when(this) {
    BoardStatus.WhitesMove -> White
    BoardStatus.BlacksMove -> Black
    else -> null
}
