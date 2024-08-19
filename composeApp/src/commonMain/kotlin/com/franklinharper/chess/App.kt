package com.franklinharper.chess

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import chess.composeapp.generated.resources.Res
import chess.composeapp.generated.resources.black_bishop
import chess.composeapp.generated.resources.black_king
import chess.composeapp.generated.resources.black_knight
import chess.composeapp.generated.resources.black_pawn
import chess.composeapp.generated.resources.black_queen
import chess.composeapp.generated.resources.black_rook
import chess.composeapp.generated.resources.white_bishop
import chess.composeapp.generated.resources.white_king
import chess.composeapp.generated.resources.white_knight
import chess.composeapp.generated.resources.white_pawn
import chess.composeapp.generated.resources.white_queen
import chess.composeapp.generated.resources.white_rook
import com.franklinharper.chess.PieceColor.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val darkSquareBackground = Color(0xFF2AA835)
    val lightSquareBackground = Color.LightGray
    MaterialTheme {
        val viewModel = remember { createViewModel() }
        val state by viewModel.state.collectAsState()

//        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (rowIndex in 0..7) {
                    Row {
                        for (colIndex in 0..7) {
                            val square = state.getSquare(colIndex, rowIndex)
                            Box(
                                modifier = Modifier
                                    // TODO calculate the size based on the screen size
                                    .size(40.dp) // Size each square
                                    .background(if ((rowIndex + colIndex) % 2 == 0) lightSquareBackground else darkSquareBackground)
                                    .clickable { viewModel.onSquareClick(rowIndex, colIndex) },
                                contentAlignment = Alignment.Center
                            ) {
                                square.piece?.let { piece ->
                                    Image(
                                        painter = painterResource(piece.image),
                                        contentDescription = null
                                    )
                                }
                            }

                        }
                    }
                }
            }
//            AnimatedVisibility(showContent) {
//                val greeting = remember { Greeting().greet() }
//                Column(
//                    Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Image(painterResource(Res.drawable.compose_multiplatform), null)
//                    Text("Compose: $greeting")
//                }
//            }
//        }
    }
}

private val Piece.image: DrawableResource
    get() {
        return when (this) {
            is Piece.Bishop -> if (color == White) Res.drawable.white_bishop else Res.drawable.black_bishop
            is Piece.King -> if (color == White) Res.drawable.white_king else Res.drawable.black_king
            is Piece.Knight -> if (color == White) Res.drawable.white_knight else Res.drawable.black_knight
            is Piece.Pawn -> if (color == White) Res.drawable.white_pawn else Res.drawable.black_pawn
            is Piece.Queen -> if (color == White) Res.drawable.white_queen else Res.drawable.black_queen
            is Piece.Rook -> if (color == White) Res.drawable.white_rook else Res.drawable.black_rook
        }
    }
