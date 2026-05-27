package com.example.SuaraRasa_A209008.gamescreens // Pastikan package ini sesuai

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.SuaraRasa_A209008.R // Pastikan R ini mengarah ke resource SuaraRasa
import com.example.SuaraRasa_A209008.navigation.AppDestinations // Path navigasi Anda
// Jika AppTopBar dan AppBottomNavigationBar ada di file lain, sesuaikan path importnya
// import com.example.suararasa.ui.components.AppTopBar
// import com.example.suararasa.ui.components.AppBottomNavigationBar
import kotlinx.coroutines.delay
import kotlin.random.Random

// Data class untuk Bubble
data class Bubble(
    val id: Int,
    val position: Offset,
    val radius: Float,
    val bubbleType: Int, // 1 = normal, 2 = rare (blue), 3 = special
    val speed: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BubblePopScreen(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            // AppTopBar(
            // navController = navController,
            // title = "Bubble Pop Game",
            // currentRoute = currentRoute,
            // showBackButton = true // Selalu tampilkan tombol kembali untuk layar game
            // )
            TopAppBar(
                title = { Text("Bubble Pop Game", fontWeight = FontWeight.Bold) },
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
            AppBottomNavigationBar(navController = navController) // Gunakan BottomNav yang konsisten
        }
    ) { innerPadding ->
        // Panggil Composable inti game di sini, berikan padding
        BubblePopGameContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Terapkan innerPadding dari Scaffold
        )
    }
}

