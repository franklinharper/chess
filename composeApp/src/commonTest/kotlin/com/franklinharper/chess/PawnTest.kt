package com.franklinharper.chess

import com.franklinharper.chess.Piece.*
import com.franklinharper.chess.Piece.Companion.blackKingInitialCoordinates
import com.franklinharper.chess.Piece.Companion.whiteKingInitialCoordinates
import com.franklinharper.chess.PieceColor.*
import kotlin.test.Test
import kotlin.test.assertEquals

class PawnTest {

    @Test
    fun testNormalAndTwoSquareAdvanceMoves() {
        val board = Board(
            setOf(
                // White
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(
                    piece = Pawn(White, hasMoved = false, true),
                    coordinates = Coordinates(col = 4, row = 6)
                ),

                // Black
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                Square(
                    piece = Pawn(Black, hasMoved = false, true),
                    coordinates = Coordinates(col = 4, row = 1)
                ),
            )
        )
        assertEquals(
            message = "White pawn moves",
            expected = setOf(
                Coordinates(col = 4, row = 5),
                Coordinates(col = 4, row = 4)
            ),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 4, row = 6)),
        )
        assertEquals(
            message = "Black pawn moves",
            expected = setOf(
                Coordinates(col = 4, row = 2),
                Coordinates(col = 4, row = 3)
            ),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 4, row = 1)),
        )
    }

    @Test
    fun testTwoSquareAdvanceOnPreviousMoveFlag() {
        val boardAfterTwoSquareAdvance = Board(
            setOf(
                // Black
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                Square(
                    piece = Pawn(Black, hasMoved = false, true),
                    coordinates = Coordinates(col = 4, row = 1)
                ),
            )
        )
            .move(
                from = Coordinates(col = 4, row = 1),
                to = Coordinates(col = 4, row = 3),
            )
        // Pawn advances two squares, so the flag is set.
        assertEquals(
            expected = Pawn(Black, hasMoved = true, twoSquareAdvanceOnPreviousMove = true),
            actual = boardAfterTwoSquareAdvance.getPieceOrNull(Coordinates(col = 4, row = 3)),
        )

        // Another move occurs => the flag is set back to false.
        val boardAfterNormalMove = boardAfterTwoSquareAdvance
            .move(
                from = blackKingInitialCoordinates,
                to = Coordinates(col = 4, row = 1),
            )
        assertEquals(
            expected = Pawn(Black, hasMoved = true, twoSquareAdvanceOnPreviousMove = false),
            actual = boardAfterNormalMove.getPieceOrNull(Coordinates(col = 4, row = 3)),
        )
    }

        @Test
    fun testTwoSquareAdvanceMovesAreNotPossibleWhenPawnHasMoved() {
        val board = Board(
            setOf(
                // White
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(
                    piece = Pawn(White, hasMoved = true, true),
                    coordinates = Coordinates(col = 4, row = 5)
                ),

                // Black
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                Square(
                    piece = Pawn(Black, hasMoved = true, true),
                    coordinates = Coordinates(col = 4, row = 2)
                ),
            )
        )
        assertEquals(
            message = "White pawn moves",
            expected = setOf(
                Coordinates(col = 4, row = 4)
            ),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 4, row = 5)),
        )
        assertEquals(
            message = "Black pawn moves",
            expected = setOf(
                Coordinates(col = 4, row = 3)
            ),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 4, row = 2)),
        )
    }

    @Test
    fun testFriendlyPiecesBlockNormalAndTwoSquareAdvanceMoves() {
        val board = Board(
            setOf(
                // White
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(
                    piece = Pawn(White, hasMoved = false, true),
                    coordinates = Coordinates(col = 4, row = 6)
                ),
                Square(
                    piece = Bishop(White),
                    coordinates = Coordinates(col = 4, row = 5)
                ),

                // Black
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                Square(
                    piece = Pawn(Black, hasMoved = false, true),
                    coordinates = Coordinates(col = 4, row = 1)
                ),
                Square(
                    piece = Bishop(Black),
                    coordinates = Coordinates(col = 4, row = 2)
                ),
            )
        )
        assertEquals(
            message = "White pawn moves",
            expected = emptySet(),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 4, row = 6)),
        )
        assertEquals(
            message = "Black pawn moves",
            expected = emptySet(),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 4, row = 1)),
        )
    }

    @Test
    fun testFriendlyPiecesBlockTwoSquareAdvanceMoves() {
        val board = Board(
            setOf(
                // White
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(
                    piece = Pawn(White, hasMoved = false, true),
                    coordinates = Coordinates(col = 4, row = 6)
                ),
                Square(
                    piece = Bishop(White),
                    coordinates = Coordinates(col = 4, row = 4)
                ),

                // Black
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                Square(
                    piece = Pawn(Black, hasMoved = false, true),
                    coordinates = Coordinates(col = 4, row = 1)
                ),
                Square(
                    piece = Bishop(Black),
                    coordinates = Coordinates(col = 4, row = 3)
                ),
            )
        )
        assertEquals(
            message = "White pawn moves",
            expected = setOf(Coordinates(col = 4, row = 5)),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 4, row = 6)),
        )
        assertEquals(
            message = "Black pawn moves",
            expected = setOf(Coordinates(col = 4, row = 2)),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 4, row = 1)),
        )
    }

    @Test
    fun testEnemyPiecesBlockNormalAndTwoSquareAdvanceMoves() {
        val board = Board(
            setOf(
                // White
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(
                    piece = Pawn(White, hasMoved = false, true),
                    coordinates = Coordinates(col = 4, row = 6)
                ),
                Square(
                    piece = Bishop(White),
                    coordinates = Coordinates(col = 4, row = 2)
                ),

                // Black
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                Square(
                    piece = Pawn(Black, hasMoved = false, true),
                    coordinates = Coordinates(col = 4, row = 1)
                ),
                Square(
                    piece = Bishop(Black),
                    coordinates = Coordinates(col = 4, row = 5)
                ),
            )
        )
        assertEquals(
            message = "White pawn moves",
            expected = emptySet(),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 4, row = 6)),
        )
        assertEquals(
            message = "Black pawn moves",
            expected = emptySet(),
            actual = findValidMoves(board = board, coordinates = Coordinates(col = 4, row = 1)),
        )
    }

    @Test
    fun testBlackPawnCaptureMoves() {
        val board = Board(
            setOf(
                // Black
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                Square(
                    piece = Pawn(Black, twoSquareAdvanceOnPreviousMove = true),
                    coordinates = Coordinates(col = 4, row = 1)
                ),
                // This pawn prevents the previous pawn from making a normal move.
                Square(
                    piece = Pawn(Black, twoSquareAdvanceOnPreviousMove = true),
                    coordinates = Coordinates(col = 4, row = 2)
                ),

                // White
                // These pawns can be captured.
                Square(
                    piece = Pawn(White, twoSquareAdvanceOnPreviousMove = true),
                    coordinates = Coordinates(col = 3, row = 2)
                ),
                Square(
                    piece = Pawn(White, twoSquareAdvanceOnPreviousMove = true),
                    coordinates = Coordinates(col = 5, row = 2)
                ),
            )
        )
        assertEquals(
            message = "Black pawn moves",
            expected = setOf(
                Coordinates(col = 3, row = 2),
                Coordinates(col = 5, row = 2)
            ),
            actual = findValidMoves(
                board = board,
                coordinates = Coordinates(col = 4, row = 1)
            ),
        )
    }

    @Test
    fun testWhitePawnCaptureMoves() {
        val board = Board(
            setOf(
                // White
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(
                    piece = Pawn(White, hasMoved = false, true),
                    coordinates = Coordinates(col = 4, row = 6)
                ),
                // This pawn prevents the previous pawn from making a normal move.
                Square(
                    piece = Pawn(White, hasMoved = true, true),
                    coordinates = Coordinates(col = 4, row = 5)
                ),

                // Black
                // These pawns can be captured.
                Square(
                    piece = Pawn(Black, hasMoved = true, true),
                    coordinates = Coordinates(col = 3, row = 5)
                ),
                Square(
                    piece = Pawn(Black, hasMoved = true, true),
                    coordinates = Coordinates(col = 5, row = 5)
                ),
            )
        )
        assertEquals(
            expected = setOf(
                Coordinates(col = 3, row = 5),
                Coordinates(col = 5, row = 5)
            ),
            actual = findValidMoves(
                board = board,
                coordinates = Coordinates(col = 4, row = 6)
            ),
        )
    }

    @Test
    fun testPawnCantMoveBecauseKingIsInCheck() {
        val board = Board(
            setOf(
                // Black
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                Square(
                    piece = Pawn(Black, hasMoved = false, true),
                    coordinates = Coordinates(col = 0, row = 1)
                ),

                // White attacks King
                Square(
                    piece = Bishop(White),
                    coordinates = Coordinates(col = 0, row = 4)
                ),
            )
        )
        assertEquals(
            expected = emptySet(),
            actual = findValidMoves(
                board = board,
                coordinates = Coordinates(col = 0, row = 1)
            ),
        )
    }

    @Test
    fun testPawnCantItIsPinned() {
        val board = Board(
            setOf(
                // Black
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                Square(
                    piece = Pawn(Black, hasMoved = false, true),
                    coordinates = Coordinates(col = 3, row = 1)
                ),

                // White attacks King
                Square(
                    piece = Bishop(White),
                    coordinates = Coordinates(col = 0, row = 4)
                ),
            )
        )
        assertEquals(
            expected = emptySet(),
            actual = findValidMoves(
                board = board,
                coordinates = Coordinates(col = 3, row = 1)
            ),
        )
    }

    @Test
    fun testCaptureRemovesCheckOnKing() {
        val board = Board(
            setOf(
                // Black
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                Square(
                    piece = Pawn(Black, hasMoved = false, true),
                    coordinates = Coordinates(col = 3, row = 1)
                ),

                // White attacks King
                Square(
                    piece = Bishop(White),
                    coordinates = Coordinates(col = 2, row = 2)
                ),
            )
        )
        assertEquals(
            expected = setOf(Coordinates(col = 2, row = 2)),
            actual = findValidMoves(
                board = board,
                coordinates = Coordinates(col = 3, row = 1)
            ),
        )
    }

    @Test
    fun testPawnBlocksCheck() {
        val board = Board(
            setOf(
                // Black
                Square(piece = King(Black), coordinates = Coordinates(col = 4, row = 1)),
                Square(
                    piece = Pawn(Black, twoSquareAdvanceOnPreviousMove = true),
                    coordinates = Coordinates(col = 3, row = 1)
                ),

                // White attacks King
                Square(
                    piece = Bishop(White),
                    coordinates = Coordinates(col = 2, row = 3)
                ),
            )
        )
        assertEquals(
            expected = setOf(Coordinates(col = 3, row = 2)),
            actual = findValidMoves(
                board = board,
                coordinates = Coordinates(col = 3, row = 1)
            ),
        )
    }

    @Test
    fun testWhiteEnPassantCapture() {
        val initialBoard = Board(
            setOf(
                // Black
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                Square(
                    piece = Pawn(Black, hasMoved = false, twoSquareAdvanceOnPreviousMove = false),
                    coordinates = Coordinates(col = 4, row = 1)
                ),
                Square(
                    piece = Pawn(Black, hasMoved = false, twoSquareAdvanceOnPreviousMove = false),
                    coordinates = Coordinates(col = 6, row = 1)
                ),
                // White
                Square(
                    piece = Pawn(White, hasMoved = true, twoSquareAdvanceOnPreviousMove = false),
                    coordinates = Coordinates(col = 5, row = 3)
                ),
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
            )
        )
        val enPassantLeftPossibleBoard = initialBoard
            .move(
                // Black left pawn 2 square advance
                from = Coordinates(col = 4, row = 1),
                to = Coordinates(col = 4, row = 3)
            )

        assertEquals(
            expected = setOf(
                // White pawn can take black pawn en passant
                Coordinates(col = 4, row = 2),
                // White pawn can advance
                Coordinates(col = 5, row = 2),
            ),
            actual = findValidMoves(
                board = enPassantLeftPossibleBoard,
                coordinates = Coordinates(col = 5, row = 3)
            ),
        )
        val afterEnPassantLeftBoard = enPassantLeftPossibleBoard
            .move(
                from = Coordinates(col = 5, row = 3),
                to = Coordinates(col = 4, row = 2)
            )
        // White pawn has moved to new square
        assertEquals(
            expected = Pawn(White, hasMoved = true, twoSquareAdvanceOnPreviousMove = false),
            actual = afterEnPassantLeftBoard.getPieceOrNull(Coordinates(col = 4, row = 2)),
        )
        // Black left pawn was captured
        assertEquals(
            expected = null,
            actual = afterEnPassantLeftBoard.getPieceOrNull(Coordinates(col = 4, row = 3)),
        )

        val enPassantRightPossibleBoard = initialBoard
            .move(
                // Black right pawn 2 square advance
                from = Coordinates(col = 6, row = 1),
                to = Coordinates(col = 6, row = 3)
            )

        assertEquals(
            expected = setOf(
                // White pawn can take black right pawn en passant
                Coordinates(col = 5, row = 2),
                // White pawn can advance
                Coordinates(col = 6, row = 2),
            ),
            actual = findValidMoves(
                board = enPassantRightPossibleBoard,
                coordinates = Coordinates(col = 5, row = 3)
            ),
        )
        val afterEnPassantRightBoard = enPassantRightPossibleBoard
            .move(
                from = Coordinates(col = 5, row = 3),
                to = Coordinates(col = 6, row = 2)
            )
        // White pawn has moved to new square
        assertEquals(
            expected = Pawn(White, hasMoved = true, twoSquareAdvanceOnPreviousMove = false),
            actual = afterEnPassantRightBoard.getPieceOrNull(Coordinates(col = 6, row = 2)),
        )
        // Black pawn was captured
        assertEquals(
            expected = Pawn(Black, hasMoved = true, twoSquareAdvanceOnPreviousMove = true),
            actual = enPassantRightPossibleBoard.getPieceOrNull(Coordinates(col = 6, row = 3)),
        )
        assertEquals(
            expected = null,
            actual = afterEnPassantRightBoard.getPieceOrNull(Coordinates(col = 6, row = 3)),
        )
    }

    @Test
    fun testBlackEnPassantCapture() {
        val initialBoard = Board(
            setOf(
                // Black
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                Square(
                    piece = Pawn(Black, hasMoved = true, twoSquareAdvanceOnPreviousMove = false),
                    coordinates = Coordinates(col = 5, row = 4)
                ),
                // White
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(
                    piece = Pawn(White, hasMoved = false, twoSquareAdvanceOnPreviousMove = false),
                    coordinates = Coordinates(col = 4, row = 6)
                ),
                Square(
                    piece = Pawn(White, hasMoved = false, twoSquareAdvanceOnPreviousMove = false),
                    coordinates = Coordinates(col = 6, row = 6)
                ),
            )
        )
        // En passant left
        val enPassantLeftPossibleBoard = initialBoard
            .move(
                // White left pawn 2 square advance
                from = Coordinates(col = 4, row = 6),
                to = Coordinates(col = 4, row = 4)
            )

        assertEquals(
            expected = setOf(
                // Black pawn can take white pawn en passant
                Coordinates(col = 4, row = 5),
                // Black pawn can advance
                Coordinates(col = 5, row = 5),
            ),
            actual = findValidMoves(
                board = enPassantLeftPossibleBoard,
                coordinates = Coordinates(col = 5, row = 4)
            ),
        )
        val afterEnPassantLeftBoard = enPassantLeftPossibleBoard
            .move(
                from = Coordinates(col = 5, row = 4),
                to = Coordinates(col = 4, row = 5)
            )
        // Black pawn has moved to new square
        assertEquals(
            expected = Pawn(Black, hasMoved = true, twoSquareAdvanceOnPreviousMove = false),
            actual = afterEnPassantLeftBoard.getPieceOrNull(Coordinates(col = 4, row = 5)),
        )
        // Black left pawn was captured
        assertEquals(
            expected = null,
            actual = afterEnPassantLeftBoard.getPieceOrNull(Coordinates(col = 4, row = 4)),
        )

        // En passant right
        val enPassantRightPossibleBoard = initialBoard
            .move(
                // White right pawn 2 square advance
                from = Coordinates(col = 6, row = 6),
                to = Coordinates(col = 6, row = 4)
            )

        assertEquals(
            expected = setOf(
                // Black pawn can take white right pawn en passant
                Coordinates(col = 6, row = 5),
                // Black pawn can advance
                Coordinates(col = 5, row = 5),
            ),
            actual = findValidMoves(
                board = enPassantRightPossibleBoard,
                coordinates = Coordinates(col = 5, row = 4)
            ),
        )
        val afterEnPassantRightBoard = enPassantRightPossibleBoard
            .move(
                from = Coordinates(col = 5, row = 4),
                to = Coordinates(col = 6, row = 5)
            )
        // Black pawn has moved to new square
        assertEquals(
            expected = Pawn(Black, hasMoved = true, twoSquareAdvanceOnPreviousMove = false),
            actual = afterEnPassantRightBoard.getPieceOrNull(Coordinates(col = 6, row = 5)),
        )
        // White pawn was captured
        assertEquals(
            expected = Pawn(White, hasMoved = true, twoSquareAdvanceOnPreviousMove = true),
            actual = enPassantRightPossibleBoard.getPieceOrNull(Coordinates(col = 6, row = 4)),
        )
        assertEquals(
            expected = null,
            actual = afterEnPassantLeftBoard.getPieceOrNull(Coordinates(col = 6, row = 4)),
        )
    }

    @Test
    fun testEnPassantIsNotPossibleBecauseNoTwoSquareAdvanceOnPreviousMove() {
        val initialBoard = Board(
            setOf(
                // Black
                Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
                Square(
                    piece = Pawn(Black, hasMoved = true, twoSquareAdvanceOnPreviousMove = false),
                    coordinates = Coordinates(col = 5, row = 4)
                ),
                // White
                Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
                Square(
                    piece = Pawn(White, hasMoved = false, twoSquareAdvanceOnPreviousMove = false),
                    coordinates = Coordinates(col = 4, row = 6)
                ),
            )
        )
        // White pawn advances twice
        val board = initialBoard
            .move(
                from = Coordinates(col = 4, row = 6),
                to = Coordinates(col = 4, row = 5)
            )
            .move(
                from = Coordinates(col = 4, row = 5),
                to = Coordinates(col = 4, row = 4)
            )

        assertEquals(
            expected = setOf(
                // Black pawn can advance
                Coordinates(col = 5, row = 5),
            ),
            actual = findValidMoves(
                board = board,
                coordinates = Coordinates(col = 5, row = 4)
            ),
        )
    }
}