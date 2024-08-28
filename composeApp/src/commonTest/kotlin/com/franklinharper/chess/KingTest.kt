package com.franklinharper.chess

import com.franklinharper.chess.Piece.*
import com.franklinharper.chess.Piece.Companion.blackKingInitialCoordinates
import com.franklinharper.chess.Piece.Companion.blackKingsideCastle
import com.franklinharper.chess.Piece.Companion.blackKingsideRookInitialCoordinates
import com.franklinharper.chess.Piece.Companion.blackQueensideCastle
import com.franklinharper.chess.Piece.Companion.blackQueensideRookInitialCoordinates
import com.franklinharper.chess.Piece.Companion.whiteKingInitialCoordinates
import com.franklinharper.chess.Piece.Companion.whiteKingsideCastle
import com.franklinharper.chess.Piece.Companion.whiteKingsideRookInitialCoordinates
import com.franklinharper.chess.Piece.Companion.whiteQueensideCastle
import com.franklinharper.chess.Piece.Companion.whiteQueensideRookInitialCoordinates
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import com.franklinharper.chess.PieceColor.White
import com.franklinharper.chess.PieceColor.Black
import kotlin.test.assertTrue


class KingTest {

    @Test
    fun testAllNormalMovesArePossible() {
        val board = Board(
            setOf(
                Square(
                    piece = King(Black, hasMoved = true),
                    coordinates = Coordinates(col = 1, row = 2)
                ),
            )
        )
        assertEquals(
            expected = setOf(
                Coordinates(col = 0, row = 1), Coordinates(col = 1, row = 1), Coordinates(col = 2, row = 1),
                Coordinates(col = 0, row = 2),                                Coordinates(col = 2, row = 2),
                Coordinates(col = 0, row = 3), Coordinates(col = 1, row = 3), Coordinates(col = 2, row = 3),
            ),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 1, row = 2))
        )
    }

    @Test
    fun testFriendlyPiecesBlockMoves() {
        val board = Board(
            setOf(
                Square(piece = King(Black, hasMoved = true), coordinates =  Coordinates(col = 1, row = 1)),
                // Friendly pawn
                Square(piece = Pawn(Black, twoSquareAdvanceOnPreviousMove = true), coordinates = Coordinates(col = 1, row = 2)),
            )
        )
        // The black king can attack all around, except for the friendly pawn.
        assertEquals(
            expected = setOf(
                Coordinates(col = 0, row = 0), Coordinates(col = 1, row = 0), Coordinates(col = 2, row = 0),
                Coordinates(col = 0, row = 1), Coordinates(col = 2, row = 1),
                // On this row Coordinates(1, 2) is not present because ot the black pawn.
                Coordinates(col = 0, row = 2), Coordinates(col = 2, row = 2),
            ),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 1, row = 1))
        )
    }

    @Test
    fun testEdgesBlockMoves() {
        val board = Board(
            setOf(
                Square(piece = King(White, hasMoved = true), coordinates = Coordinates(col = 0, row = 0)),
            )
        )
        assertEquals(
            expected = setOf(
                Coordinates(col = 0, row = 1),
                Coordinates(col = 1, row = 0),
                Coordinates(col = 1, row = 1)
            ),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 0, row = 0))
        )
    }

    @Test
    fun testSquaresThatCanBeAttackedArentValidMoves() {
        val board = Board(
            setOf(
                Square(piece = King(White, hasMoved = true), coordinates = Coordinates(col = 0, row = 0)),
            )
        )
        assertEquals(
            expected = setOf(
                Coordinates(col = 0, row = 1),
                Coordinates(col = 1, row = 0),
                Coordinates(col = 1, row = 1)
            ),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 0, row = 0))
        )
    }

    @Test
    fun testCastlingIsPossible() {
        val board = Board(
            setOf(
                // White
                Square(piece = Rook(White, hasMoved = false), coordinates = whiteQueensideRookInitialCoordinates),
                Square(piece = King(White, hasMoved = false), coordinates = whiteKingInitialCoordinates),
                Square(piece = Rook(White, hasMoved = false), coordinates = whiteKingsideRookInitialCoordinates),
                // Black
                Square(piece = Rook(Black, hasMoved = false), coordinates = blackQueensideRookInitialCoordinates),
                Square(piece = King(Black, hasMoved = false), coordinates = blackKingInitialCoordinates),
                Square(piece = Rook(Black, hasMoved = false), coordinates = blackKingsideRookInitialCoordinates),
            )
        )
        // The white king can castle on both sides
        val whiteActual = findValidMoves(board = board, coordinates = whiteKingInitialCoordinates)
        assertContains(whiteActual, Piece.whiteQueensideCastle)
        assertContains(whiteActual, whiteKingsideCastle)

        // Validate the board state after white kingside castling
        val boardAfterWhiteKingsideCastling = board.move(
            from = whiteKingInitialCoordinates,
            to = whiteKingsideCastle
        )
        assertEquals(
            expected = King(White, hasMoved = true),
            actual =  boardAfterWhiteKingsideCastling.getPieceOrNull(whiteKingsideCastle)
        )
        assertEquals(
            expected = Rook(White, hasMoved = true),
            actual =  boardAfterWhiteKingsideCastling.getPieceOrNull(Coordinates(col = 5, row = 7))
        )
        // Validate the board state after white queenside castling
        val boardAfterWhiteQueensideCastling = board.move(
            from = whiteKingInitialCoordinates,
            to = whiteQueensideCastle,
        )
        assertEquals(
            expected = King(White, hasMoved = true),
            actual =  boardAfterWhiteQueensideCastling.getPieceOrNull(whiteQueensideCastle)
        )
        assertEquals(
            expected = Rook(White, hasMoved = true),
            actual =  boardAfterWhiteQueensideCastling.getPieceOrNull(Coordinates(col = 3, row = 7))
        )

        // The black king can castle on both sides
        val blackActual = findValidMoves(board = board, coordinates = blackKingInitialCoordinates)
        assertContains(blackActual, blackQueensideCastle)
        assertContains(blackActual, blackKingsideCastle)
        // Validate the board state after white kingside castling
        val boardAfterBlackKingsideCastling = board.move(
            from = blackKingInitialCoordinates,
            to = blackKingsideCastle
        )
        assertEquals(
            expected = King(Black, hasMoved = true),
            actual =  boardAfterBlackKingsideCastling.getPieceOrNull(blackKingsideCastle)
        )
        assertEquals(
            expected = Rook(Black, hasMoved = true),
            actual =  boardAfterBlackKingsideCastling.getPieceOrNull(Coordinates(col = 5, row = 0))
        )
        // Validate the board state after white queenside castling
        val boardAfterBlackQueensideCastling = board.move(
            from = blackKingInitialCoordinates,
            to = blackQueensideCastle,
        )
        assertEquals(
            expected = King(Black, hasMoved = true),
            actual =  boardAfterBlackQueensideCastling.getPieceOrNull(blackQueensideCastle)
        )
        assertEquals(
            expected = Rook(Black, hasMoved = true),
            actual =  boardAfterBlackQueensideCastling.getPieceOrNull(Coordinates(col = 3, row = 0))
        )
    }

    @Test
    fun testCastlingIsNotPossibleWhenKingHasMoved() {
        val board = Board(
            setOf(
                // White
                Square(
                    piece = King(hasMoved = true, color = White),
                    coordinates = whiteKingInitialCoordinates,
                ),
                Square(piece = Rook(White), coordinates = whiteQueensideRookInitialCoordinates),
                Square(piece = Rook(White), coordinates = whiteKingsideRookInitialCoordinates),

                // Black
                Square(
                    piece = King(Black, hasMoved = true),
                    coordinates = blackKingInitialCoordinates,
                ),
                Square(piece = Rook(Black), coordinates = blackQueensideRookInitialCoordinates),
                Square(piece = Rook(Black), coordinates = blackKingsideRookInitialCoordinates),
            )
        )
        val whiteActual = findValidMoves(board = board, coordinates = whiteKingInitialCoordinates)
        assertFalse(whiteActual.contains(Piece.whiteQueensideCastle))
        assertFalse(whiteActual.contains(whiteKingsideCastle))

        val blackActual = findValidMoves(board = board, coordinates = blackKingInitialCoordinates)
        assertFalse(blackActual.contains(Piece.whiteQueensideCastle))
        assertFalse(blackActual.contains(whiteKingsideCastle))
    }

    @Test
    fun testCastlingIsNotPossibleWhenRookHasMoved() {
        val board = Board(
            setOf(
                // White
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(piece = Rook(White), coordinates = whiteQueensideRookInitialCoordinates),
                Square(piece = Rook(White, hasMoved = true), coordinates = whiteKingsideRookInitialCoordinates),

                // Black
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                Square(piece = Rook(Black), coordinates = blackQueensideRookInitialCoordinates),
                Square(piece = Rook(Black, hasMoved = true), coordinates = blackKingsideRookInitialCoordinates),
            )
        )

        val whiteActual = findValidMoves(board = board, coordinates = whiteKingInitialCoordinates)
        // Queenside castling IS possible
        assertContains(whiteActual, Piece.whiteQueensideCastle)
        // Kingside castling is NOT possible
        assertFalse(whiteActual.contains(whiteKingsideCastle))

        val blackActual = findValidMoves(board = board, coordinates = blackKingInitialCoordinates)
        // Queenside castling IS possible
        assertContains(blackActual, Piece.blackQueensideCastle)
        // Kingside castling is NOT possible
        assertFalse(blackActual.contains(Piece.blackKingsideCastle))
    }

    @Test
    fun testCastlingIsNotPossibleWhenKingIsInCheck() {
        val board = Board(
            setOf(
                // White King and rooks that haven't moved
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(piece = Rook(White), coordinates = whiteQueensideRookInitialCoordinates),
                Square(piece = Rook(White), coordinates = whiteKingsideRookInitialCoordinates),

                // Black rook attacking the white king
                Square(piece = Rook(Black), coordinates = Coordinates(col = 4, row = 6)),

                // Black King and Rooks that haven't moved
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                Square(piece = Rook(Black), coordinates = blackQueensideRookInitialCoordinates),
                Square(piece = Rook(Black), coordinates = blackKingsideRookInitialCoordinates),

                // White rook attacking the black King
                Square(piece = Rook(White), coordinates = Coordinates(col = 4, row = 1)),
            )
        )

        // Castling is not possible because the Kings are in check
        val whiteActual = findValidMoves(board = board, coordinates = whiteKingInitialCoordinates)
        assertFalse(whiteActual.contains(Piece.whiteQueensideCastle))
        assertFalse(whiteActual.contains(whiteKingsideCastle))

        val blackActual = findValidMoves(board = board, coordinates = blackKingInitialCoordinates)
        assertFalse(blackActual.contains(Piece.blackQueensideCastle))
        assertFalse(blackActual.contains(Piece.blackKingsideCastle))
    }

    @Test
    fun testWhiteCastlingIsNotPossibleWhenAnIntermediateSquareIsOccupied() {
        val board = Board(
            setOf(
                Square(piece = Rook(White), coordinates = whiteQueensideRookInitialCoordinates),
                Square(piece = Knight(White), coordinates = Coordinates(col = 1, row = 7)),
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(piece = Knight(White), coordinates = Coordinates(col = 6, row = 7)),
                Square(piece = Rook(White), coordinates = whiteKingsideRookInitialCoordinates),
            )
        )
        val actual = findValidMoves(board = board, coordinates = whiteKingInitialCoordinates)
        assertFalse(actual.contains(Piece.whiteQueensideCastle))
        assertFalse(actual.contains(whiteKingsideCastle))
    }

    @Test
    fun testBlackCastlingIsNotPossibleWhenAnIntermediateSquareIsOccupied() {
        val board = Board(
            setOf(
                // Black
                Square(piece = Rook(Black), coordinates = blackQueensideRookInitialCoordinates),
                Square(piece = Knight(Black), coordinates = Coordinates(col = 1, row = 0)),
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                Square(piece = Knight(Black), coordinates = Coordinates(col = 6, row = 0)),
                Square(piece = Rook(Black), coordinates = blackKingsideRookInitialCoordinates),
            )
        )
        val actual = findValidMoves(board = board, coordinates = blackKingInitialCoordinates)
        assertFalse(actual.contains(Piece.blackQueensideCastle))
        assertFalse(actual.contains(Piece.blackKingsideCastle))
    }

    @Test
    fun testWhiteCastlingIsNotPossibleWhenAnIntermediateSquareIsOccupied2() {
        val board = Board(
            setOf(
                Square(piece = Rook(White), coordinates = whiteQueensideRookInitialCoordinates),
                Square(piece = Bishop(White), coordinates = Coordinates(col = 2, row = 7)),
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(piece = Bishop(White), coordinates = Coordinates(col = 5, row = 7)),
                Square(piece = Rook(White), coordinates = whiteKingsideRookInitialCoordinates),
            )
        )
        val actual = findValidMoves(board = board, coordinates = whiteKingInitialCoordinates)
        // Queenside castling is not possible
        assertFalse(actual.contains(Piece.whiteQueensideCastle))
        // Kingside castling is not possible
        assertFalse(actual.contains(whiteKingsideCastle))
    }

    @Test
    fun testBlackCastlingIsNotPossibleWhenAnIntermediateSquareIsOccupied2() {
        val board = Board(
            setOf(
                Square(piece = Rook(Black), coordinates = blackQueensideRookInitialCoordinates),
                Square(piece = Bishop(Black), coordinates = Coordinates(col = 2, row = 0)),
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                Square(piece = Bishop(Black), coordinates = Coordinates(col = 5, row = 0)),
                Square(piece = Rook(Black), coordinates = blackKingsideRookInitialCoordinates),
            )
        )
        val actual = findValidMoves(board = board, coordinates = blackKingInitialCoordinates)
        // Queenside castling is not possible
        assertFalse(actual.contains(Piece.blackQueensideCastle))
        // Kingside castling is not possible
        assertFalse(actual.contains(Piece.blackKingsideCastle))
    }

    @Test
    fun testWhiteQueensideCastlingIsNotPossibleWhenQueenIsInItsInitialPosition() {
        val board = Board(
            setOf(
                Square(piece = Rook(White), coordinates = whiteQueensideRookInitialCoordinates),
                Square(piece = Queen(White), coordinates = Piece.whiteQueenInitialCoordinates),
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(piece = Rook(White), coordinates = whiteKingsideRookInitialCoordinates),
            )
        )
        val actual = findValidMoves(board = board, coordinates = whiteKingInitialCoordinates)
        // Queenside castling is not possible
        assertFalse(actual.contains(Piece.whiteQueensideCastle))
        // Kingside castling is possible
        assertContains(actual, whiteKingsideCastle)
    }

    @Test
    fun testBlackQueensideCastlingIsNotPossibleWhenQueenIsInItsInitialPosition() {
        val board = Board(
            setOf(
                Square(piece = Rook(Black), coordinates = blackQueensideRookInitialCoordinates),
                Square(piece = Queen(Black), coordinates = Piece.blackQueenInitialCoordinates),
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                Square(piece = Rook(Black), coordinates = blackKingsideRookInitialCoordinates),
            )
        )
        val actual = findValidMoves(board = board, coordinates = blackKingInitialCoordinates)
        // Queenside castling is not possible
        assertFalse(actual.contains(Piece.blackQueensideCastle))
        // Kingside castling is possible
        assertContains(actual, Piece.blackKingsideCastle)
    }

    @Test
    fun testCastlingIsNotPossibleWhenEnemypieceIsOnAnIntermediateSquare() {
        val board = Board(
            setOf(
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(piece = Rook(Black), coordinates = Coordinates(col = 6, row = 7)),
                Square(piece = Rook(White), coordinates = whiteKingsideRookInitialCoordinates),
            )
        )
        val actual = findValidMoves(board = board, coordinates = whiteKingInitialCoordinates)
        // Queenside castling is NOT possible
        assertFalse(actual.contains(Piece.whiteQueensideCastle))
        // Kingside castling is NOT possible
        assertFalse(actual.contains(whiteKingsideCastle))
    }

    @Test
    fun testCastlingIsNotPossibleWhenAnIntermediateSquareIsUnderAttack() {
        val board = Board(
            setOf(
                Square(piece = Rook(White), coordinates = whiteKingsideRookInitialCoordinates),
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(piece = Rook(White), coordinates = whiteKingsideRookInitialCoordinates),

                Square(piece = Rook(Black), coordinates = Coordinates(col = 4, row = 6))
            )
        )
        val actual = findValidMoves(board = board, coordinates = whiteKingInitialCoordinates)
        // Queenside castling is NOT possible
        assertFalse(actual.contains(Piece.whiteQueensideCastle))
        // Kingside castling is NOT possible
        assertFalse(actual.contains(whiteKingsideCastle))
    }

    @Test
    fun testCheckmate() {
        val board = Board(
            // Black
            setOf(
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                // White
                Square(piece = Queen(White), coordinates = Coordinates(col = 4, row = 1)),
                Square(piece = King(White), coordinates = Coordinates(col = 4, row = 2)),),
            )
        assertFalse(board.isCheckmate(White))
        assertTrue(board.isCheckmate(Black))
    }

    @Test
    fun testStalemate() {
        val board = Board(
            // Black
            setOf(
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                // White
                Square(piece = Bishop(White), coordinates = Coordinates(col = 4, row = 1)),
                Square(piece = King(White), coordinates = Coordinates(col = 4, row = 2)),),
        )
        assertFalse(board.isStalemate(White))
        assertTrue(board.isStalemate(Black))
    }
}