@Composable
fun BubblePopGameContent(modifier: Modifier = Modifier) {
    var bubbles by remember { mutableStateOf(listOf<Bubble>()) }
    var score by remember { mutableStateOf(0) }
    var gameTime by remember { mutableStateOf(60) }
    var isGameOver by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val prefs = remember { context.getSharedPreferences("bubble_game_prefs_suararasa", Context.MODE_PRIVATE) }
    var highScore by remember {
        mutableStateOf(prefs.getInt("HIGH_SCORE", 0))
    }

    val backgroundMusicPlayer = remember {
        MediaPlayer.create(context, R.raw.background_music).apply {
            isLooping = true
            setVolume(0.3f, 0.3f)
        }
    }

    val soundPool = remember {
        SoundPool.Builder().setMaxStreams(5).build()
    }
    val popSoundId = remember { soundPool.load(context, R.raw.pop, 1) }
    val rarePopSoundId = remember { soundPool.load(context, R.raw.rare_pop, 1) }


    val gameOverSoundPlayer = remember {
        MediaPlayer.create(context, R.raw.game_over)
    }

    DisposableEffect(Unit) {
        if (!isGameOver && gameTime > 0) {
            backgroundMusicPlayer.start()
        }
        onDispose {
            soundPool.release()
            backgroundMusicPlayer.stop()
            backgroundMusicPlayer.release()
            gameOverSoundPlayer.release()
        }
    }

    // Game timer
    LaunchedEffect(isGameOver, gameTime) {
        if (!isGameOver && gameTime > 0) {
            if (!backgroundMusicPlayer.isPlaying) {
                backgroundMusicPlayer.start()
            }
            delay(1000)
            gameTime--
        } else if (gameTime == 0 && !isGameOver) {
            isGameOver = true
            backgroundMusicPlayer.pause()
            gameOverSoundPlayer.start()
            if (score > highScore) {
                highScore = score
                prefs.edit {
                    putInt("HIGH_SCORE", highScore)
                }
            }
        }
    }

    // Bubble spawner
    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (true) {
                delay(Random.nextLong(200, 500))
                if (isGameOver) break

                val bubbleType = when (Random.nextInt(100)) {
                    in 0..75 -> 1  // Normal (green)
                    in 76..93 -> 2  // Rare (blue)
                    else -> 3       // Special (gold)
                }

                val radius = when (bubbleType) {
                    1 -> Random.nextInt(35, 60).toFloat()
                    2 -> Random.nextInt(30, 50).toFloat()
                    else -> Random.nextInt(25, 45).toFloat() // Special bubble
                }

                val screenWidth = 1000f
                val screenHeight = 1800f

                val newBubble = Bubble(
                    id = Random.nextInt(),
                    position = Offset(
                        x = Random.nextInt(radius.toInt(), (screenWidth - radius).toInt()).toFloat(),
                        y = screenHeight + radius
                    ),
                    radius = radius,
                    bubbleType = bubbleType,
                    speed = when (bubbleType) {
                        1 -> Random.nextFloat() * 2.5f + 1.5f
                        2 -> Random.nextFloat() * 3.5f + 2.5f
                        else -> Random.nextFloat() * 2.0f + 1.0f
                    }
                )
                bubbles = bubbles + newBubble
            }
        }
    }


    // Bubble movement
    LaunchedEffect(isGameOver) {
        if (!isGameOver) {
            while (true) {
                delay(16)
                if (isGameOver) break

                bubbles = bubbles.map {
                    it.copy(position = it.position.copy(y = it.position.y - it.speed))
                }.filter { it.position.y + it.radius > 0f }
            }
        }
    }

    Box(
        modifier = modifier
            .background(Color(0xFF81D4FA))
    ) {
        // UI Skor dan Waktu
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Score: $score",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Time: $gameTime",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }


        Text(
            text = "High Score: $highScore",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 40.dp)
        )


        // Render bubbles
        bubbles.forEach { bubble ->
            Box(
                modifier = Modifier
                    .size((bubble.radius * 2).dp)
                    .offset {
                        IntOffset(
                            (bubble.position.x - bubble.radius).toInt(),
                            (bubble.position.y - bubble.radius).toInt()
                        )
                    }
                    .pointerInput(bubble.id) {
                        detectTapGestures {
                            if (!isGameOver) {
                                bubbles = bubbles.filterNot { it.id == bubble.id }
                                score += when (bubble.bubbleType) {
                                    1 -> 1  // Normal
                                    2 -> 5  // Rare
                                    else -> 10 // Special
                                }

                                // Mainkan suara pop
                                when (bubble.bubbleType) {
                                    2 -> soundPool.play(rarePopSoundId, 0.8f, 0.8f, 1, 0, 1f)
                                    // 3 -> soundPool.play(specialPopSoundId, 1f, 1f, 1, 0, 1f)
                                    else -> soundPool.play(popSoundId, 1f, 1f, 1, 0, 1f)
                                }
                            }
                        }
                    }
            ) {
                Image(
                    painter = painterResource(
                        id = when (bubble.bubbleType) {
                            1 -> R.drawable.bubble_green // Pastikan resource ini ada
                            2 -> R.drawable.bubble_blue  // Pastikan resource ini ada
                            else -> R.drawable.bubble_gold // Pastikan resource ini ada
                        }
                    ),
                    contentDescription = "Bubble",
                    modifier = Modifier.fillMaxSize()
                )

                // Teks skor di dalam bubble spesial
                if (bubble.bubbleType != 1) { // Hanya untuk rare dan special
                    val pointValue = when (bubble.bubbleType) {
                        2 -> "+5"
                        else -> "+10"
                    }
                    Text(
                        text = pointValue,
                        color = Color.White,
                        fontSize = (bubble.radius * 0.4).sp, // Ukuran teks disesuaikan dengan radius bubble
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        if (isGameOver) {
            GameEndScreen( // Mengganti nama agar tidak konflik dengan GameOverScreen lain jika ada
                score = score,
                highScore = highScore,
                onRestart = {
                    score = 0
                    gameTime = 60 // Reset waktu game
                    isGameOver = false
                    bubbles = emptyList() // Hapus semua bubble
                    // Musik akan dimulai otomatis oleh LaunchedEffect(isGameOver, gameTime)
                }
            )
        }
    }
}

@Composable
fun GameEndScreen(score: Int, highScore: Int, onRestart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f)) // Latar lebih gelap
            .clickable(onClick = onRestart), // Restart saat diklik di mana saja
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Game Over!",
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold, // Lebih tebal
                color = Color.Red // Warna merah untuk game over
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Your Score: $score",
                fontSize = 32.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Highest Score: $highScore",
                fontSize = 28.sp,
                color = Color.Yellow, // Warna kuning untuk high score
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(48.dp)) // Jarak lebih besar sebelum tombol
            Text(
                text = "Tap to play again",
                fontSize = 22.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// Composable untuk BottomNavigationBar (seperti yang telah kita definisikan sebelumnya)
// Pastikan ini ada di scope yang benar atau diimpor
@Composable
fun AppBottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        NavigationBarItem(
            icon = { Icon(painterResource(R.drawable.ic_game), contentDescription = "Game") },
            selected = currentRoute == AppDestinations.GAME_HOME_ROUTE,
            onClick = {
                navController.navigate(AppDestinations.GAME_HOME_ROUTE) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            label = { Text("Game") }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(R.drawable.ic_capsule), contentDescription = "Capsule") },
            selected = currentRoute == AppDestinations.CAPSULE_ROUTE,
            onClick = {
                navController.navigate(AppDestinations.CAPSULE_ROUTE) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            label = { Text("Capsule") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") }, // Menggunakan Default.Home
            selected = currentRoute == AppDestinations.HOME_ROUTE,
            onClick = {
                navController.navigate(AppDestinations.HOME_ROUTE) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            label = { Text("Home") }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(R.drawable.ic_music), contentDescription = "Music") },
            selected = currentRoute == AppDestinations.MUSIC_ROUTE,
            onClick = {
                navController.navigate(AppDestinations.MUSIC_ROUTE) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            label = { Text("Music") }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(R.drawable.ic_mood), contentDescription = "Mood Tracker") },
            selected = currentRoute == AppDestinations.MOOD_TRACKER_ROUTE,
            onClick = {
                navController.navigate(AppDestinations.MOOD_TRACKER_ROUTE) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            label = { Text("Mood") }
        )
    }
}