package com.franklinharper.chess

data class Square(
    val coordinates: Coordinates,
    val piece: Piece?,
) {
    fun containsFriendlyPiece(friendlyColor: PieceColor) = piece?.color == friendlyColor
    fun isEmpty() = piece == null
    fun isNotEmpty() = piece != null
    fun containsEnemyPiece(friendlyColor: PieceColor) = piece?.color == friendlyColor.enemyColor()
}
