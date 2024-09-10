package com.franklinharper.chess

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.SpaceEvenly
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.rememberModalBottomSheetState
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
import chess.composeapp.generated.resources.possible_move
import chess.composeapp.generated.resources.white_bishop
import chess.composeapp.generated.resources.white_king
import chess.composeapp.generated.resources.white_knight
import chess.composeapp.generated.resources.white_pawn
import chess.composeapp.generated.resources.white_queen
import chess.composeapp.generated.resources.white_rook
import com.franklinharper.chess.Piece.*
import com.franklinharper.chess.PieceColor.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        // TODO use rememberSaveable so that the game state is preserved across
        //  configuration changes and process death.
        //
        // A custom Saver implementation is necessary to use rememberSaveable
        // Example:
        // data class User(val name: String, val age: Int)
        //
        // val userSaver = Saver<User, Map<String, Any>>(
        //    save = { user -> mapOf("name" to user.name, "age" to user.age) },
        //    restore = { savedMap ->
        //        User(
        //            name = savedMap["name"] as String,
        //            age = savedMap["age"] as Int
        //        )
        //    }
        //)
        val viewModel = remember { createViewModel() }
        val state by viewModel.state.collectAsState()
        println("state: $state")
        var expanded by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Chess") },
                    actions = {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More actions")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    expanded = false
                                    viewModel.onNewGameClick()
                                }
                            ) {
                                Text("New Game")
                            }
                            DropdownMenuItem(
                                onClick = {
                                    expanded = false
                                    viewModel.onSetEndGamePositionClick()
                                }
                            ) {
                                Text("Set end Game position")
                            }
                        }
                    }
                )
            }
        ) {
            ChessBoard(state.board, viewModel)
        }
    }
}

val darkSquareBackground = Color(0xFF2AA835)
val lightSquareBackground = Color.LightGray

@Composable
private fun ChessBoard(
    board: Board,
    viewModel: ViewController,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(board.getStatus().toString())
        for (rowIndex in 0..7) {
            Row {
                for (colIndex in 0..7) {
                    val isLightSquare = (rowIndex + colIndex) % 2 == 0
                    val square = board.getSquare(colIndex, rowIndex)
                    val backgroundColor = if (isLightSquare) {
                        when {
                            square.isSelected -> Color.Red
                            else -> lightSquareBackground
                        }
                    } else {
                        when {
                            square.isSelected -> Color.Red
                            else -> darkSquareBackground
                        }
                    }
                    Box(
                        modifier = Modifier
                            // TODO calculate the size based on the screen size
                            .size(40.dp) // Size each square
                            .background(backgroundColor)
                            .clickable {
                                viewModel.onSquareClick(
                                    colIndex = colIndex,
                                    rowIndex = rowIndex,
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        square.piece?.let { piece ->
                            Image(
                                painter = painterResource(piece.image),
                                contentDescription = null
                            )
                        }
                        if (square.isValidMoveDestination) {
                            Image(
                                painter = painterResource(Res.drawable.possible_move),
                                contentDescription = null
                            )
                        }
                    }

                }
            }
        }
        val promotionSquare = board.findPromotionSquare()
        if (promotionSquare != null) {
            PromotionDialog(promotionSquare) { piece, square ->
                viewModel.onPromotionClick(piece, square)
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PromotionDialog(
    promotionSquare: Square,
    onPromotionClick: (Piece, Square) -> Unit,
) {
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )

    val coroutineScope = rememberCoroutineScope()

    coroutineScope.launch { modalBottomSheetState.show() }

    val pieceColor = promotionSquare.piece!!.color

    ModalBottomSheetLayout(
        sheetState = modalBottomSheetState,
        sheetContent = {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(horizontalArrangement = SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    PieceButton(Queen(pieceColor), promotionSquare, onPromotionClick, coroutineScope, modalBottomSheetState)
                    PieceButton(Knight(pieceColor), promotionSquare, onPromotionClick, coroutineScope, modalBottomSheetState)
                }
                Spacer(Modifier.size(16.dp))
                Row(horizontalArrangement = SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    PieceButton(Rook(pieceColor), promotionSquare, onPromotionClick, coroutineScope, modalBottomSheetState)
                    PieceButton(Bishop(pieceColor), promotionSquare, onPromotionClick, coroutineScope, modalBottomSheetState)
                }
            }
        }
    )
    {
        // Nothing is displayed when this sheet is hidden.
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PieceButton(
    piece: Piece,
    square: Square,
    onPromotionClick: (Piece, Square) -> Unit,
    coroutineScope: CoroutineScope,
    modalBottomSheetState: ModalBottomSheetState,
) {
    Button(
        onClick = {
            coroutineScope.launch { modalBottomSheetState.hide() }
            onPromotionClick(piece, square)
        }
    ) {
        Image(
            painter = painterResource(piece.image),
            contentDescription = null
        )
    }
}

private val Piece.image: DrawableResource
    get() {
        return when (this) {
            is Bishop -> if (color == White) Res.drawable.white_bishop else Res.drawable.black_bishop
            is King -> if (color == White) Res.drawable.white_king else Res.drawable.black_king
            is Knight -> if (color == White) Res.drawable.white_knight else Res.drawable.black_knight
            is Pawn -> if (color == White) Res.drawable.white_pawn else Res.drawable.black_pawn
            is Queen -> if (color == White) Res.drawable.white_queen else Res.drawable.black_queen
            is Rook -> if (color == White) Res.drawable.white_rook else Res.drawable.black_rook
        }
    }
