package com.franklinharper.chess

import com.franklinharper.chess.BoardStatus.*
import com.franklinharper.chess.Piece.*
import com.franklinharper.chess.Piece.Companion.blackKingInitialCoordinates
import com.franklinharper.chess.Piece.Companion.whiteKingInitialCoordinates
import com.franklinharper.chess.PieceColor.Black
import com.franklinharper.chess.PieceColor.White
import kotlin.math.abs
import kotlin.reflect.KClass

// Internal API
// Board(squares: Set<Square>))
// board.move(from, to)
// board.getPiece(coordinates) or board.getSquare(coordinates)?
// board.getKing(color)
//
// High level API (extension functions)
// board.canBeMovedTo(pieceColor, coordinates)

data class Board(
    val squareMap: Map<Coordinates, Square> = emptyMap(),
    val moveColor: PieceColor, // Who's turn is it to move?
) {
    constructor(
        squares: Set<Square>,
        moveColor: PieceColor,
    ) : this(
        squareMap = squares.associateBy { square -> square.coordinates },
        moveColor = moveColor,
    )

    constructor() : this(
        squares = initialSetup,
        moveColor = White,
    )

    // TODO write tests for this function?
    fun clickSquare(
        colIndex: Int,
        rowIndex: Int,
    ): Board {
        val square = getSquare(
            colIndex = colIndex,
            rowIndex = rowIndex,
        )
        println("squareMap(4,7)=${squareMap[Coordinates(col = 4, row = 7)]}")

        return when {
            // Move piece
            square.isValidMoveDestination -> {
                val fromSquare = getSelectedSquare()!!
                copyAndDeselectAllSquares()
                    .copyAndUpdateValidMoves(emptySet())
                    .move(
                        from = fromSquare.coordinates,
                        to = square.coordinates,
                    )
            }

            // Select square
            square.isNotSelected
                    && square.piece != null
                    && square.piece.color == getStatus().toColor() -> {

                val validMoves = findValidMoves(
                    board = this,
                    coordinates = square.coordinates,
                )
                copyAndDeselectAllSquares()
                    .copyAndUpdateValidMoves(validMoves)
                    .copyAndReplaceSquare(square.copy(isSelected = true))
            }

            else -> {
                copyAndDeselectAllSquares()
                    .copyAndUpdateValidMoves(validMoves = emptySet())
            }
        }
    }

    fun move(
        from: Coordinates,
        to: Coordinates,
    ): Board {
        val newMap = copyMapAndMakeMove(
            fromPiece = getSquare(from).piece!!,
            from = from,
            to = to,
        )
        return Board(
            squareMap = newMap,
            moveColor = moveColor.enemyColor()
        )
    }

    fun getStatus(): BoardStatus =
        when {
            moveColor == White && isCheckmate(board = this, color = White)
            -> BlackWin

            moveColor == Black && isCheckmate(board = this, color = Black)
            -> WhiteWin

            isStalemate(board = this, color = moveColor)
            -> Stalemate

            else
            -> when (moveColor) {
                White -> WhitesMove
                Black -> BlacksMove
            }
        }

    private fun copyMapAndMakeMove(
        fromPiece: Piece,
        from: Coordinates,
        to: Coordinates,
    ): Map<Coordinates, Square> {
        val mutableMap = squareMap.toMutableMap()

        // Ensure that none of the friendly pawn's twoSquareAdvanceOnPreviousMove flags are set.
        for (square in mutableMap.values) {
            val piece = square.piece
            if (piece is Pawn && piece.color == piece.color && piece.twoSquareAdvanceOnPreviousMove) {
                mutableMap[square.coordinates] = square.copy(
                    piece = piece.copy(twoSquareAdvanceOnPreviousMove = false)
                )
                break // Only one pawn can have made a 2 square advance on the previous move
            }
        }
        val movedPiece = when (fromPiece) {
            is Pawn -> {
                fromPiece.copy(
                    hasMoved = true,
                    twoSquareAdvanceOnPreviousMove = abs(to.row - from.row) == 2,
                    isWaitingForPromotion = to.row == 0 || to.row == 7,
                )
            }

            else -> {
                fromPiece.copy(hasMoved = true)
            }
        }
        mutableMap.remove(key = from)
        mutableMap[to] = Square(piece = movedPiece, coordinates = to)

        // Check if this move requires moving other pieces.

        // En passant
        val isEnPassant = movedPiece is Pawn && getPieceOrNull(to) == null
        if (isEnPassant) {
            val rowOffset = if (movedPiece.color == White) 1 else -1
            val enemyPawnCoordinates = to.copy(row = to.row + rowOffset)
            mutableMap.remove(enemyPawnCoordinates)
        }

        // Castling
        val isCastling = movedPiece is King && abs(to.col - from.col) == 2
        if (isCastling) {
            // Also move the rook
            if (to.col > from.col) {
                // Kingside
                val rookFrom = Coordinates(col = 7, row = from.row)
                val rookTo = Coordinates(col = 5, row = from.row)
                mutableMap.remove(rookFrom)
                mutableMap[rookTo] = Square(
                    piece = Rook(movedPiece.color, hasMoved = true),
                    coordinates = rookTo
                )
            } else {
                // Queenside
                val rookFrom = Coordinates(col = 0, row = from.row)
                val rookTo = Coordinates(col = 3, row = from.row)
                mutableMap.remove(rookFrom)
                mutableMap[rookTo] = Square(
                    piece = Rook(movedPiece.color, hasMoved = true),
                    coordinates = rookTo
                )
            }
        }
        return mutableMap
    }

    fun getSquareOrNull(coordinates: Coordinates) = squareMap.getOrElse(coordinates) {
        if (coordinates.col in 0..7 && coordinates.row in 0..7)
        // Empty Square
            Square(coordinates, piece = null)
        else
        // Square out of bounds
            null
    }

    fun getSquare(colIndex: Int, rowIndex: Int) = getSquareOrNull(Coordinates(colIndex, rowIndex))!!
    fun getSquare(coordinates: Coordinates) = getSquareOrNull(coordinates)!!

    fun getPieceOrNull(coordinates: Coordinates) = getSquareOrNull(coordinates)?.piece

    // This function could be made faster by storing the King's coordinates when
    // the board is created and updated.
    fun getKingSquare(color: PieceColor) = squareMap.values.find { square ->
        square.piece is King && square.piece.color == color
    }!!

    // This function could be made faster by storing the select square's coordinates when
    // the board is created and updated.
    private fun getSelectedSquare() = squareMap.values.find { it.isSelected }

    private fun copyAndUpdateValidMoves(validMoves: Set<Coordinates>): Board {
        val mutableMap = squareMap.toMutableMap()
        for (square in squareMap.values) {
            if (square.isValidMoveDestination) mutableMap[square.coordinates] =
                square.copy(isValidMoveDestination = false)

        }
        for (coordinates in validMoves) {
            mutableMap[coordinates] = getSquare(coordinates).copy(isValidMoveDestination = true)
        }
        return Board(
            squareMap = mutableMap,
            moveColor = moveColor,
        )
    }

    private fun copyAndDeselectAllSquares(): Board {
        val mutableMap = squareMap.toMutableMap()
        for (square in mutableMap.values) {
            if (square.isSelected) mutableMap[square.coordinates] = square.copy(isSelected = false)
        }
        return Board(
            squareMap = mutableMap,
            moveColor = moveColor,
        )
    }

    private fun copyAndReplaceSquare(newSquare: Square): Board {
        val mutableMap = squareMap.toMutableMap()
        mutableMap[newSquare.coordinates] = newSquare
        return Board(
            squareMap = mutableMap,
            moveColor = moveColor,
        )
    }

    override fun toString(): String {
        return "moveColor=$moveColor, squares=${squareMap.values})"
    }

    fun removePiece(from: Coordinates): Board {
        val mutableMap = squareMap.toMutableMap()
        mutableMap.remove(from)
        // The board status is not recalculated; which is a bug.
        // TODO fix the bug by removing the boardSatus property from Board
        return Board(squareMap = mutableMap, moveColor = moveColor)
    }

    fun findPromotionSquare(): Square? {
        return squareMap
            .values
            .find { square -> square.piece is Pawn && square.piece.isWaitingForPromotion }
    }

    fun replacePiece(newPiece: Piece, square: Square): Board {
        val mutableMap = squareMap.toMutableMap()
        mutableMap[square.coordinates] = square.copy(piece = newPiece)
        return copy(squareMap = mutableMap)
    }

    companion object {

        val initialSetup = setOf(
            // Black
            Square(piece = Rook(Black), coordinates = Coordinates(col = 0, row = 0)),
            Square(piece = Knight(Black), coordinates = Coordinates(col = 1, row = 0)),
            Square(piece = Bishop(Black), coordinates = Coordinates(col = 2, row = 0)),
            Square(piece = Queen(Black), coordinates = Coordinates(col = 3, row = 0)),
            Square(piece = King(Black), coordinates = blackKingInitialCoordinates),
            Square(piece = Bishop(Black), coordinates = Coordinates(col = 5, row = 0)),
            Square(piece = Knight(Black), coordinates = Coordinates(col = 6, row = 0)),
            Square(piece = Rook(Black), coordinates = Coordinates(col = 7, row = 0)),

            Square(piece = Pawn(Black), coordinates = Coordinates(col = 0, row = 1)),
            Square(piece = Pawn(Black), coordinates = Coordinates(col = 1, row = 1)),
            Square(piece = Pawn(Black), coordinates = Coordinates(col = 2, row = 1)),
            Square(piece = Pawn(Black), coordinates = Coordinates(col = 3, row = 1)),
            Square(piece = Pawn(Black), coordinates = Coordinates(col = 4, row = 1)),
            Square(piece = Pawn(Black), coordinates = Coordinates(col = 5, row = 1)),
            Square(piece = Pawn(Black), coordinates = Coordinates(col = 6, row = 1)),
            Square(piece = Pawn(Black), coordinates = Coordinates(col = 7, row = 1)),

            // White
            Square(piece = Pawn(White), coordinates = Coordinates(col = 0, row = 6)),
            Square(piece = Pawn(White), coordinates = Coordinates(col = 1, row = 6)),
            Square(piece = Pawn(White), coordinates = Coordinates(col = 2, row = 6)),
            Square(piece = Pawn(White), coordinates = Coordinates(col = 3, row = 6)),
            Square(piece = Pawn(White), coordinates = Coordinates(col = 4, row = 6)),
            Square(piece = Pawn(White), coordinates = Coordinates(col = 5, row = 6)),
            Square(piece = Pawn(White), coordinates = Coordinates(col = 6, row = 6)),
            Square(piece = Pawn(White), coordinates = Coordinates(col = 7, row = 6)),

            Square(piece = Rook(White), coordinates = Coordinates(col = 0, row = 7)),
            Square(piece = Knight(White), coordinates = Coordinates(col = 1, row = 7)),
            Square(piece = Bishop(White), coordinates = Coordinates(col = 2, row = 7)),
            Square(piece = Queen(White), coordinates = Coordinates(col = 3, row = 7)),
            Square(piece = King(White), coordinates = whiteKingInitialCoordinates),
            Square(piece = Bishop(White), coordinates = Coordinates(col = 5, row = 7)),
            Square(piece = Knight(White), coordinates = Coordinates(col = 6, row = 7)),
            Square(piece = Rook(White), coordinates = Coordinates(col = 7, row = 7)),
        )
        val endGameSetupForTesting = setOf(
            // Black
            Square(
                piece = King(Black, hasMoved = true),
                coordinates = Coordinates(col = 3, row = 0)
            ),

            Square(
                piece = Pawn(Black, hasMoved = false),
                coordinates = Coordinates(col = 0, row = 1)
            ),

            // White
            Square(
                piece = Queen(White, hasMoved = true),
                coordinates = Coordinates(col = 6, row = 1)
            ),
            Square(
                piece = Pawn(White, hasMoved = true),
                coordinates = Coordinates(col = 7, row = 1)
            ),
            Square(
                piece = King(White, hasMoved = true),
                coordinates = Coordinates(col = 3, row = 2)
            ),
            Square(
                piece = Bishop(White, hasMoved = true),
                coordinates = Coordinates(col = 0, row = 4)
            ),
        )

        val neighborOffsets = setOf(
            // Offsets are Pair(col, row)
            // Row above
            Pair(-1, -1), Pair(0, -1), Pair(1, -1),

            // Same row
            Pair(-1, 0), Pair(1, 0),

            // Row below
            Pair(-1, 1), Pair(0, 1), Pair(1, 1),
        )
    }
}

