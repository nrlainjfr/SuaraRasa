package com.example.SuaraRasa_A209008.gamescreens // Pastikan package ini sesuai

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.SuaraRasa_A209008.R // Pastikan R ini mengarah ke resource SuaraRasa
// Jika AppTopBar dan AppBottomNavigationBar ada di file lain, sesuaikan path importnya
// import com.example.suararasa.ui.components.AppTopBar
// import com.example.suararasa.ui.components.AppBottomNavigationBar
import com.example.SuaraRasa_A209008.GameState
import com.example.SuaraRasa_A209008.Card

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchCardScreen( // Ganti nama agar lebih deskriptif sebagai layar
    navController: NavController, // Tambahkan NavController
    modifier: Modifier = Modifier, // Modifier tetap ada jika dibutuhkan
    gridSize: Int = 4
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Match Card Game", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            // Panggil Composable BottomNavigationBar Anda di sini
            AppBottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        // Konten inti game Anda (yang sebelumnya MemoryGameScreen)
        // akan menggunakan innerPadding dari Scaffold
        val coroutineScope = rememberCoroutineScope()
        // GameState di-remember di sini, di dalam lingkup Scaffold
        val gameState = remember(gridSize, coroutineScope) { GameState(gridSize, coroutineScope = coroutineScope) }

        if (gameState.gameComplete) {
            // Layar selesai game, juga perlu padding dari Scaffold
            GameCompleteScreen(
                moves = gameState.moves,
                onPlayAgain = { gameState.startNewGame() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding) // Terapkan innerPadding
            )
        } else {
            // Kolom utama game, juga perlu padding dari Scaffold
            Column(
                modifier = modifier // Gunakan modifier yang diteruskan ke MatchCardGameScreen
                    .fillMaxSize()
                    .padding(innerPadding) // Terapkan innerPadding di sini
                    .padding(horizontal = 8.dp), // Padding tambahan untuk konten jika perlu
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Game stats
                Text(
                    text = "Moves: ${gameState.moves}",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp) // Sesuaikan padding
                )

                // Game grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridSize),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Agar grid mengambil ruang yang tersedia
                        .aspectRatio(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Berikan key untuk performa yang lebih baik pada LazyVerticalGrid
                    items(gameState.cards, key = { it.id }) { card ->
                        CardItem(
                            card = card,
                            // onClick di CardItem tidak perlu suspend lagi karena GameState.onCardClick
                            // sudah menangani coroutine scope.
                            onClick = { gameState.onCardClick(card) }
                        )
                    }
                }

                // Restart button
                Button(
                    onClick = { gameState.startNewGame() },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Restart Game")
                }
            }
        }
    }
}

@Composable
fun CardItem(
    card: Card,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                enabled = !card.isMatched && !card.isFaceUp,
                onClick = onClick
            )
            .background(
                if (card.isFaceUp || card.isMatched) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.primary
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (card.isFaceUp || card.isMatched) {
            val imageResId = when (card.value) {
                1 -> R.drawable.ic_sleepy
                2 -> R.drawable.ic_sad
                3 -> R.drawable.ic_happy
                4 -> R.drawable.ic_chill

                5 -> R.drawable.ic_happyshark
                6 -> R.drawable.ic_sadshark
                7 -> R.drawable.ic_bored
                8 -> R.drawable.ic_sulking
                else -> R.drawable.ic_default // fallback
            }

            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Card image for value ${card.value}",
                modifier = Modifier.fillMaxSize(0.80f)
            )
            // Text(
            //     text = card.value.toString(),
            //     fontSize = 30.sp,
            //     color = Color.Black.copy(alpha = 0.5f),
            //     fontWeight = FontWeight.Bold
            // )
        }
    }
}

@Composable
fun GameCompleteScreen(
    moves: Int,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Congratulations!",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "You completed the game in $moves moves",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        Button(
            onClick = onPlayAgain,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Play Again")
        }
    }
}