package com.franklinharper.chess

data class Square(
    val coordinates: Coordinates,
    val piece: Piece?,

    // UI shows this square highlighted
    val isSelected: Boolean = false,

    // UI shows this square as a valid move destination
    // for the currently selected piece.
    val isValidMoveDestination: Boolean = false,
) {
    val isNotSelected = !isSelected
    fun containsFriendlyPiece(friendlyColor: PieceColor) = piece?.color == friendlyColor
    fun isEmpty() = piece == null
    fun isNotEmpty() = piece != null
    fun containsEnemyPiece(friendlyColor: PieceColor) = piece?.color == friendlyColor.enemyColor()
}