fun findValidMoves(
    board: Board,
    coordinates: Coordinates,
): Set<Coordinates> =
    board.getPieceOrNull(coordinates)!!
        .findValidToCoordinates(
            board = board,
            fromCoordinates = coordinates,
        )

fun findAllValidMovesByColor(board: Board, color: PieceColor): Set<Move> =
    board.squareMap
        .values
        .filter { square ->
            square.piece?.color == color
        }
        .flatMap { square ->
            findValidMoves(
                board = board,
                coordinates = square.coordinates,
            ).map { toCoordinates ->
                Move(
                    from = square.coordinates,
                    to = toCoordinates
                )
            }
        }
        .toSet()

fun squareCanBeAttacked(
    board: Board,
    attackingColor: PieceColor,
    coordinates: Coordinates,
): Boolean =
    isUnderDiagonalAttack(board, attackingColor, initialCoordinates = coordinates) ||
            isUnderRowOrColumnAttack(board, attackingColor, initialCoordinates = coordinates) ||
            isUnderKnightAttack(board, attackingColor, coordinates) ||
            isUnderPawnAttack(board, attackingColor, coordinates) ||
            isUnderKingAttack(board, attackingColor, coordinates)

// Check diagonals for attacks from enemy Bishops, or Queen
private fun isUnderDiagonalAttack(
    board: Board,
    enemyColor: PieceColor,
    initialCoordinates: Coordinates,
): Boolean {
    val possibleAttackingPieces = setOf(Bishop::class, Queen::class)
    return isUnderAttackFrom(
        board = board,
        enemyColor = enemyColor,
        initialCoordinates = initialCoordinates,
        allowedAttackingPieces = possibleAttackingPieces,
        colDelta = -1,
        rowDelta = -1
    ) ||
            isUnderAttackFrom(
                board = board,
                enemyColor = enemyColor,
                initialCoordinates = initialCoordinates,
                allowedAttackingPieces = possibleAttackingPieces,
                colDelta = 1,
                rowDelta = -1
            ) ||
            isUnderAttackFrom(
                board = board,
                enemyColor = enemyColor,
                initialCoordinates = initialCoordinates,
                allowedAttackingPieces = possibleAttackingPieces,
                colDelta = -1,
                rowDelta = 1
            ) ||
            isUnderAttackFrom(
                board = board,
                enemyColor = enemyColor,
                initialCoordinates = initialCoordinates,
                allowedAttackingPieces = possibleAttackingPieces,
                colDelta = 1,
                rowDelta = 1
            )
}

