package com.franklinharper.chess

import com.franklinharper.chess.Piece.*
import com.franklinharper.chess.Piece.Companion.blackKingInitialCoordinates
import com.franklinharper.chess.Piece.Companion.whiteKingInitialCoordinates
import com.franklinharper.chess.PieceColor.*
import kotlin.test.Test
import kotlin.test.assertEquals

class KnightTest {

    @Test
    fun testMovesToEmptySquaresArePossible() {
        val board = Board(
            setOf(
                Square(
                    piece = King(White),
                    coordinates = whiteKingInitialCoordinates
                ),
                Square(
                    piece = Knight(White),
                    coordinates = Coordinates(col = 2, row = 2)
                ),
            )
        )
        assertEquals(
            expected = setOf(
                // Above
                Coordinates(col = 1, row = 0), Coordinates(col = 3, row = 0),

                // Left
                Coordinates(col = 0, row = 1), Coordinates(col = 0, row = 3),

                // Right
                Coordinates(col = 4, row = 1), Coordinates(col = 4, row = 3),

                // Below
                Coordinates(col = 1, row = 4), Coordinates(col = 3, row = 4),
            ),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 2, row = 2))
        )
    }

    @Test
    fun testMovesToUnoccupiedSquaresArePossible() {
        val board = Board(
            setOf(
                Square(
                    piece = King(White),
                    coordinates = whiteKingInitialCoordinates,
                ),
                Square(
                    piece = Knight(White),
                    coordinates = Coordinates(col = 0, row = 1),
                ),
            )
        )
        assertEquals(
            expected = setOf(
                Coordinates(col = 1, row = 3),
                Coordinates(col = 2, row = 2),
                Coordinates(col = 2, row = 0),
            ),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 0, row = 1))
        )
    }

    @Test
    fun testMovesToEnemyOccupiedSquaresArePossible() {
        val board = Board(
            setOf(
                Square(
                    piece = King(White),
                    coordinates = whiteKingInitialCoordinates,
                ),
                Square(
                    piece = Knight(White),
                    coordinates = Coordinates(col = 0, row = 1)
                ),
                Square(
                    piece = Pawn(Black, twoSquareAdvanceOnPreviousMove = true),
                    coordinates = Coordinates(col = 1, row = 3)
                ),
            )
        )
        assertEquals(
            expected = setOf(
                Coordinates(col = 1, row = 3),
                Coordinates(col = 2, row = 2),
                Coordinates(col = 2, row = 0),
            ),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 0, row = 1))
        )
    }

    @Test
    fun testEdgesAndFriendlyPiecesBlockMoves() {
        val board = Board(
            setOf(
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(piece = Knight(White), coordinates = Coordinates(col = 0, row = 0)),
                Square(piece = Pawn(White, twoSquareAdvanceOnPreviousMove = true), coordinates = Coordinates(col = 1, row = 2)),
                Square(piece = Pawn(White, twoSquareAdvanceOnPreviousMove = true), coordinates = Coordinates(col = 2, row = 1)),
            )
        )
        assertEquals(
            expected = emptySet(),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 0, row = 0))
        )
    }

    @Test
    fun testMoveCantPutKingInCheck() {
        val board = Board(
            setOf(
                // White
                Square(piece = King(White), coordinates = Coordinates(col = 0, row = 0)),
                Square(piece = Knight(White), coordinates = Coordinates(col = 0, row = 1)),
                // Black
                Square(piece = Rook(Black), coordinates = Coordinates(col = 0, row = 2)),
            )
        )
        assertEquals(
            expected = emptySet(),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 0, row = 1))
        )
    }

    @Test
    fun testMoveDefendsKingFromCheck() {
        val board = Board(
            setOf(
                // White
                Square(piece = King(White), coordinates = Coordinates(col = 0, row = 0)),
                Square(piece = Knight(White), coordinates = Coordinates(col = 2, row = 0)),
                // Black
                Square(piece = Rook(Black), coordinates = Coordinates(col = 0, row = 2)),
            )
        )
        assertEquals(
            expected = setOf(Coordinates(col = 0, row = 1)),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 2, row = 0))
        )
    }

    @Test
    fun testCaptureRemovesCheckOnKing() {
        val board = Board(
            setOf(
                // White
                Square(piece = King(White), coordinates = Coordinates(col = 0, row = 0)),
                Square(piece = Knight(White), coordinates = Coordinates(col = 1, row = 0)),
                // Black
                Square(piece = Rook(Black), coordinates = Coordinates(col = 0, row = 2)),
            )
        )
        assertEquals(
            expected = setOf(Coordinates(col = 0, row = 2)),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 1, row = 0))
        )
    }

    @Test
    fun testKnightCantCaptureFriendlyKing() {
        // This test reproduces a bug in move validation where the friendly king was removed
        // from the board. This would cause a null pointer exception to be thrown.
        val board = Board(
            setOf(
                // White
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(piece = Knight(White), coordinates = Coordinates(col = 5, row = 5)),
                // Black
                Square(piece = Rook(Black), coordinates = Coordinates(col = 0, row = 2)),
            )
        )
        assertEquals(
            expected = setOf(
                Coordinates(col = 3, row = 6),
                Coordinates(col = 3, row = 4),
                Coordinates(col = 4, row = 3),
                Coordinates(col = 6, row = 3),
                Coordinates(col = 7, row = 4),
                Coordinates(col = 7, row = 6),
                Coordinates(col = 6, row = 7),
            ),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 5, row = 5))
        )
    }

    @Test
    fun testPawnCantMoveWhenEnemyKnightAttacksFriendlyKing() {
        // This test reproduces a bug in
        val board = Board()
            // Kingside knight teleports to attack the enemy king!
            .move(
                from = Coordinates(col = 6, row = 7),
                to = Coordinates(col = 3, row = 2)
            )

        assertEquals(
            // The pawn can't move because it's king is in check
            expected = emptySet(),
            actual = findValidMoves(
                board = board,
                coordinates = Coordinates(col = 6, row = 1)
            )
        )
    }
}