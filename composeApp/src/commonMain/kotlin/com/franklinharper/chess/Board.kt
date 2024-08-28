package com.franklinharper.chess

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
    val moveColor: PieceColor,
) {
    constructor(squareSet: Set<Square>, moveColor: PieceColor = White) :
            this(
                squareMap = squareSet.associateBy { square -> square.coordinates },
                moveColor = moveColor
            )

    constructor() :
            this(
                moveColor = White,
                squareSet = setOf(
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
            // Move a piece
            square.isValidMove -> {
                val fromSquare = getSelectedSquare()!!
                copyAndDeselectAllSquares()
                    .copyAndUpdateValidMoves(emptySet())
                    .move(from = fromSquare.coordinates, to = square.coordinates)
            }

            // Select a square
            square.isNotSelected && square.piece != null && square.piece.color == moveColor -> {
                val validMoves = findValidMoves(
                    board = this,
                    coordinates = square.coordinates
                )
                println("2 squareMap(4,7)=${squareMap[Coordinates(col = 4, row = 7)]}")
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
        val mutableMap = squareMap.toMutableMap()
        val fromPiece = mutableMap[from]?.piece!!

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
                val twoSquareAdvance = abs(to.row - from.row) == 2
                fromPiece.copy(
                    hasMoved = true,
                    twoSquareAdvanceOnPreviousMove = twoSquareAdvance
                )
            }

            else -> {
                fromPiece.copy(hasMoved = true)
            }
        }
        mutableMap.remove(key = from)
        mutableMap[to] = Square(piece = movedPiece, coordinates = to)
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
        return Board(squareMap = mutableMap, moveColor = moveColor.enemyColor())
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
            if (square.isValidMove) mutableMap[square.coordinates] =
                square.copy(isValidMove = false)

        }
        for (coordinates in validMoves) {
            mutableMap[coordinates] = getSquare(coordinates).copy(isValidMove = true)
        }
        return Board(
            squareMap = mutableMap,
            moveColor = moveColor
        )
    }

    private fun copyAndDeselectAllSquares(): Board {
        val mutableMap = squareMap.toMutableMap()
        for (square in mutableMap.values) {
            if (square.isSelected) mutableMap[square.coordinates] = square.copy(isSelected = false)
        }
        return Board(squareMap = mutableMap, moveColor = moveColor)
    }

    private fun copyAndReplaceSquare(newSquare: Square): Board {
        val mutableMap = squareMap.toMutableMap()
        mutableMap[newSquare.coordinates] = newSquare
        return Board(
            squareMap = mutableMap,
            moveColor = moveColor
        )
    }

    override fun toString(): String {
        return "moveColor=$moveColor, squares=${squareMap.values})"
    }

    fun isCheckmate(color: PieceColor): Boolean {
        val kingSquare = getKingSquare(color)
        val kingsValidMoves = findValidMoves(
            board = this,
            coordinates = kingSquare.coordinates
        )
        val king = kingSquare.piece as King
        val kingInCheck = king.isInCheck(
            board = this,
            originCoordinates = kingSquare.coordinates
        )
        return kingInCheck && kingsValidMoves.isEmpty()
    }

    fun isStalemate(color: PieceColor): Boolean {
        val kingSquare = getKingSquare(color)
        val king = kingSquare.piece as King
        val kingNotInCheck = !king.isInCheck(
            board = this,
            originCoordinates = kingSquare.coordinates
        )
        val validMoves = findAllValidMovesByColor(
            board = this,
            color = color
        )
        return kingNotInCheck && validMoves.isEmpty()
    }

    companion object {
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

fun findValidMoves(board: Board, coordinates: Coordinates): Set<Coordinates> =
    board.getPieceOrNull(coordinates)!!
        .findMoveDestinationCoordinates(board, coordinates)

fun findAllValidMovesByColor(board: Board, color: PieceColor): Set<Move> =
    board.squareMap
        .filter { (coordinates, square) ->
            square.piece?.color == color
        }
        .values
        .flatMap { square ->
            findValidMoves(
                board,
                square.coordinates
            ).map { toCoordinates ->
                Move(
                    from = square.coordinates,
                    to = toCoordinates
                )
            }
        }
        .toSet()

//    //    private val squares: Array<Array<Square>> = arrayOf(
//    //        arrayOf(
//    //            Square(Piece.Rook(PieceColor.Black), Coordinates(col = 0, row = 0)),
//    //            Square(Piece.Knight(PieceColor.Black), Coordinates(col = 1, row = 0)),
//    //            Square(Piece.Bishop(PieceColor.Black),  Coordinates(col = 2, row = 0)),
//    //            Square(Piece.King(PieceColor.Black),  Coordinates(col = 3, row = 0)),
//    //            Square(Piece.Queen(PieceColor.Black),  Coordinates(col = 4, row = 0)),
//    //            Square(Piece.Bishop(PieceColor.Black),  Coordinates(col = 5, row = 0)),
//    //            Square(Piece.Knight(PieceColor.Black),  Coordinates(col = 6, row = 0)),
//    //            Square(Piece.Rook(PieceColor.Black),  Coordinates(col = 7, row = 0)),
//    //        ),
//    //        arrayOf(
//    //            Square(Piece.Pawn(PieceColor.Black)),
//    //            Square(Piece.Pawn(PieceColor.Black)),
//    //            Square(Piece.Pawn(PieceColor.Black)),
//    //            Square(Piece.Pawn(PieceColor.Black)),
//    //            Square(Piece.Pawn(PieceColor.Black)),
//    //            Square(Piece.Pawn(PieceColor.Black)),
//    //            Square(Piece.Pawn(PieceColor.Black)),
//    //            Square(Piece.Pawn(PieceColor.Black)),
//    //        ),
//    //        arrayOf(
//    //            Square(piece = null), Square(piece = null), Square(piece = null), Square(piece = null),
//    //            Square(piece = null), Square(piece = null), Square(piece = null), Square(piece = null),
//    //        ),
//    //        arrayOf(
//    //            Square(piece = null), Square(piece = null), Square(piece = null), Square(piece = null),
//    //            Square(piece = null), Square(piece = null), Square(piece = null), Square(piece = null),
//    //        ),
//    //        arrayOf(
//    //            Square(piece = null), Square(piece = null), Square(piece = null), Square(piece = null),
//    //            Square(piece = null), Square(piece = null), Square(piece = null), Square(piece = null),
//    //        ),
//    //        arrayOf(
//    //            Square(piece = null), Square(piece = null), Square(piece = null), Square(piece = null),
//    //            Square(piece = null), Square(piece = null), Square(piece = null), Square(piece = null),
//    //        ),
//    //        arrayOf(
//    //            Square(Piece.Pawn(PieceColor.White)),
//    //            Square(Piece.Pawn(PieceColor.White)),
//    //            Square(Piece.Pawn(PieceColor.White)),
//    //            Square(Piece.Pawn(PieceColor.White)),
//    //            Square(Piece.Pawn(PieceColor.White)),
//    //            Square(Piece.Pawn(PieceColor.White)),
//    //            Square(Piece.Pawn(PieceColor.White)),
//    //            Square(Piece.Pawn(PieceColor.White)),
//    //        ),
//    //        arrayOf(
//    //            Square(Piece.Rook(PieceColor.White)),
//    //            Square(Piece.Knight(PieceColor.White)),
//    //            Square(Piece.Bishop(PieceColor.White)),
//    //            Square(Piece.King(PieceColor.White)),
//    //            Square(Piece.Queen(PieceColor.White)),
//    //            Square(Piece.Bishop(PieceColor.White)),
//    //            Square(Piece.Knight(PieceColor.White)),
//    //            Square(Piece.Rook(PieceColor.White)),
//    //        ),
//    //    )
//
//    fun findAttacks(color: PieceColor): Set<Coordinates> {
//        val attackedSquares = mutableSetOf<Coordinates>()
//        for (row in 0..7) {
//            for (col in 0..7) {
//                val piece = squares[col][row].piece
//                if (piece != null && piece.color == color) {
//                    attackedSquares.addAll(piece.findMoveDestinationCoordinates(this, Coordinates(col, row)))
//                }
//            }
//        }
//        return attackedSquares
//    }
//
//    fun getSquareOrNull(coordinates: Coordinates) =
//        squares.getOrNull(coordinates.col)?.getOrNull(coordinates.row)
//
//    fun getSquare(coordinates: Coordinates) =
//        squares[coordinates.col][coordinates.row]
//
//    fun getPiece(coordinates: Coordinates): Piece? =
//        getSquareOrNull(coordinates)?.piece
//
//
//    operator fun get(col: Int, row: Int) = squares[col][row]
//
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
        enemyColor,
        initialCoordinates,
        possibleAttackingPieces,
        colDelta = -1,
        rowDelta = -1
    ) ||
            isUnderAttackFrom(
                board = board,
                enemyColor,
                initialCoordinates,
                possibleAttackingPieces,
                colDelta = 1,
                rowDelta = -1
            ) ||
            isUnderAttackFrom(
                board = board,
                enemyColor,
                initialCoordinates,
                possibleAttackingPieces,
                colDelta = -1,
                rowDelta = 1
            ) ||
            isUnderAttackFrom(
                board = board,
                enemyColor,
                initialCoordinates,
                possibleAttackingPieces,
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
        board,
        enemyColor,
        initialCoordinates,
        possiblettackingPieces,
        colDelta = -1,
        rowDelta = 0
    ) ||
            isUnderAttackFrom(
                board,
                enemyColor,
                initialCoordinates,
                possiblettackingPieces,
                colDelta = 1,
                rowDelta = 0
            ) ||
            isUnderAttackFrom(
                board,
                enemyColor,
                initialCoordinates,
                possiblettackingPieces,
                colDelta = 0,
                rowDelta = -1
            ) ||
            isUnderAttackFrom(
                board,
                enemyColor,
                initialCoordinates,
                possiblettackingPieces,
                colDelta = 0,
                rowDelta = 1
            )
}

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
