package com.franklinharper.chess

import com.franklinharper.chess.Board
import com.franklinharper.chess.PieceColor
import com.franklinharper.chess.squareCanBeAttacked
import com.franklinharper.chess.PieceColor.White

// Could this be a sealed interface?

sealed class Piece {

    companion object {
        // Coordinates that are used frequently
        //
        // White Initial coordinates
        val whiteKingInitialCoordinates = Coordinates(col = 4, row = 7)
        val whiteQueenInitialCoordinates = Coordinates(col = 3, row = 7)
        val whiteQueensideRookInitialCoordinates = Coordinates(col = 0, row = 7)
        val whiteKingsideRookInitialCoordinates = Coordinates(col = 7, row = 7)

        // White Castling coordinates
        val whiteQueensideCastle = Coordinates(col = 2, row = 7)
        val whiteKingsideCastle = Coordinates(col = 6, row = 7)

        // Black Initial coordinates
        val blackKingInitialCoordinates = Coordinates(col = 4, row = 0)
        val blackQueenInitialCoordinates = Coordinates(col = 3, row = 0)
        val blackQueensideRookInitialCoordinates = Coordinates(col = 0, row = 0)
        val blackKingsideRookInitialCoordinates = Coordinates(col = 7, row = 0)

        // Black Castling coordinates
        val blackQueensideCastle = Coordinates(col = 2, row = 0)
        val blackKingsideCastle = Coordinates(col = 6, row = 0)
    }

    // For a given Square, return the moves that a piece can make.
    //
    // @param board Board
    // @return Set<Coordinates>
    //
    // Possible attacks include squares that are occupied by a enemy piece.
    // This is because, in this move, a friendly piece can capture that enemy piece
    // This would put the friendly piece that captured under attack.
    //
    // An important example of this is when a King can't capture a piece because
    // it would put itself in check. Because of this some
    // A King cannot move into check when the attacking enemy piece is itself pinned
    // Some possible attacks are not valid moves. An example of is when a piece is pinned.
    // A pinned piece has a set of possible attacks; but some won't be valid moves because they
    // would expose the King to an attack.
    abstract fun findMoveDestinationCoordinates(
        board: Board,
        fromCoordinates: Coordinates,
    ): Set<Coordinates>

    abstract val color: PieceColor
    abstract val hasMoved: Boolean

    abstract fun copy(hasMoved: Boolean): Piece

    private fun hasNotMoved() = !hasMoved

    data class Rook(
        override val color: PieceColor,
        override val hasMoved: Boolean = false,
    ) : Piece() {
        override fun findMoveDestinationCoordinates(
            board: Board,
            fromCoordinates: Coordinates,
        ): Set<Coordinates> {
            val possibleMoves = mutableSetOf<Coordinates>()
            possibleMoves.addAll(rowLeft(board, fromCoordinates))
            possibleMoves.addAll(rowRight(board, fromCoordinates))
            possibleMoves.addAll(colUp(board, fromCoordinates))
            possibleMoves.addAll(colDown(board, fromCoordinates))
            return possibleMoves
        }

        override fun copy(hasMoved: Boolean) = Rook(this.color, hasMoved)
    }

    data class Knight(
        override val color: PieceColor,
        override val hasMoved: Boolean = false,
    ) : Piece() {
        private val offsets = setOf(
            // Offsets are Pair(col, row)

            // Above
            Pair(-1, -2),
            Pair(1, -2),

            //Left
            Pair(-2, 1),
            Pair(-2, -1),

            // Right
            Pair(2, 1),
            Pair(2, -1),

            // Below
            Pair(-1, 2),
            Pair(1, 2),
        )

        override fun findMoveDestinationCoordinates(
            board: Board,
            fromCoordinates: Coordinates,
        ): Set<Coordinates> = findPossibleMovesByOffsets(
            board = board,
            sourceCoordinates = fromCoordinates,
            offsets = offsets
        )

        override fun copy(hasMoved: Boolean) = Knight(this.color, hasMoved)
    }

