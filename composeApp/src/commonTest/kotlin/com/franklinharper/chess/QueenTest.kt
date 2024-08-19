package com.franklinharper.chess

import com.franklinharper.chess.Piece.*
import com.franklinharper.chess.PieceColor.*
import kotlin.test.Test
import kotlin.test.assertEquals

class QueenTest {

    @Test
    fun testAllMovesArePossible() {
//        val board = Board(
//            listOf(
//                Square(Piece.Queen(PieceColor.White), col = 3, row = 3),
//            )
//        )
//        assertEquals(
//            expected = setOf(
//
//                // Upper Left diagonal
//                Coordinates(col = 2, row = 2), Coordinates(col = 1, row = 1), Coordinates(col = 0, row = 0),
//                // Upper Right diagonal
//                Coordinates(col = 4, row = 2), Coordinates(col = 5, row = 1), Coordinates(col = 6, row = 0),
//                // Lower Left diagonal
//                Coordinates(col = 2, row = 4), Coordinates(col = 1, row = 5), Coordinates(col = 0, row = 6),
//                // Lower Right diagonal
//                Coordinates(col = 4, row = 4), Coordinates(col = 5, row = 5), Coordinates(col = 6, row = 6), Coordinates(col = 7, row = 7),
//
//                // Row Left
//                Coordinates(col = 2, row = 3), Coordinates(col = 1, row = 3), Coordinates(col = 0, row = 3),
//                // Row Right
//                Coordinates(col = 4, row = 3), Coordinates(col = 5, row = 3), Coordinates(col = 6, row = 3), Coordinates(col = 7, row = 3),
//                // Row Up
//                Coordinates(col = 3, row = 2), Coordinates(col = 3, row = 1), Coordinates(col = 3, row = 0),
//                // Row Down
//                Coordinates(col = 3, row = 4), Coordinates(col = 3, row = 5), Coordinates(col = 3, row = 6), Coordinates(col = 3, row = 7),
//
//            ),
//            actual = board.findPossibleAttacks(Coordinates(col = 3, row = 3))
//        )
    }

    @Test
    fun testFriendlyPiecesBlockMoves() {
//        val board = Board(
//            listOf(
//                Square(Piece.Queen(PieceColor.White), col = 3, row = 3),
//
//                // Upper Left diagonal
//                Square(Piece.Pawn(PieceColor.White), col = 1, row = 1),
//                // Upper Right diagonal
//                Square(Piece.Pawn(PieceColor.White), col = 5, row = 1),
//                // Lower Left diagonal
//                Square(Piece.Pawn(PieceColor.White), col = 1, row = 5),
//                // Lower Right diagonal
//                Square(Piece.Pawn(PieceColor.White), col = 5, row = 5),
//
//                // Row Left
//                Square(Piece.Pawn(PieceColor.White), col = 3, row = 1),
//                // Row Right
//                Square(Piece.Pawn(PieceColor.White), col = 3, row = 5),
//                // Column Up
//                Square(Piece.King(PieceColor.White), col = 1, row = 3),
//                // Column Down
//                Square(Piece.King(PieceColor.White), col = 5, row = 3),
//            )
//        )
//        assertEquals(
//            expected = setOf(
//                // Upper Left diagonal
//                Coordinates(col = 2, row = 2),
//                // Upper Right diagonal
//                Coordinates(col = 4, row = 2),
//                // Lower Left diagonal
//                Coordinates(col = 2, row = 4),
//                // Lower Right diagonal
//                Coordinates(col = 4, row = 4),
//
//                // Row Left
//                Coordinates(col = 2, row = 3),
//                // Row Right
//                Coordinates(col = 4, row = 3),
//                // Column Up
//                Coordinates(col = 3, row = 2),
//                // Column Down
//                Coordinates(col = 3, row = 4),
//            ),
//            actual = board.findPossibleAttacks(Coordinates(col = 3, row = 3))
//        )
    }

