package com.franklinharper.chess

import com.franklinharper.chess.Piece.Bishop
import com.franklinharper.chess.Piece.King
import com.franklinharper.chess.Piece.Rook
import kotlin.test.Test
import kotlin.test.assertEquals

class BishopTest {

    @Test
    fun testAllMovesArePossible() {
//        val board = Board(
//            listOf(
//                Square(Piece.Bishop(PieceColor.White), col = 3, row = 3),
//            )
//        )
//        assertEquals(
//            expected = setOf(
//                // Upper Left diagonal
//                Coordinates(col = 2, row = 2), Coordinates(col = 1, row = 1), Coordinates(col = 0, row = 0),
//                // Upper Right diagonal
//                Coordinates(col = 4, row = 2), Coordinates(col = 5, row = 1), Coordinates(col = 6, row = 0),
//                // Lower Left diagonal
//                Coordinates(col = 2, row = 4), Coordinates(col = 1, row = 5), Coordinates(col = 0, row = 6),
//                // Lower Right diagonal
//                Coordinates(col = 4, row = 4), Coordinates(col = 5, row = 5), Coordinates(col = 6, row = 6), Coordinates(col = 7, row = 7),
//            ),
//            actual = board.findPossibleAttacks(Coordinates(col = 3, row = 3))
//        )
    }

    @Test
    fun testFriendlyPiecesBlockMoves() {
//        val board = Board(
//            listOf(
//                Square(Piece.Bishop(PieceColor.White), col = 3, row = 3),
//
//                // Upper Left diagonal
//                Square(Piece.Pawn(PieceColor.White), col = 1, row = 1),
//                // Upper Right diagonal
//                Square(Piece.Pawn(PieceColor.White), col = 5, row = 1),
//                // Lower Left diagonal
//                Square(Piece.Pawn(PieceColor.White), col = 1, row = 5),
//                // Lower Right diagonal
//                Square(Piece.Pawn(PieceColor.White), col = 5, row = 5),
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
//            ),
//            actual = board.findPossibleAttacks(Coordinates(col = 3, row = 3))
//        )
    }

    @Test
    fun testMovesAreBlockedAfterEnemyPieces() {
//        val board = Board(
//            listOf(
//                Square(Piece.Bishop(PieceColor.White), col = 3, row = 3),
//
//                // Upper Left diagonal
//                Square(Piece.Pawn(PieceColor.Black), col = 1, row = 1),
//                // Upper Right diagonal
//                Square(Piece.Pawn(PieceColor.Black), col = 5, row = 1),
//                // Lower Left diagonal
//                Square(Piece.Pawn(PieceColor.Black), col = 1, row = 5),
//                // Lower Right diagonal
//                Square(Piece.Pawn(PieceColor.Black), col = 5, row = 5),
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
//            ),
//            actual = board.findPossibleAttacks(Coordinates(col = 3, row = 3))
//        )
    }

    @Test
    fun testCornerCaseAndNoPossibleMoves() {
//        val board = Board(
//            listOf(
//                Square(Piece.Bishop(PieceColor.White), col = 7, row = 7),
//
//                // Upper Left diagonal
//                Square(Piece.Pawn(PieceColor.White), col = 6, row = 6),
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
            setOf(
                // White
                Square(piece = King(PieceColor.White), coordinates = Coordinates(col = 0, row = 0)),
                Square(piece = Bishop(PieceColor.White), coordinates = Coordinates(col = 0, row = 1)),
                // Black
                Square(piece = Rook(PieceColor.Black), coordinates = Coordinates(col = 0, row = 2)),
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
                Square(piece = King(PieceColor.White), coordinates = Coordinates(col = 0, row = 0)),
                Square(piece = Bishop(PieceColor.White), coordinates = Coordinates(col = 1, row = 0)),
                // Black
                Square(piece = Rook(PieceColor.Black), coordinates = Coordinates(col = 0, row = 2)),
            )
        )
        assertEquals(
            expected = setOf(Coordinates(col = 0, row = 1)),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 1, row = 0))
        )
    }

    @Test
    fun testCaptureRemovesCheckOnKing() {
        val board = Board(
            setOf(
                // White
                Square(piece = King(PieceColor.White), coordinates = Coordinates(col = 0, row = 0)),
                Square(piece = Bishop(PieceColor.White), coordinates = Coordinates(col = 2, row = 0)),
                // Black
                Square(piece = Rook(PieceColor.Black), coordinates = Coordinates(col = 0, row = 2)),
            )
        )
        assertEquals(
            expected = setOf(Coordinates(col = 0, row = 2)),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 2, row = 0))
        )
    }
}