    data class Bishop(
        override val color: PieceColor,
        override val hasMoved: Boolean = false,
    ) : Piece() {
        // We aren't checking if Bishops are on a Square of the same color as the Bishop.
        // This allows an issue to occur when a Bishop is put on a Square where the color of the Square doesn't
        // match the color of the Bishop.
        override fun findMoveDestinationCoordinates(
            board: Board,
            fromCoordinates: Coordinates,
        ): Set<Coordinates> {
            val possibleMoves = mutableSetOf<Coordinates>()
            possibleMoves.addAll(upperLeftDiagonal(board, fromCoordinates))
            possibleMoves.addAll(upperRightDiagonal(board, fromCoordinates))
            possibleMoves.addAll(lowerLeftDiagonal(board, fromCoordinates))
            possibleMoves.addAll(lowerRightDiagonal(board, fromCoordinates))
            return possibleMoves
        }

        override fun copy(hasMoved: Boolean) = Bishop(this.color, hasMoved)
    }

    data class King(
        override val color: PieceColor,
        override val hasMoved: Boolean = false,
    ) : Piece() {
        fun isInCheck(
            board: Board,
            originCoordinates: Coordinates,
        ): Boolean = squareCanBeAttacked(
            board = board,
            attackingColor = color.enemyColor(),
            coordinates = originCoordinates,
        )

        private fun squareIsEmptyAndCanNotBeAttacked(
            board: Board,
            enemyColor: PieceColor,
            coordinates: Coordinates,
        ) = board.getSquareOrNull(coordinates)!!.isEmpty() &&
                !squareCanBeAttacked(
                    board = board,
                    attackingColor = enemyColor,
                    coordinates = coordinates
                )

        private fun checkForCastlingMove(
            board: Board,
            enemyColor: PieceColor,
            cornerCoordinates: Coordinates, // Corner where there might be a Rook
            intermediateSquares: Set<Coordinates>, // Squares between the corner and the king
        ): Boolean {
            val cornerPiece = board.getPieceOrNull(cornerCoordinates)
            val cornerRookHasNotMoved = cornerPiece is Rook && cornerPiece.hasNotMoved()
            val intermediateSquaresAreEmptyAndCannotBeAttacked = intermediateSquares.all { coordinates ->
                squareIsEmptyAndCanNotBeAttacked(
                    board = board,
                    enemyColor = enemyColor,
                    coordinates = coordinates,
                )
            }
            return cornerRookHasNotMoved &&
                    intermediateSquaresAreEmptyAndCannotBeAttacked
        }

        override fun findMoveDestinationCoordinates(
            board: Board,
            fromCoordinates: Coordinates,
        ): Set<Coordinates> {
            val king = board.getPieceOrNull(fromCoordinates)
            require(king is King)

            val enemyColor = king.color.enemyColor()

            val normalMoves = Board.neighborOffsets.mapNotNull {
                val offsetCoordinates = Coordinates(
                    col = fromCoordinates.col + it.first,
                    row = fromCoordinates.col + it.second,
                )
                board.getSquareOrNull(offsetCoordinates)
            }.filter { destinationSquare ->
                val destinationPiece = board.getPieceOrNull(destinationSquare.coordinates)
                val destinationSquareIsEmptyOrEnemy = destinationPiece == null || destinationPiece.color == enemyColor
                val isUnderAttack = squareCanBeAttacked(
                    board = board,
                    attackingColor = enemyColor,
                    coordinates = destinationSquare.coordinates,
                )
                destinationSquareIsEmptyOrEnemy && !isUnderAttack
            }.map { square ->
                square.coordinates
            }.toSet()

            return normalMoves + castlingMoves(board, king)
        }

        private fun castlingMoves(
            board: Board,
            king: King,
        ): Set<Coordinates> {
            val kingCoordinates = if (color == White) whiteKingInitialCoordinates else blackKingInitialCoordinates
            if (king.hasMoved || king.isInCheck(board, kingCoordinates)) {
                return emptySet()
            }
            val castlingMoves = mutableSetOf<Coordinates>()
            val row = if (color == White) 7 else 0
            val enemyColor = color.enemyColor() // Kingside
            val kingsideRookCoordinates = Coordinates(col = 7, row = row)
            if (checkForCastlingMove(
                    board = board,
                    enemyColor = enemyColor,
                    cornerCoordinates = kingsideRookCoordinates,
                    intermediateSquares = setOf(
                        Coordinates(col = 5, row = row),
                        Coordinates(col = 6, row = row),
                    )
                )
            ) {
                castlingMoves.add(if (color == White) whiteKingsideCastle else blackKingsideCastle)
            } // Queenside
            val queensideRookCoordinates = Coordinates(col = 0, row = row)
            if (checkForCastlingMove(
                    board = board,
                    enemyColor = enemyColor,
                    cornerCoordinates = queensideRookCoordinates,
                    intermediateSquares = setOf(
                        Coordinates(col = 1, row = row),
                        Coordinates(col = 2, row = row),
                        Coordinates(col = 3, row = row)
                    )
                )
            ) {
                castlingMoves.add(if (color == White) whiteQueensideCastle else blackQueensideCastle)
            }
            return castlingMoves
        }

        override fun copy(hasMoved: Boolean) = King(this.color, hasMoved)
    }

