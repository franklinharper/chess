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
            moveColor = White,
            squares =
            setOf(
                Square(
                    piece = King(Black, hasMoved = true),
                    coordinates = Coordinates(col = 1, row = 2)
                ),
            )
        )
        assertEquals(
            expected = setOf(
                Coordinates(col = 0, row = 1),
                Coordinates(col = 1, row = 1),
                Coordinates(col = 2, row = 1),
                Coordinates(col = 0, row = 2),
                Coordinates(col = 2, row = 2),
                Coordinates(col = 0, row = 3),
                Coordinates(col = 1, row = 3),
                Coordinates(col = 2, row = 3),
            ),
            actual = findValidMoves(
                board = board,
                coordinates = Coordinates(col = 1, row = 2),
            )
        )
    }

    @Test
    fun testFriendlyPiecesBlockMoves() {
        val board = Board(
            moveColor = Black,
            squares = setOf(
                Square(
                    piece = King(Black, hasMoved = true),
                    coordinates = Coordinates(col = 1, row = 1)
                ),
                // Friendly pawn
                Square(
                    piece = Pawn(Black, twoSquareAdvanceOnPreviousMove = true),
                    coordinates = Coordinates(col = 1, row = 2)
                ),
            )
        )
        // The black king can attack all around, except for the friendly pawn.
        assertEquals(
            expected = setOf(
                Coordinates(col = 0, row = 0),
                Coordinates(col = 1, row = 0),
                Coordinates(col = 2, row = 0),
                Coordinates(col = 0, row = 1),
                Coordinates(col = 2, row = 1),
                // On this row Coordinates(1, 2) is not present because ot the black pawn.
                Coordinates(col = 0, row = 2),
                Coordinates(col = 2, row = 2),
            ),
            actual = findValidMoves(
                board = board,
                coordinates = Coordinates(col = 1, row = 1),
            )
        )
    }

    @Test
    fun testEdgesBlockMoves() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                Square(
                    piece = King(White, hasMoved = true),
                    coordinates = Coordinates(col = 0, row = 0)
                ),
            )
        )
        assertEquals(
            expected = setOf(
                Coordinates(col = 0, row = 1),
                Coordinates(col = 1, row = 0),
                Coordinates(col = 1, row = 1)
            ),
            actual = findValidMoves(
                board = board,
                coordinates = Coordinates(col = 0, row = 0),
            )
        )
    }

    @Test
    fun testSquaresThatCanBeAttackedArentValidMoves() {
        val board = Board(
            moveColor = White,
            squares =
            setOf(
                Square(
                    piece = King(White, hasMoved = true),
                    coordinates = Coordinates(col = 0, row = 0)
                ),
            )
        )
        assertEquals(
            expected = setOf(
                Coordinates(col = 0, row = 1),
                Coordinates(col = 1, row = 0),
                Coordinates(col = 1, row = 1)
            ),
            actual = findValidMoves(
                board = board,
                coordinates = Coordinates(col = 0, row = 0),
            )
        )
    }

    @Test
    fun testCastlingIsPossible() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                // White
                Square(
                    piece = Rook(White, hasMoved = false),
                    coordinates = whiteQueensideRookInitialCoordinates
                ),
                Square(
                    piece = King(White, hasMoved = false),
                    coordinates = whiteKingInitialCoordinates
                ),
                Square(
                    piece = Rook(White, hasMoved = false),
                    coordinates = whiteKingsideRookInitialCoordinates
                ),
                // Black
                Square(
                    piece = Rook(Black, hasMoved = false),
                    coordinates = blackQueensideRookInitialCoordinates
                ),
                Square(
                    piece = King(Black, hasMoved = false),
                    coordinates = blackKingInitialCoordinates
                ),
                Square(
                    piece = Rook(Black, hasMoved = false),
                    coordinates = blackKingsideRookInitialCoordinates
                ),
            )
        )
        // The white king can castle on both sides
        val whiteActual = findValidMoves(
            board = board,
            coordinates = whiteKingInitialCoordinates,
        )
        assertContains(whiteActual, whiteQueensideCastle)
        assertContains(whiteActual, whiteKingsideCastle)

        // Validate the board state after white kingside castling
        val boardAfterWhiteKingsideCastling = board.move(
            from = whiteKingInitialCoordinates,
            to = whiteKingsideCastle
        )
        assertEquals(
            expected = King(White, hasMoved = true),
            actual = boardAfterWhiteKingsideCastling.getPieceOrNull(whiteKingsideCastle)
        )
        assertEquals(
            expected = Rook(White, hasMoved = true),
            actual = boardAfterWhiteKingsideCastling.getPieceOrNull(Coordinates(col = 5, row = 7))
        )
        // Validate the board state after white queenside castling
        val boardAfterWhiteQueensideCastling = board.move(
            from = whiteKingInitialCoordinates,
            to = whiteQueensideCastle,
        )
        assertEquals(
            expected = King(White, hasMoved = true),
            actual = boardAfterWhiteQueensideCastling.getPieceOrNull(whiteQueensideCastle)
        )
        assertEquals(
            expected = Rook(White, hasMoved = true),
            actual = boardAfterWhiteQueensideCastling.getPieceOrNull(Coordinates(col = 3, row = 7))
        )

        // The black king can castle on both sides
        val blackActual = findValidMoves(
            board = board,
            coordinates = blackKingInitialCoordinates,
        )
        assertContains(blackActual, blackQueensideCastle)
        assertContains(blackActual, blackKingsideCastle)
        // Validate the board state after white kingside castling
        val boardAfterBlackKingsideCastling = board.move(
            from = blackKingInitialCoordinates,
            to = blackKingsideCastle
        )
        assertEquals(
            expected = King(Black, hasMoved = true),
            actual = boardAfterBlackKingsideCastling.getPieceOrNull(blackKingsideCastle)
        )
        assertEquals(
            expected = Rook(Black, hasMoved = true),
            actual = boardAfterBlackKingsideCastling.getPieceOrNull(Coordinates(col = 5, row = 0))
        )
        // Validate the board state after white queenside castling
        val boardAfterBlackQueensideCastling = board.move(
            from = blackKingInitialCoordinates,
            to = blackQueensideCastle,
        )
        assertEquals(
            expected = King(Black, hasMoved = true),
            actual = boardAfterBlackQueensideCastling.getPieceOrNull(blackQueensideCastle)
        )
        assertEquals(
            expected = Rook(Black, hasMoved = true),
            actual = boardAfterBlackQueensideCastling.getPieceOrNull(Coordinates(col = 3, row = 0))
        )
    }

    @Test
    fun testCastlingIsNotPossibleWhenKingHasMoved() {
        val board = Board(
            moveColor = White,
            squares = setOf(
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
        val whiteActual = findValidMoves(
            board = board,
            coordinates = whiteKingInitialCoordinates,
        )
        assertFalse(whiteActual.contains(whiteQueensideCastle))
        assertFalse(whiteActual.contains(whiteKingsideCastle))

        val blackActual = findValidMoves(
            board = board,
            coordinates = blackKingInitialCoordinates,
        )
        assertFalse(blackActual.contains(whiteQueensideCastle))
        assertFalse(blackActual.contains(whiteKingsideCastle))
    }

    @Test
    fun testCastlingIsNotPossibleWhenRookHasMoved() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                // White
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(piece = Rook(White), coordinates = whiteQueensideRookInitialCoordinates),
                Square(
                    piece = Rook(White, hasMoved = true),
                    coordinates = whiteKingsideRookInitialCoordinates
                ),

                // Black
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                Square(piece = Rook(Black), coordinates = blackQueensideRookInitialCoordinates),
                Square(
                    piece = Rook(Black, hasMoved = true),
                    coordinates = blackKingsideRookInitialCoordinates
                ),
            )
        )

        val whiteActual = findValidMoves(
            board = board,
            coordinates = whiteKingInitialCoordinates,
        )
        // Queenside castling IS possible
        assertContains(whiteActual, whiteQueensideCastle)
        // Kingside castling is NOT possible
        assertFalse(whiteActual.contains(whiteKingsideCastle))

        val blackActual = findValidMoves(
            board = board,
            coordinates = blackKingInitialCoordinates,
        )
        // Queenside castling IS possible
        assertContains(blackActual, blackQueensideCastle)
        // Kingside castling is NOT possible
        assertFalse(blackActual.contains(blackKingsideCastle))
    }

    @Test
    fun testCastlingIsNotPossibleWhenKingIsInCheck() {
        val board = Board(
            moveColor = White,
            squares = setOf(
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
        val whiteActual = findValidMoves(
            board = board,
            coordinates = whiteKingInitialCoordinates,
        )
        assertFalse(whiteActual.contains(whiteQueensideCastle))
        assertFalse(whiteActual.contains(whiteKingsideCastle))

        val blackActual = findValidMoves(
            board = board,
            coordinates = blackKingInitialCoordinates,
        )
        assertFalse(blackActual.contains(blackQueensideCastle))
        assertFalse(blackActual.contains(blackKingsideCastle))
    }

    @Test
    fun testWhiteCastlingIsNotPossibleWhenAnIntermediateSquareIsOccupied() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                Square(piece = Rook(White), coordinates = whiteQueensideRookInitialCoordinates),
                Square(piece = Knight(White), coordinates = Coordinates(col = 1, row = 7)),
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(piece = Knight(White), coordinates = Coordinates(col = 6, row = 7)),
                Square(piece = Rook(White), coordinates = whiteKingsideRookInitialCoordinates),
            )
        )
        val actual = findValidMoves(
            board = board,
            coordinates = whiteKingInitialCoordinates,
        )
        assertFalse(actual.contains(whiteQueensideCastle))
        assertFalse(actual.contains(whiteKingsideCastle))
    }

    @Test
    fun testBlackCastlingIsNotPossibleWhenAnIntermediateSquareIsOccupied() {
        val board = Board(
            moveColor = Black,
            squares = setOf(
                // Black
                Square(piece = Rook(Black), coordinates = blackQueensideRookInitialCoordinates),
                Square(piece = Knight(Black), coordinates = Coordinates(col = 1, row = 0)),
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                Square(piece = Knight(Black), coordinates = Coordinates(col = 6, row = 0)),
                Square(piece = Rook(Black), coordinates = blackKingsideRookInitialCoordinates),
            )
        )
        val actual = findValidMoves(
            board = board,
            coordinates = blackKingInitialCoordinates,
        )
        assertFalse(actual.contains(blackQueensideCastle))
        assertFalse(actual.contains(blackKingsideCastle))
    }

    @Test
    fun testWhiteCastlingIsNotPossibleWhenAnIntermediateSquareIsOccupied2() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                Square(piece = Rook(White), coordinates = whiteQueensideRookInitialCoordinates),
                Square(piece = Bishop(White), coordinates = Coordinates(col = 2, row = 7)),
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(piece = Bishop(White), coordinates = Coordinates(col = 5, row = 7)),
                Square(piece = Rook(White), coordinates = whiteKingsideRookInitialCoordinates),
            )
        )
        val actual = findValidMoves(
            board = board,
            coordinates = whiteKingInitialCoordinates,
        )
        // Queenside castling is not possible
        assertFalse(actual.contains(whiteQueensideCastle))
        // Kingside castling is not possible
        assertFalse(actual.contains(whiteKingsideCastle))
    }

    @Test
    fun testBlackCastlingIsNotPossibleWhenAnIntermediateSquareIsOccupied2() {
        val board = Board(
            moveColor = Black,
            squares = setOf(
                // Black
                Square(piece = Rook(Black), coordinates = blackQueensideRookInitialCoordinates),
                Square(piece = Bishop(Black), coordinates = Coordinates(col = 2, row = 0)),
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                Square(piece = Bishop(Black), coordinates = Coordinates(col = 5, row = 0)),
                Square(piece = Rook(Black), coordinates = blackKingsideRookInitialCoordinates),
                // White
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
            )
        )
        val actual = findValidMoves(
            board = board,
            coordinates = blackKingInitialCoordinates,
        )
        // Queenside castling is not possible
        assertFalse(actual.contains(blackQueensideCastle))
        // Kingside castling is not possible
        assertFalse(actual.contains(blackKingsideCastle))
    }

    @Test
    fun testWhiteQueensideCastlingIsNotPossibleWhenQueenIsInItsInitialPosition() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                Square(piece = Rook(White), coordinates = whiteQueensideRookInitialCoordinates),
                Square(piece = Queen(White), coordinates = Piece.whiteQueenInitialCoordinates),
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(piece = Rook(White), coordinates = whiteKingsideRookInitialCoordinates),
            )
        )
        val actual = findValidMoves(
            board = board,
            coordinates = whiteKingInitialCoordinates,
        )
        // Queenside castling is not possible
        assertFalse(actual.contains(whiteQueensideCastle))
        // Kingside castling is possible
        assertContains(actual, whiteKingsideCastle)
    }

    @Test
    fun testBlackQueensideCastlingIsNotPossibleWhenQueenIsInItsInitialPosition() {
        val board = Board(
            moveColor = Black,
            squares = setOf(
                Square(piece = Rook(Black), coordinates = blackQueensideRookInitialCoordinates),
                Square(piece = Queen(Black), coordinates = Piece.blackQueenInitialCoordinates),
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                Square(piece = Rook(Black), coordinates = blackKingsideRookInitialCoordinates),
            )
        )
        val actual = findValidMoves(
            board = board,
            coordinates = blackKingInitialCoordinates,
        )
        // Queenside castling is not possible
        assertFalse(actual.contains(blackQueensideCastle))
        // Kingside castling is possible
        assertContains(actual, blackKingsideCastle)
    }

    @Test
    fun testCastlingIsNotPossibleWhenEnemyPieceIsOnAnIntermediateSquare() {
        val board = Board(
            moveColor = White,
            squares =
            setOf(
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(piece = Rook(Black), coordinates = Coordinates(col = 6, row = 7)),
                Square(piece = Rook(White), coordinates = whiteKingsideRookInitialCoordinates),
            )
        )
        val actual = findValidMoves(
            board = board,
            coordinates = whiteKingInitialCoordinates,
        )
        // Queenside castling is NOT possible
        assertFalse(actual.contains(whiteQueensideCastle))
        // Kingside castling is NOT possible
        assertFalse(actual.contains(whiteKingsideCastle))
    }

    @Test
    fun testCastlingIsNotPossibleWhenAnIntermediateSquareIsUnderAttack() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                Square(piece = Rook(White), coordinates = whiteKingsideRookInitialCoordinates),
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(piece = Rook(White), coordinates = whiteKingsideRookInitialCoordinates),

                Square(piece = Rook(Black), coordinates = Coordinates(col = 4, row = 6))
            )
        )
        val actual = findValidMoves(
            board = board,
            coordinates = whiteKingInitialCoordinates,
        )
        // Queenside castling is NOT possible
        assertFalse(actual.contains(whiteQueensideCastle))
        // Kingside castling is NOT possible
        assertFalse(actual.contains(whiteKingsideCastle))
    }

    @Test
    fun testCheckmate() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                // Black
                Square(
                    piece = King(Black, hasMoved = false),
                    coordinates = blackKingInitialCoordinates
                ),
                // White
                Square(
                    piece = Queen(White, hasMoved = true),
                    coordinates = Coordinates(col = 4, row = 1)
                ),
                Square(
                    piece = King(White, hasMoved = true),
                    coordinates = Coordinates(col = 4, row = 2)
                )
            ),
        )
        assertFalse(isCheckmate(board, White))
        assertTrue(isCheckmate(board, Black))
    }

    @Test
    fun testCheckmateWithPinnedRook() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                // White
                Square(
                    piece = King(White, hasMoved = true),
                    coordinates = Coordinates(col = 0, row = 7)
                ),
                Square(
                    piece = Bishop(White, hasMoved = true),
                    coordinates = Coordinates(col = 2, row = 2)
                ),
                // Black
                Square(
                    piece = King(Black, hasMoved = true),
                    coordinates = Coordinates(col = 0, row = 0)
                ),
                Square(
                    piece = Rook(Black, hasMoved = true),
                    coordinates = Coordinates(col = 1, row = 1)
                ),
                Square(
                    piece = Queen(Black, hasMoved = true),
                    coordinates = Coordinates(col = 1, row = 6)
                ),
            ),
        )
        assertFalse(isCheckmate(board, Black))
        assertTrue(isCheckmate(board, White))
    }

    @Test
    fun testCheckmate2() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                // Black
                Square(
                    piece = King(Black, hasMoved = false),
                    coordinates = blackKingInitialCoordinates
                ),
                // White
                Square(
                    piece = Queen(White, hasMoved = true),
                    coordinates = Coordinates(col = 6, row = 1)
                ),
                Square(
                    piece = King(White, hasMoved = true),
                    coordinates = Coordinates(col = 4, row = 2)
                )
            ),
        )
            .move(
                from = Coordinates(col = 6, row = 1),
                to = Coordinates(col = 6, row = 0)
            )
        assertFalse(isCheckmate(board, White))
        assertTrue(isCheckmate(board, Black))
    }

    @Test
    fun testStalemate() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                // Black
                Square(
                    piece = King(Black, hasMoved = false),
                    coordinates = blackKingInitialCoordinates
                ),
                // White
                Square(
                    piece = Bishop(White, hasMoved = true),
                    coordinates = Coordinates(col = 4, row = 1)
                ),
                Square(
                    piece = King(White, hasMoved = true),
                    coordinates = Coordinates(col = 4, row = 2)
                ),
            ),
        )
        assertFalse(isStalemate(board, White))
        assertTrue(isStalemate(board, Black))
    }

    @Test
    fun testPinnedPieceStalemate() {
        val board = Board(
            moveColor = White,
            squares = setOf(
                // Black
                Square(
                    piece = King(Black, hasMoved = true),
                    coordinates = Coordinates(col = 0, row = 0)
                ),
                Square(
                    piece = Pawn(Black, hasMoved = false),
                    coordinates = Coordinates(col = 1, row = 1)
                ),
                // White
                Square(
                    piece = King(White, hasMoved = true),
                    coordinates = Coordinates(col = 2, row = 0)
                ),
                Square(
                    piece = Bishop(White, hasMoved = true),
                    coordinates = Coordinates(col = 6, row = 7)
                ),
                Square(
                    piece = Bishop(White, hasMoved = true),
                    coordinates = Coordinates(col = 7, row = 7)
                ),
            ),
        )
        assertFalse(isStalemate(board, White))
        assertTrue(isStalemate(board, Black))
    }
}