package com.example.SuaraRasa_A209008.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue // Diperlukan untuk currentBackStackEntryAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState // Diperlukan
import com.example.SuaraRasa_A209008.R
import com.example.SuaraRasa_A209008.navigation.AppDestinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mini Games",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null &&
                        currentRoute != AppDestinations.HOME_ROUTE) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            AppBottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),  // Gabungkan kedua-dua modifier dalam satu chain
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Choose Your Game",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Game Cards Column
                Column(
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    // Bubble Pop Game
                    GameCard(
                        title = "Bubble Pop",
                        imageResId = R.drawable.bubble_pop,
                        description = "Pop colorful bubbles as fast as you can! Test your reflexes and see how many you can pop in 60 seconds.",
                        onClick = { navController.navigate(AppDestinations.BUBBLE_POP_ROUTE) }
                    )

                    // Match Card Game
                    GameCard(
                        title = "Match Card",
                        imageResId = R.drawable.match_card,
                        description = "Find all matching pairs of cards. Improve your memory with this classic matching game.",
                        onClick = { navController.navigate(AppDestinations.MATCH_CARD_ROUTE) }
                    )
                }
            }
        }
    }
}

@Composable
fun GameCard(
    title: String,
    imageResId: Int,
    description: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f)
    )

    Column(
        modifier = Modifier
            .width(250.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Card with Image
        Card(
            modifier = Modifier
                .height(280.dp) // Made taller
                .fillMaxWidth()
                .graphicsLayer(scaleX = scale, scaleY = scale),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient overlay for better text visibility
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                ),
                                startY = 0.6f
                            )
                        )
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                )
            }
        }

        // Description with fixed height
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp, max = 100.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun AppBottomNavigationBar(navController: NavController) {
    // Implementasi BottomNavigationBar Anda akan ada di sini
    // Saya akan menyalin implementasi yang Anda berikan sebelumnya sebagai contoh
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
        // ... item navigasi lainnya (Capsule, Home, Music, Mood) ...
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
            icon = { Icon(androidx.compose.material.icons.Icons.Default.Home, contentDescription = "Home") },
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
            label = { Text("Soul Beats") }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(R.drawable.ic_mood), contentDescription = "Mood Tracker") },
            selected = currentRoute == AppDestinations.MOOD_TRACKER_ROUTE,
            onClick = {
                // Navigasi ke rute awal dari MoodApp (nested graph)
                navController.navigate(AppDestinations.MOOD_TRACKER_ROUTE) { // Ini akan menavigasi ke MoodAppDestinations.MOOD_ROOT_ROUTE
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            label = { Text("Mood") }
        )
    }
}