    data class Queen(
        override val color: PieceColor,
        override val hasMoved: Boolean = false,
    ) : Piece() {
        override fun findMoveDestinationCoordinates(
            board: Board,
            fromCoordinates: Coordinates,
        ): Set<Coordinates> {
            val possibleMoves = mutableSetOf<Coordinates>()

            // Diagonals
            possibleMoves.addAll(upperLeftDiagonal(board, fromCoordinates))
            possibleMoves.addAll(upperRightDiagonal(board, fromCoordinates))
            possibleMoves.addAll(lowerLeftDiagonal(board, fromCoordinates))
            possibleMoves.addAll(lowerRightDiagonal(board, fromCoordinates))

            // Rows and Columns
            possibleMoves.addAll(rowLeft(board, fromCoordinates))
            possibleMoves.addAll(rowRight(board, fromCoordinates))
            possibleMoves.addAll(colUp(board, fromCoordinates))
            possibleMoves.addAll(colDown(board, fromCoordinates))
            return possibleMoves
        }

        override fun copy(hasMoved: Boolean) = Queen(this.color, hasMoved)
    }

    data class Pawn(
        override val color: PieceColor,
        override val hasMoved: Boolean = false,
        val twoSquareAdvanceOnPreviousMove: Boolean = false,
    ) : Piece() {

        override fun findMoveDestinationCoordinates(
            board: Board,
            fromCoordinates: Coordinates,
        ): Set<Coordinates> {
            // Capture Moves
            val captureOffsets = if (color == White) whiteCaptureOffsets else blackCaptureOffsets
            val candidateCaptureMoves = captureOffsets
                .mapNotNull {
                    val offsetCoordinates = Coordinates(
                        col = fromCoordinates.col + it.first,
                        row = fromCoordinates.row + it.second
                    )
                    board.getSquareOrNull(offsetCoordinates)
                }.filter { destinationSquare ->
                    // Capture moves require an enemy piece to capture!
                    destinationSquare.containsEnemyPiece(friendlyColor = color)
                }.map { square ->
                    square.coordinates
                }.toSet()
            // Normal Move
            val rowDelta = if (color == White) -1 else 1
            val normalMoveCoordinates = Coordinates(
                col = fromCoordinates.col,
                row = fromCoordinates.row + rowDelta
            )
            val normalMoveSquare = board.getSquareOrNull(normalMoveCoordinates)
            val candidateNormalMove = when {
                normalMoveSquare == null -> emptySet() // Off the edge of the board
                normalMoveSquare.isEmpty() -> setOf(normalMoveCoordinates)
                else -> emptySet() // A piece is already on the square
            }
            // Two Square Advance
            val rowDelta2 = if (color == White) -2 else 2
            val twoSquareAdvanceCoordinates = Coordinates(
                col = fromCoordinates.col,
                row = fromCoordinates.row + rowDelta2
            )
            val twoSquareAdvanceSquare = board.getSquareOrNull(twoSquareAdvanceCoordinates)
            val candidateTwoSquareAdvanceMove = when {
                hasMoved -> emptySet()
                // Two square advance is only possible when the normal move is possible.
                candidateNormalMove.isEmpty() -> emptySet()
                // If the pawn hasn't moved then the two square advance square is not off the board,
                // so we can use !! with impunity!
                twoSquareAdvanceSquare!!.isEmpty() -> setOf(twoSquareAdvanceCoordinates)
                else -> emptySet() // A piece is on the square
            }
            val enPassantLeft = enPassantMoveSet(
                colDelta = -1,
                board = board,
                from = fromCoordinates
            )
            val enPassantRight = enPassantMoveSet(
                colDelta = 1,
                board = board,
                from = fromCoordinates
            )
            return (
                    candidateCaptureMoves +
                            candidateNormalMove +
                            candidateTwoSquareAdvanceMove +
                            enPassantLeft +
                            enPassantRight
                    )
                .filter { coordinates ->
                    kingIsNotInCheckAfterMove(board, fromCoordinates, coordinates)
                }
                .toSet()
        }

        private fun enPassantMoveSet(
            board: Board,
            from: Coordinates,
            colDelta: Int,
        ): Set<Coordinates> {
            val possiblePawnCoordinates = from.copy(col = from.col + colDelta)
            val piece = board.getPieceOrNull(possiblePawnCoordinates)
            val isEnPassantPossible = piece is Pawn &&
                    piece.color == color.enemyColor() &&
                    piece.twoSquareAdvanceOnPreviousMove
            val rowDelta = if (color == White) -1 else 1
            return if (isEnPassantPossible)
                setOf(possiblePawnCoordinates.copy(row = from.row + rowDelta))
            else
                emptySet()
        }

        companion object {
            // Offsets are Pair(col, row)
            private val whiteCaptureOffsets = setOf(Pair(-1, -1), Pair(1, -1))
            private val blackCaptureOffsets = setOf(Pair(-1, 1), Pair(1, 1))
        }

        override fun copy(hasMoved: Boolean) = Pawn(this.color, hasMoved)
    }

