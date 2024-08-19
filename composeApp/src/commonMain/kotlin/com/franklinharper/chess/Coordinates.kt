package com.franklinharper.chess

//{
// It might be cool to be able to use the Coordinate value class instead of Int.
// But there were some issues with that.
// TODO use the Coordinate value class instead of Int.
//   val col = Coordinate(col)
//   val row = Coordinate(row)
//   val col = col
//   val row = row
//}

data class Coordinates (
   // Passing ints to this constructor allows us to avoid
   // calling the Coordinate constructor every time we
   // instantiate a new Coordinates object.
   // I.e we can write
   //
   //    Coordinates(1, 2)
   //
   //    instead of the more verbose
   //
   //    Coordinates(Coordinate(1), Coordinate(2))
   val col: Int,
   val row: Int,
) {
    // Sometimes Coordinates are created with col or row values that are out of bounds.
    // Example code from Piece.kt
    //    val offsetCoordinates = Coordinates(sourceCoordinates.col + it.first, sourceCoordinates.col + it.second)
    //    board.getSquareOrNull(offsetCoordinates)
    //
    // Because of this requiring valid col and row values would cause an exception in some cases.
    //   init {
    //       require(col in 0..7 && row in 0..7)
    //   }
}

