package com.franklinharper.chess

enum class PieceColor {
    White,
    Black;

    fun enemyColor() = if (this == White) Black else White
}