    internal fun findPossibleMovesByOffsets(
        board: Board,
        sourceCoordinates: Coordinates,
        offsets: Set<Pair<Int, Int>>,
    ): Set<Coordinates> {
        val enemyPieceColor = board.getPieceOrNull(sourceCoordinates)?.color?.enemyColor()
        return offsets.mapNotNull {
            val offsetCoordinates = Coordinates(
                col = sourceCoordinates.col + it.first,
                row = sourceCoordinates.row + it.second
            )
            board.getSquareOrNull(offsetCoordinates)
        }.filter { destinationSquare ->
            val destinationIsEmptyOrEnemy = destinationSquare.isEmpty() ||
                    destinationSquare.piece!!.color == enemyPieceColor
            val kingIsNotInCheckAfterMove = kingIsNotInCheckAfterMove(
                board,
                sourceCoordinates,
                destinationSquare.coordinates
            )
            destinationIsEmptyOrEnemy && kingIsNotInCheckAfterMove
        }.map { square ->
            square.coordinates
        }.toSet()
    }

    internal fun kingIsNotInCheckAfterMove(
        board: Board,
        from: Coordinates,
        to: Coordinates,
    ): Boolean {
        val newBoard = board.move(
            from = from,
            to = to
        )
        val king = newBoard.getKingSquare(color)!!
        return !squareCanBeAttacked(
            board = newBoard,
            attackingColor = color.enemyColor(),
            coordinates = king.coordinates
        )
    }