// Check for row or column attacks from enemy Rooks, or Queen
private fun isUnderRowOrColumnAttack(
    board: Board,
    enemyColor: PieceColor,
    initialCoordinates: Coordinates,
): Boolean {
    val possiblettackingPieces = setOf(Rook::class, Queen::class)
    return isUnderAttackFrom(
        board = board,
        enemyColor = enemyColor,
        initialCoordinates = initialCoordinates,
        allowedAttackingPieces = possiblettackingPieces,
        colDelta = -1,
        rowDelta = 0
    ) ||
            isUnderAttackFrom(
                board = board,
                enemyColor = enemyColor,
                initialCoordinates = initialCoordinates,
                allowedAttackingPieces = possiblettackingPieces,
                colDelta = 1,
                rowDelta = 0
            ) ||
            isUnderAttackFrom(
                board = board,
                enemyColor = enemyColor,
                initialCoordinates = initialCoordinates,
                allowedAttackingPieces = possiblettackingPieces,
                colDelta = 0,
                rowDelta = -1
            ) ||
            isUnderAttackFrom(
                board = board,
                enemyColor = enemyColor,
                initialCoordinates = initialCoordinates,
                allowedAttackingPieces = possiblettackingPieces,
                colDelta = 0,
                rowDelta = 1
            )
}

