package com.franklinharper.chess

import com.franklinharper.chess.Piece.Bishop
import com.franklinharper.chess.Piece.King
import com.franklinharper.chess.Piece.Rook
import com.franklinharper.chess.PieceColor.*
import kotlin.test.Test
import kotlin.test.assertEquals

class RookTest {

    @Test
    fun testAllMovesArePossible() {
//        val board = Board(
//            listOf(
//                Square(Piece.Rook(PieceColor.White), col = 3, row = 3),
//            )
//        )
//        assertEquals(
//            expected = setOf(
//                // Left
//                Coordinates(col = 2, row = 3), Coordinates(col = 1, row = 3), Coordinates(col = 0, row = 3),
//                // Right
//                Coordinates(col = 4, row = 3), Coordinates(col = 5, row = 3), Coordinates(col = 6, row = 3), Coordinates(col = 7, row = 3),
//                // Up
//                Coordinates(col = 3, row = 2), Coordinates(col = 3, row = 1), Coordinates(col = 3, row = 0),
//                // Down
//                Coordinates(col = 3, row = 4), Coordinates(col = 3, row = 5), Coordinates(col = 3, row = 6), Coordinates(col = 3, row = 7),
//            ),
//            actual = board.findPossibleAttacks(Coordinates(col = 3, row = 3))
//        )
    }

    @Test
    fun testFriendlyPiecesBlockMoves() {
//        val board = Board(
//            listOf(
//                Square(Piece.Rook(PieceColor.White), col = 3, row = 3),
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
//                Square(Piece.Rook(PieceColor.White), col = 3, row = 3),
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
//                Square(Piece.Rook(PieceColor.White), col = 7, row = 7),
//
//                Square(Piece.Knight(PieceColor.White), col = 7, row = 6),
//                Square(Piece.Knight(PieceColor.White), col = 6, row = 7),
//                Square(Piece.Knight(PieceColor.White), col = 6, row = 6),
//            )
//        )
//        assertEquals(
//            expected =  emptySet(),
//            actual = board.findPossibleAttacks(Coordinates(col = 7, row = 7))
//        )
    }

    @Test
    fun testMoveCantPutKingInCheck() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                // White
                Square(piece = King(White), coordinates = Coordinates(col = 0, row = 0)),
                // This piece is pinned to the King
                Square(piece = Rook(White), coordinates = Coordinates(col = 1, row = 1)),

                // Black
                Square(piece = Bishop(Black), coordinates = Coordinates(col = 2, row = 2)),
            )
        )
        assertEquals(
            expected = emptySet(),
            actual = findValidMoves(
                board = board,
                coordinates = Coordinates(col = 1, row = 1),
            )
        )
    }

    @Test
    fun testMoveDefendsKingFromCheck() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                // White
                Square(piece = King(color = White), coordinates = Coordinates(col = 0, row = 0)),
                // This Rook can defend the King
                Square(piece = Rook(color = White), coordinates = Coordinates(col = 1, row = 1)),

                // Black
                Square(piece = Rook(color = Black), coordinates = Coordinates(col = 0, row = 2)),
            )
        )
        assertEquals(
            expected = setOf(Coordinates(col = 0, row = 1)),
            actual = findValidMoves(
                board = board,
                coordinates = Coordinates(col = 1, row = 1),
            )
        )
    }

    @Test
    fun testCaptureRemovesCheckOnKing() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                // White
                Square(piece = King(color = White), coordinates = Coordinates(col = 0, row = 0)),
                // This Rook can capture the Rook that is attacking the King
                Square(piece = Rook(color = White), coordinates = Coordinates(col = 1, row = 2)),

                // Black
                Square(piece = Rook(color = Black), coordinates = Coordinates(col = 0, row = 2)),
            )
        )
        assertEquals(
            expected = setOf(Coordinates(col = 0, row = 2)),
            actual = findValidMoves(
                board = board,
                coordinates = Coordinates(col = 1, row = 2),
            )
        )
    }
}