    internal fun rowLeft(board: Board, initialCoordinates: Coordinates): Collection<Coordinates> =
        findPossibleMoveCoordinates(
            board = board,
            initialCoordinates = initialCoordinates,
            colDelta = -1,
            rowDelta = 0,
        )

    internal fun rowRight(board: Board, initialCoordinates: Coordinates): Collection<Coordinates> =
        findPossibleMoveCoordinates(
            board = board,
            initialCoordinates = initialCoordinates,
            colDelta = 1,
            rowDelta = 0,
        )

    internal fun colUp(board: Board, initialCoordinates: Coordinates): Collection<Coordinates> =
        findPossibleMoveCoordinates(
            board = board,
            initialCoordinates = initialCoordinates,
            colDelta = 0,
            rowDelta = -1,
        )

    internal fun colDown(
        board: Board,
        initialCoordinates: Coordinates,
    ): Collection<Coordinates> =
        findPossibleMoveCoordinates(
            board = board,
            initialCoordinates = initialCoordinates,
            colDelta = 0,
            rowDelta = 1,
        )

    internal fun upperLeftDiagonal(board: Board, initialCoordinates: Coordinates): Collection<Coordinates> =
        findPossibleMoveCoordinates(
            board = board,
            initialCoordinates = initialCoordinates,
            colDelta = -1,
            rowDelta = -1,
        )

    internal fun upperRightDiagonal(board: Board, initialCoordinates: Coordinates): Collection<Coordinates> =
        findPossibleMoveCoordinates(
            board = board,
            initialCoordinates = initialCoordinates,
            colDelta = 1,
            rowDelta = -1,
        )

    internal fun lowerRightDiagonal(board: Board, initialCoordinates: Coordinates): Collection<Coordinates> =
        findPossibleMoveCoordinates(
            board = board,
            initialCoordinates = initialCoordinates,
            colDelta = 1,
            rowDelta = 1,
        )

    internal fun lowerLeftDiagonal(board: Board, initialCoordinates: Coordinates): Collection<Coordinates> =
        findPossibleMoveCoordinates(
            board = board,
            initialCoordinates = initialCoordinates,
            colDelta = -1,
            rowDelta = 1,
        )

    // Find possible moves, in a given direction, for pieces that can't jump over other pieces. I.e. not Knights.
    // The direction to explore is defined by the [colDelta] and [rowDelta] values.
    private fun findPossibleMoveCoordinates(
        board: Board,
        initialCoordinates: Coordinates,
        colDelta: Int,
        rowDelta: Int,
    ): Collection<Coordinates> {
        require(colDelta != 0 || rowDelta != 0)

        val possibleMoves = mutableListOf<Coordinates>()

        var col = initialCoordinates.col + initialOffset(colDelta)
        var row = initialCoordinates.row + initialOffset(rowDelta)
        while (true) {
            val toCoordinates = Coordinates(col = col, row = row)
            val toSquare = board.getSquareOrNull(toCoordinates) ?: return possibleMoves // Off the edge of the board
            when {
                toSquare.containsFriendlyPiece(friendlyColor = color) -> return possibleMoves

                toSquare.containsEnemyPiece(friendlyColor = color) -> {
                    if (kingIsNotInCheckAfterMove(board = board, from = initialCoordinates, to = toCoordinates)
                    ) {
                        possibleMoves.add(toCoordinates)
                    }
                    return possibleMoves
                }

                toSquare.isEmpty() -> {
                    if (kingIsNotInCheckAfterMove(board = board, from = initialCoordinates, to = toCoordinates)) {
                        possibleMoves.add(toCoordinates)
                    }
                }
            }
            col += colDelta
            row += rowDelta
        }
    }

    private fun initialOffset(delta: Int): Int = when {
        delta > 0 -> 1
        delta == 0 -> 0
        else -> -1
    }
}