// Squares can be under attack from pieces that are pinned.
// That's because a king can't move into check, even if the "checking" piece is pinned.
private fun isUnderAttackFrom(
    board: Board,
    enemyColor: PieceColor,
    initialCoordinates: Coordinates,
    allowedAttackingPieces: Set<KClass<out Piece>>,
    colDelta: Int,
    rowDelta: Int,
): Boolean {
    val firstPiece = findFirstPiece(
        board = board,
        initialCoordinates = initialCoordinates,
        colDelta = colDelta,
        rowDelta = rowDelta
    )
    return firstPiece != null
            && (firstPiece::class in allowedAttackingPieces
            && firstPiece.color == enemyColor)
}

// Search for first piece in a given direction starting from the initial coordinates.
// When a piece is on the square at initialCoordinates it is not taken into account.
private fun findFirstPiece(
    board: Board,
    initialCoordinates: Coordinates,
    colDelta: Int,
    rowDelta: Int,
): Piece? {
    require(colDelta != 0 || rowDelta != 0)
    var col = initialCoordinates.col + colDelta
    var row = initialCoordinates.row + rowDelta
    while (true) {
        val square = board.getSquareOrNull(Coordinates(col = col, row = row))
        // Reached the end of the board?
        square ?: return null
        // Found a piece
        if (square.piece != null) {
            return square.piece
        }
        col += colDelta
        row += rowDelta
    }
}