    @Test
    fun testMovesAreBlockedAfterEnemyPieces() {
//        val board = Board(
//            listOf(
//                Square(Piece.Queen(PieceColor.White), col = 3, row = 3),
//
//                // Upper Left diagonal
//                Square(Piece.Pawn(PieceColor.Black), col = 1, row = 1),
//                // Upper Right diagonal
//                Square(Piece.Pawn(PieceColor.Black), col = 5, row = 1),
//                // Lower Left diagonal
//                Square(Piece.Pawn(PieceColor.Black), col = 1, row = 5),
//                // Lower Right diagonal
//                Square(Piece.Pawn(PieceColor.Black), col = 5, row = 5),
//
//                // Row Left
//                Square(Piece.King(PieceColor.Black), col = 1, row = 3),
//                // Row Right
//                Square(Piece.King(PieceColor.Black), col = 5, row = 3),
//                // Column Up
//                Square(Piece.Pawn(PieceColor.Black), col = 3, row = 1),
//                // Column Down
//                Square(Piece.Pawn(PieceColor.Black), col = 3, row = 5),
//            )
//        )
//        assertEquals(
//            expected = setOf(
//                // Upper Left diagonal
//                Coordinates(col = 2, row = 2), Coordinates(col = 1, row = 1),
//                // Upper Right diagonal
//                Coordinates(col = 4, row = 2), Coordinates(col = 5, row = 1),
//                // Lower Left diagonal
//                Coordinates(col = 2, row = 4), Coordinates(col = 1, row = 5),
//                // Lower Right diagonal
//                Coordinates(col = 4, row = 4), Coordinates(col = 5, row = 5),
//
//                // Row Left
//                Coordinates(col = 2, row = 3), Coordinates(col = 1, row = 3),
//                // Row Right
//                Coordinates(col = 4, row = 3), Coordinates(col = 5, row = 3),
//                // Column Up
//                Coordinates(col = 3, row = 2), Coordinates(col = 3, row = 1),
//                // Column Down
//                Coordinates(col = 3, row = 4), Coordinates(col = 3, row = 5),
//            ),
//            actual = board.findPossibleAttacks(Coordinates(col = 3, row = 3))
//        )
    }

    @Test
    fun testCornerCaseAndNoPossibleMoves() {
//        val board = Board(
//            listOf(
//                Square(Piece.Queen(PieceColor.White), col = 7, row = 7),
//
//                // Upper Left diagonal
//                Square(Piece.Pawn(PieceColor.White), col = 6, row = 6),
//                // Column Up
//                Square(Piece.Pawn(PieceColor.White), col = 7, row = 6),
//                // Row Left
//                Square(Piece.Pawn(PieceColor.White), col = 6, row = 7),
//            )
//        )
//        assertEquals(
//            expected =  emptySet(),
//            actual = board.findPossibleAttacks(Coordinates(col = 7, row = 7))
//        )
    }

    @Test
    fun testQueenIsPinned() {
        // When the Queen is pinned to the King, it can always capture the
        // attacking piece
        val queenCoordinates = Coordinates(col = 0, row = 1)
        val board = Board(
            setOf(
                // White
                Square(piece = King(White), coordinates = Coordinates(col = 0, row = 0)),
                // The Queen is pinned to the King
                Square(piece = Queen(White), coordinates = queenCoordinates),

                // Black
                Square(piece = Rook(Black), coordinates = Coordinates(col = 0, row = 2)),
            )
        )
        // The only possible move for the Queen is to capture the attacking Rook
        assertEquals(
            expected = setOf(Coordinates(col = 0, row = 2)),
            actual = findValidMoves(board = board, coordinates = queenCoordinates)
        )
    }

    @Test
    fun testCaptureRemovesCheckOnKing() {
        val board = Board(
            setOf(
                // White
                Square(piece = King(White), coordinates = Coordinates(col = 0, row = 0)),
                Square(piece = Queen(White), coordinates = Coordinates(col = 1, row = 1)),

                // Black
                Square(piece = Rook(Black), coordinates = Coordinates(col = 0, row = 2)),
            )
        )
        assertEquals(
            expected = setOf(
                // Queen can capture the rook
                Coordinates(col = 0, row = 2),
                // Queen can block the rook attack
                Coordinates(col = 0, row = 1)
            ),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 1, row = 1))
        )
    }
}