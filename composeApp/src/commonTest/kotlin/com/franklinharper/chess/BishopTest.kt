package com.franklinharper.chess

import com.franklinharper.chess.Piece.*
import com.franklinharper.chess.Piece.Companion.blackKingInitialCoordinates
import com.franklinharper.chess.Piece.Companion.whiteKingInitialCoordinates
import com.franklinharper.chess.PieceColor.*
import kotlin.test.Test
import kotlin.test.assertEquals

class BishopTest {

    @Test
    fun testAllMovesArePossible() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                // White
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(piece = Bishop(White), coordinates = Coordinates(col = 3, row = 3)),
                // Black
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
            )
        )
        assertEquals(
            expected = setOf(
                // Upper Left diagonal
                Coordinates(col = 2, row = 2), Coordinates(col = 1, row = 1), Coordinates(col = 0, row = 0),
                // Upper Right diagonal
                Coordinates(col = 4, row = 2), Coordinates(col = 5, row = 1),  Coordinates(col = 6, row = 0),
                // Lower Left diagonal
                Coordinates(col = 2, row = 4), Coordinates(col = 1, row = 5), Coordinates(col = 0, row = 6),
                // Lower Right diagonal
                Coordinates(col = 4, row = 4), Coordinates(col = 5, row = 5), Coordinates(col = 6, row = 6), Coordinates(col = 7, row = 7),
            ),
            actual = findValidMoves(board, Coordinates(col = 3, row = 3))
        )
    }

    @Test
    fun testFriendlyPiecesBlockMoves() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                // White
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(piece = Bishop(White), coordinates = Coordinates(col = 3, row = 3)),
                // Upper Left diagonal
                Square(piece = Pawn(White), coordinates = Coordinates(col = 1, row = 1)),
                // Upper Right diagonal
                Square(piece = Pawn(White), coordinates = Coordinates(col = 5, row = 1)),
                // Lower Left diagonal
                Square(piece = Pawn(White), coordinates = Coordinates(col = 1, row = 5)),
                // Lower Right diagonal
                Square(piece = Pawn(White), coordinates = Coordinates(col = 5, row = 5)),

                // Black
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
            )
        )
        assertEquals(
            expected = setOf(
                // Upper Left diagonal
                Coordinates(col = 2, row = 2),
                // Upper Right diagonal
                Coordinates(col = 4, row = 2),
                // Lower Left diagonal
                Coordinates(col = 2, row = 4),
                // Lower Right diagonal
                Coordinates(col = 4, row = 4),
            ),
            actual = findValidMoves(board, Coordinates(col = 3, row = 3))
        )
    }

    @Test
    fun testMovesAreBlockedAfterEnemyPieces() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                // White
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(piece = Bishop(White), coordinates = Coordinates(col = 3, row = 3)),

                // Black
                // Upper Left diagonal
                Square(piece = Pawn(Black), coordinates = Coordinates(col = 1, row = 1)),
                // Upper Right diagonal
                Square(piece = Pawn(Black), coordinates = Coordinates(col = 5, row = 1)),
                // Lower Left diagonal
                Square(piece = Pawn(Black), coordinates = Coordinates(col = 1, row = 5)),
                // Lower Right diagonal
                Square(piece = Pawn(Black), coordinates = Coordinates(col = 5, row = 5)),
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
            )
        )
        assertEquals(
            expected = setOf(
                // Upper Left diagonal
                Coordinates(col = 2, row = 2), Coordinates(col = 1, row = 1),
                // Upper Right diagonal
                Coordinates(col = 4, row = 2), Coordinates(col = 5, row = 1),
                // Lower Left diagonal
                Coordinates(col = 2, row = 4), Coordinates(col = 1, row = 5),
                // Lower Right diagonal
                Coordinates(col = 4, row = 4), Coordinates(col = 5, row = 5),
            ),
            actual = findValidMoves(board, Coordinates(col = 3, row = 3))
        )
    }

    @Test
    fun testCornerCaseAndNoPossibleMoves() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                // White
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(piece = Bishop(White), coordinates = Coordinates(col = 7, row = 7)),
                Square(piece = Pawn(White), coordinates = Coordinates(col = 6, row = 6)),

                // Black
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
            )
        )
        assertEquals(
            expected =  emptySet(),
            actual = findValidMoves(
                board = board,
                coordinates = Coordinates(col = 7, row = 7)
            )
        )
    }

    @Test
    fun testMoveCantPutKingInCheck() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                // White
                Square(piece = King(White), coordinates = Coordinates(col = 0, row = 0)),
                Square(piece = Bishop(White), coordinates = Coordinates(col = 0, row = 1)),
                // Black
                Square(piece = Rook(Black), coordinates = Coordinates(col = 0, row = 2)),
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
            )
        )
        assertEquals(
            expected = emptySet(),
            actual = findValidMoves(
                board = board,
                coordinates = Coordinates(col = 0, row = 1),
            )
        )
    }

    @Test
    fun testMoveDefendsKingFromCheck() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                // White
                Square(piece = King(White), coordinates = Coordinates(col = 0, row = 0)),
                Square(piece = Bishop(White), coordinates = Coordinates(col = 1, row = 0)),
                // Black
                Square(piece = Rook(Black), coordinates = Coordinates(col = 0, row = 2)),
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
            )
        )
        assertEquals(
            expected = setOf(Coordinates(col = 0, row = 1)),
            actual = findValidMoves(
                board = board,
                coordinates = Coordinates(col = 1, row = 0),
            )
        )
    }

    @Test
    fun testCaptureRemovesCheckOnKing() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                // White
                Square(piece = King(White), coordinates = Coordinates(col = 0, row = 0)),
                Square(piece = Bishop(White), coordinates = Coordinates(col = 2, row = 0)),
                // Black
                Square(piece = Rook(Black), coordinates = Coordinates(col = 0, row = 2)),
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
            )
        )
        assertEquals(
            expected = setOf(Coordinates(col = 0, row = 2)),
            actual = findValidMoves(
                board = board,
                coordinates = Coordinates(col = 2, row = 0),
            )
        )
    }
}