private val knightOffsets = setOf(
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

// Check for attacks from enemy Knights
private fun isUnderKnightAttack(
    board: Board,
    enemyColor: PieceColor,
    originCoordinates: Coordinates,
): Boolean = findPieceByOffsets(
    board = board,
    originCoordinates = originCoordinates,
    piece = Knight(enemyColor),
    offsets = knightOffsets
)

private fun findPieceByOffsets(
    board: Board,
    originCoordinates: Coordinates,
    piece: Piece,
    offsets: Set<Pair<Int, Int>>,
): Boolean {
    return offsets.any { offset ->
        val col = originCoordinates.col + offset.first
        val row = originCoordinates.row + offset.second
        val otherPiece = board.getPieceOrNull(Coordinates(col = col, row = row))
        piece.isSameColorAndTypeAs(otherPiece)
    }
}

// Check for attacks from enemy Pawns
//
// Offsets are Pair(col, row)
private val whitePawnOffsets = setOf(Pair(-1, 1), Pair(1, 1))
private val blackPawnOffsets = setOf(Pair(1, -1), Pair(-1, -1))

// Pawns can only attack forwards diagonally.
private fun isUnderPawnAttack(
    board: Board,
    enemyColor: PieceColor,
    coordinates: Coordinates,
): Boolean {
    val offsets = if (enemyColor == White) whitePawnOffsets else blackPawnOffsets
    return findPieceByOffsets(
        board = board,
        originCoordinates = coordinates,
        piece = Pawn(enemyColor, twoSquareAdvanceOnPreviousMove = true),
        offsets = offsets
    )
}

// Check for attacks from enemy King
private fun isUnderKingAttack(
    board: Board,
    enemyColor: PieceColor,
    coordinates: Coordinates,
): Boolean = findPieceByOffsets(
    board = board,
    originCoordinates = coordinates,
    piece = King(enemyColor),
    offsets = Board.neighborOffsets
)

fun isCheckmate(board: Board, color: PieceColor): Boolean {
    val kingSquare = board.getKingSquare(color)
    val king = kingSquare.piece as King
    val kingNotInCheck = !king.isInCheck(
        board = board,
        kingCoordinates = kingSquare.coordinates
    )
    if (kingNotInCheck) return false

    // TODO remove the potential for recursive calls by removing the call to findValidMoves() and
    //  replacing it with calls to squareCanBeAttacked().
    val kingsValidMoves = findValidMoves(
        board = board,
        coordinates = kingSquare.coordinates,
    )
    return kingsValidMoves.isEmpty()
}

fun isStalemate(board: Board, color: PieceColor): Boolean {
    val kingSquare = board.getKingSquare(color)
    val king = kingSquare.piece as King
    val kingInCheck = king.isInCheck(
        board = board,
        kingCoordinates = kingSquare.coordinates
    )
    if (kingInCheck) return false

    val atLeastOneValidMove = board.squareMap
        .values
        .filter { square ->
            square.piece?.color == color
        }
        .any { square ->
            findValidMoves(
                board = board,
                coordinates = square.coordinates,
            ).isNotEmpty()
        }
    return !atLeastOneValidMove
}
