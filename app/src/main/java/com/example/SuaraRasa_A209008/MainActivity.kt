package com.example.SuaraRasa_A209008

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.SuaraRasa_A209008.R
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.SuaraRasa_A209008.navigation.AppDestinations
import com.example.SuaraRasa_A209008.screens.GameScreen
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.SuaraRasa_A209008.screens.*
import androidx.navigation.NavController // Import NavController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.a209020Lab6.ui.mood.MoodEntryDestination
import com.example.a209020Lab6.ui.mood.MoodEntryScreen
import com.example.SuaraRasa_A209008.screen.home.skrinminda
import com.example.SuaraRasa_A209008.screen.sleep.SleepQuestionScreen
import com.example.SuaraRasa_A209008.screen.sleep.SleepScoreScreen
import com.example.SuaraRasa_A209008.screen.sleep.SleepScreen
import com.example.SuaraRasa_A209008.screen.stress.StressQuestionScreen
import com.example.SuaraRasa_A209008.screen.stress.StressScoreScreen
import com.example.SuaraRasa_A209008.screen.stress.StressScreen
import com.example.SuaraRasa_A209008.gamescreens.BubblePopScreen
import com.example.SuaraRasa_A209008.gamescreens.MatchCardScreen
import com.example.SuaraRasa_A209008.home.MoodAppHomeScreen
import com.example.SuaraRasa_A209008.item.MoodDetailsDestination
import com.example.SuaraRasa_A209008.item.MoodDetailsScreen
import com.example.SuaraRasa_A209008.item.MoodEditDestination
import com.example.SuaraRasa_A209008.item.MoodEditScreen
import com.example.SuaraRasa_A209008.ui.theme.EmergencyViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppNavigation() // Panggil fungsi navigasi utama Anda
            }
        }
    }
}

object MoodAppDestinations {
    const val MOOD_ROOT_ROUTE = "mood_app_root" // Rute induk untuk navigasi mood
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val emergencyViewModel: EmergencyViewModel = viewModel() // ✅ Tambah ini

