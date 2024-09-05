package com.franklinharper.chess

import com.franklinharper.chess.Piece.*
import com.franklinharper.chess.Piece.Companion.blackKingInitialCoordinates
import com.franklinharper.chess.Piece.Companion.whiteKingInitialCoordinates
import kotlin.test.Test
import kotlin.test.assertTrue

import com.franklinharper.chess.PieceColor.White
import com.franklinharper.chess.PieceColor.Black
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class BoardTest {

    //    @Test
    //    fun testEmptyBoard() {
    //        val board = Board()
    //        val attacks = board.findAttacks(Black)
    //        assertTrue(attacks.isEmpty())
    //    }

    @Test
    fun testBishopDiagonalAttacks() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                Square(piece = Bishop(White), coordinates = Coordinates(col = 1, row = 1)),
            )
        )
        //  Square is attacked from upper left diagonal
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 2, row = 2),
                attackingColor = White,
            )
        )
        //  Square is attacked from upper left diagonal
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 7, row = 7),
                attackingColor = White,
            )
        )
        //  Square is attacked from lower left diagonal
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 2, row = 0),
                attackingColor = White,
            )
        )
        // Square is attacked from upper right diagonal
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 0, row = 2),
                attackingColor = White,
            )
        )
        // Square can't be attacked because the piece is not an enemy
        assertFalse(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 0, row = 2),
                attackingColor = Black,
            )
        )
        // Square can't be attacked because it isn't on a diagonal of the Bishop
        assertFalse(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 0, row = 3),
                attackingColor = White,
            )
        )
        assertFalse(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 0, row = 4),
                attackingColor = White,
            )
        )
    }

    @Test
    fun testQueenDiagonalAttacks() {
        val board = Board(
            moveColor = Black,
            squares = setOf(
                Square(piece = Queen(Black), coordinates = Coordinates(col = 6, row = 7)),
            )
        )

        // Square is attacked from lower left diagonal
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 7, row = 6),
                attackingColor = Black,
            )
        )
        // Square is attacked from lower right diagonal
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 0, row = 1),
                attackingColor = Black,
            )
        )
        // Square can't be attacked because it isn't on a diagonal of the Queen
        assertFalse(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 1, row = 0),
                attackingColor = Black,
            )
        )
    }

    @Test
    fun testRookAttacks() {
        val board = Board(
            moveColor = Black,
            squares = setOf(
                Square(piece = Rook(Black), coordinates = Coordinates(col = 1, row = 1)),
            )
        )
        // Square is attacked from right row
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 0, row = 1),
                attackingColor = Black,
            )
        )
    }

    @Test
    fun testKnightAttacks() {
        val board = Board(
            moveColor = Black,
            squares = setOf(
                Square(piece = Knight(Black), coordinates = Coordinates(col = 1, row = 1)),
            )
        )
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 0, row = 3),
                attackingColor = Black,
            )
        )
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 2, row = 3),
                attackingColor = Black,
            )
        )
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 3, row = 2),
                attackingColor = Black,
            )
        )
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 3, row = 0),
                attackingColor = Black,
            )
        )
        // Square can't be attacked by a friendly piece
        assertFalse(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 3, row = 0),
                attackingColor = White,
            )
        )
        assertFalse(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 2, row = 0),
                attackingColor = Black,
            )
        )
    }

    @Test
    fun testBlackPawnAttacks() {
        val board = Board(
            moveColor = Black,
            squares = setOf(
                Square(
                    piece = Pawn(Black, twoSquareAdvanceOnPreviousMove = true),
                    coordinates = Coordinates(col = 1, row = 1)
                ),
            )
        )
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 0, row = 2),
                attackingColor = Black,
            )
        )
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 2, row = 2),
                attackingColor = Black,
            )
        )
        assertFalse(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 0, row = -1),
                attackingColor = Black,
            )
        )
    }

    @Test
    fun testWhitePawnAttacks() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                Square(
                    piece = Pawn(White, twoSquareAdvanceOnPreviousMove = true),
                    coordinates = Coordinates(col = 1, row = 1)
                ),
            )
        )
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 0, row = 0),
                attackingColor = White,
            )
        )
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 2, row = 0),
                attackingColor = White,
            )
        )
        assertFalse(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 1, row = 0),
                attackingColor = White,
            )
        )
    }

    @Test
    fun testKingAttacks() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                Square(piece = King(White), coordinates = Coordinates(col = 1, row = 1)),
            )
        )
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 0, row = 0),
                attackingColor = White,
            )
        )
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 1, row = 0),
                attackingColor = White,
            )
        )
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 2, row = 0),
                attackingColor = White,
            )
        )
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 0, row = 1),
                attackingColor = White,
            )
        )
        assertFalse(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 1, row = 1),
                attackingColor = White,
            )
        )
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 2, row = 1),
                attackingColor = White,
            )
        )
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 0, row = 2),
                attackingColor = White,
            )
        )
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 1, row = 2),
                attackingColor = White,
            )
        )
        assertTrue(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 2, row = 2),
                attackingColor = White,
            )
        )
        assertFalse(
            squareCanBeAttacked(
                board = board,
                coordinates = Coordinates(col = 2, row = 2),
                attackingColor = Black,
            )
        )
    }

    @Test
    fun testBasicMove() {
        val board =
            Board(
                moveColor = White,
                squares = setOf(
                    // White
                    Square(
                        piece = King(White, hasMoved = true),
                        coordinates = Coordinates(col = 1, row = 1)
                    ),

                    // Black
                    Square(
                        piece = King(Black, hasMoved = false),
                        coordinates = blackKingInitialCoordinates
                    ),
                )
            )
                .move(from = Coordinates(col = 1, row = 1), to = Coordinates(col = 2, row = 2))
        assertEquals(
            expected = King(White, hasMoved = true),
            actual = board.getPieceOrNull(Coordinates(col = 2, row = 2))
        )
        assertEquals(
            expected = null,
            actual = board.getPieceOrNull(Coordinates(col = 1, row = 1))
        )
    }

    @Test
    fun testCaptureMove() {
        val board =
            Board(
                moveColor = Black,
                squares = setOf(
                    // Black
                    Square(
                        piece = King(Black, hasMoved = false),
                        coordinates = blackKingInitialCoordinates,
                    ),
                    // White
                    Square(
                        piece = Queen(White, hasMoved = true),
                        coordinates = Coordinates(col = 4, row = 1)
                    ),
                    Square(
                        piece = King(White, hasMoved = false),
                        coordinates = whiteKingInitialCoordinates,
                    ),
                )
            )
                .move(from = blackKingInitialCoordinates, to = Coordinates(col = 4, row = 1))
        assertEquals(
            expected = King(Black, hasMoved = true),
            actual = board.getPieceOrNull(Coordinates(col = 4, row = 1))
        )
        assertEquals(
            expected = null,
            actual = board.getPieceOrNull(blackKingInitialCoordinates)
        )
    }
}