    NavHost(navController = navController, startDestination = AppDestinations.HOME_ROUTE) {
        composable(AppDestinations.HOME_ROUTE) {
            HomeScreen(navController = navController)
        }
        composable(AppDestinations.GAME_HOME_ROUTE) {
            GameScreen(navController = navController)
        }
        navigation(
            startDestination = MoodEntryDestination.route, // <--- PERUBAHAN DI SINI
            route = MoodAppDestinations.MOOD_ROOT_ROUTE // Rute untuk keseluruhan MoodApp graph
            // Ini harus sama dengan AppDestinations.MOOD_TRACKER_ROUTE
        ) {
            composable(route = MoodEntryDestination.route) {
                // Gunakan alias jika ada konflik nama
                MoodAppHomeScreen(
                    navigateToMoodEntry = { navController.navigate(MoodEntryDestination.route) },
                    navigateToMoodUpdate = { moodId -> navController.navigate("${MoodDetailsDestination.route}/$moodId") },
                    navigateUp = { navController.popBackStack() }
                )
            }
            composable(route = MoodEntryDestination.route) {
                MoodEntryScreen( // Pastikan ini adalah composable yang benar dari MoodApp
                    navigateBack = { navController.popBackStack() },
                    onNavigateUp = { navController.navigateUp() }
                )
            }
            composable(
                route = MoodDetailsDestination.routeWithArgs,
                arguments = listOf(navArgument(MoodDetailsDestination.moodIdArg) {
                    type = NavType.IntType
                })
            ) {
                MoodDetailsScreen( // Pastikan ini adalah composable yang benar dari MoodApp
                    navigateToEditMood = { moodId ->
                        navController.navigate("${MoodEditDestination.route}/$moodId")
                    },
                    navigateBack = { navController.navigateUp() }
                )
            }
            composable(
                route = MoodEditDestination.routeWithArgs,
                arguments = listOf(navArgument(MoodEditDestination.moodIdArg) {
                    type = NavType.IntType
                })
            ) {
                MoodEditScreen( // Pastikan ini adalah composable yang benar dari MoodApp
                    navigateBack = { navController.popBackStack() },
                    onNavigateUp = { navController.navigateUp() }
                )
            }
        }
        composable(AppDestinations.BUBBLE_POP_ROUTE) {
            BubblePopScreen(navController = navController)
        }
        composable(AppDestinations.MATCH_CARD_ROUTE) {
            MatchCardScreen(navController = navController)
        }
        composable(AppDestinations.CAPSULE_ROUTE) {
            CapsuleScreen(navController = navController)
        }

        soulBeatsGraph(navController)

        // ✅ Hantar viewModel sekali ke emergencyGraph
        emergencyGraph(navController, emergencyViewModel)

        composable(AppDestinations.MENTAL_HEALTH_TEST_ROUTE) {
            skrinminda(navController = navController)
        }
        composable(AppDestinations.BREATHING_EXERCISES_ROUTE) {
            BreathingExercisesScreen(navController = navController)
        }
        composable(AppDestinations.QUOTES_ROUTE) {
            QuotesScreen(navController = navController)
        }
        composable(AppDestinations.AI_ASSISTANT_ROUTE) {
            AiAssistantScreen(navController = navController)
        }

        composable("stress") {
            StressScreen(navController = navController)
        }
        composable("StressQuestionScreen") {
            StressQuestionScreen(navController = navController)
        }
        composable("StressScoreScreen") {
            StressScoreScreen(navController = navController)
        }

        composable("sleep") {
            SleepScreen(navController = navController)
        }
        composable("SleepQuestionScreen") {
            SleepQuestionScreen(navController = navController)
        }
        composable("SleepScoreScreen") {
            SleepScoreScreen(navController = navController)
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: AdviceViewModel = viewModel()


//    var showMenu by remember { mutableStateOf(false) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }


    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        topBar = {
            TopAppBar(
                title = { Text("SuaraRasa", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                    if (currentRoute != AppDestinations.HOME_ROUTE) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                //emergency!!!
                actions = {
                    IconButton(onClick = {
                        navController.navigate("emergency_graph/home") // ✅ correct nested route
                    }) {
                        Icon(Icons.Default.Call, contentDescription = "Call")
                    }
                          },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Image with Gradient Section (rectangular)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(MaterialTheme.shapes.medium)
                ) {
                    // Background Image
                    Image(
                        painter = painterResource(R.drawable.mental_health_bg), // Add your image
                        contentDescription = "Mental Health Header",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                                    ),
                                    startY = 100f
                                )
                            )
                    )

                    // Welcome text on top of image
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = "Hi! Welcome to SuaraRasa",
                            style = MaterialTheme.typography.headlineLarge.copy(color = Color.White),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "How are you feeling today?",
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White.copy(alpha = 0.9f))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Cards Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Mental Health Test Card
                    Card(
                        onClick = { navController.navigate(AppDestinations.MENTAL_HEALTH_TEST_ROUTE) }, // Navigasi
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_test),
                                contentDescription = "Mental Health Test",
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Mental Health Test",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Check your current mental state",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // Breathing Exercises Card
                    Card(
                        onClick = { navController.navigate(AppDestinations.BREATHING_EXERCISES_ROUTE) }, // Navigasi
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_breathing),
                                contentDescription = "Breathing Exercises",
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Breathing Exercises",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "Calm your mind with guided breathing",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // Quotes Card
                    Card(
                        onClick = { navController.navigate(AppDestinations.QUOTES_ROUTE) }, // Navigasi
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_quotes),
                                contentDescription = "Inspirational Quotes",
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Inspirational Quotes",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "Get motivated with daily quotes",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            // Floating AI Button
            FloatingActionButton(
                onClick = { navController.navigate(AppDestinations.AI_ASSISTANT_ROUTE) },
                modifier = Modifier
                    .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 80.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    }
                    .size(60.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape // Pastikan bentuk bulat
            ) {
                // Gunakan Box untuk lapisan tambahan
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ai_profile),
                        contentDescription = "AI Assistant",
                        modifier = Modifier
                            .size(50.dp) // Lebih besar dari Icon
                            .clip(CircleShape) // Pastikan bulat
                            .border(
                                width = 2.dp,
                                color = Color.White,
                                shape = CircleShape
                            ),
                        contentScale = ContentScale.Crop // Pastikan gambar ter-crop dengan benar
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        NavigationBarItem(
            icon = { Icon(painterResource(R.drawable.ic_game), contentDescription = "Game") },
            selected = false,
            onClick = {
                navController.navigate(AppDestinations.GAME_HOME_ROUTE) { // Navigasi ke layar pilihan game
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            label = { Text("Game") }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(R.drawable.ic_capsule), contentDescription = "Capsule") },
            selected = false,
            onClick = {
                navController.navigate(AppDestinations.CAPSULE_ROUTE) { // Navigasi ke layar pilihan game
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            label = { Text("Capsule") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            selected = true,
            onClick =  {
                navController.navigate(AppDestinations.HOME_ROUTE) { // Navigasi ke layar pilihan game
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            label = { Text("Home") }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(R.drawable.ic_music), contentDescription = "Soul Beats") },
            selected = false,
            onClick = {
                navController.navigate(AppDestinations.MUSIC_ROUTE) { // Navigasi ke layar pilihan game
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            label = { Text("Soul Beats") }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(R.drawable.ic_mood), contentDescription = "Mood Tracker") },
            selected = false,
            onClick = {
                navController.navigate(MoodAppDestinations.MOOD_ROOT_ROUTE) { // Ini akan menavigasi ke MoodAppDestinations.MOOD_ROOT_ROUTE
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            label = { Text("Mood") }
        )
    }
}
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.suararasa.R
//import androidx.compose.material3.TopAppBarDefaults
//import androidx.compose.material3.TopAppBar
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            // Call your HomeScreen here
//            HomeScreen()
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HomeScreen() {
//    Scaffold(
//        bottomBar = {
//            BottomNavigationBar()
//        },
//        topBar = {
//            TopAppBar(
//                title = {},
//                navigationIcon = {
//                    IconButton(onClick = { /* Back action */ }) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                actions = {
//                    IconButton(onClick = { /* Menu */ }) {
//                        Icon(Icons.Default.Menu, contentDescription = "Menu")
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = Color.White
//                )
//            )
//        }
//    ) { padding ->
//        Box(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxSize()
//                .padding(16.dp),
//            contentAlignment = Alignment.TopCenter
//        ) {
//            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                Text(
//                    text = "Quotes",
//                    fontSize = 28.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(top = 32.dp, bottom = 16.dp)
//                )
//
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(300.dp)
//                        .background(Color.LightGray, shape = MaterialTheme.shapes.medium)
//                        .padding(16.dp)
//                ) {
//                    // Your API-based quote content goes here
//                }
//
//                Spacer(modifier = Modifier.height(24.dp))
//
//                // AI Assistant Placeholder (Floating Circle)
//                Box(
//                    modifier = Modifier
//                        .align(Alignment.End)
//                        .size(60.dp)
//                        .clip(CircleShape)
//                        .background(Color.Gray),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text("AI", color = Color.White)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun BottomNavigationBar() {
//    NavigationBar(
//        containerColor = Color.White,
//    ) {
//        NavigationBarItem(
//            icon = { Icon(painterResource(R.drawable.ic_game), contentDescription = "Game") },
//            selected = false,
//            onClick = { /* Navigate to Game */ },
//            label = { Text("Game") }
//        )
//        NavigationBarItem(
//            icon = { Icon(painterResource(R.drawable.ic_capsule), contentDescription = "Capsule") },
//            selected = false,
//            onClick = { /* Navigate to Capsule */ },
//            label = { Text("Capsule") }
//        )
//        NavigationBarItem(
//            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
//            selected = true,
//            onClick = { /* Already on Home */ },
//            label = { Text("Home") }
//        )
//        NavigationBarItem(
//            icon = { Icon(painterResource(R.drawable.ic_music), contentDescription = "Music") },
//            selected = false,
//            onClick = { /* Navigate to Music */ },
//            label = { Text("Music") }
//        )
//        NavigationBarItem(
//            icon = { Icon(painterResource(R.drawable.ic_mood), contentDescription = "Mood Tracker") },
//            selected = false,
//            onClick = { /* Navigate to Mood Tracker */ },
//            label = { Text("Mood") }
//        )
//    }
//}
