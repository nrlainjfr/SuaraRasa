package com.example.SuaraRasa_A209008.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.SuaraRasa_A209008.BottomNavigationBar
import com.example.SuaraRasa_A209008.R
import com.example.SuaraRasa_A209008.navigation.AppDestinations
import kotlinx.coroutines.delay

enum class BreathingMode(
    val label: String,
    val inhale: Int,
    val hold: Int,
    val exhale: Int
) {
    CALM("Calm (4-6)", inhale = 4000, hold = 0, exhale = 6000),
    ENERGIZE("Energize (3-2-3)", inhale = 3000, hold = 2000, exhale = 3000),
    SLEEP("Sleep (4-7-8)", inhale = 4000, hold = 7000, exhale = 8000),
    BOX("Box (4-4-4)", inhale = 4000, hold = 4000, exhale = 4000)
}

@Composable
fun ProximityBreathingScreen() {
    val context = LocalContext.current
    var isNear by remember { mutableStateOf(false) }

    var selectedMode by remember { mutableStateOf(BreathingMode.CALM) }
// Use remember to avoid recreating mediaPlayer on recomposition
    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.calm_audio)
    }
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val proximitySensor = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                val near = event?.values?.get(0) ?: return
                isNear = near < (proximitySensor?.maximumRange ?: 0f)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)

        onDispose {
            sensorManager.unregisterListener(listener)
            if (mediaPlayer.isPlaying) mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    // Control audio playback
    LaunchedEffect(isNear) {
        if (isNear) {
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
                mediaPlayer.isLooping = true
            }
        } else {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
        }
    }

    Column {
        DropdownMenuBreathingMode(
            selected = selectedMode,
            onSelected = { selectedMode = it }
        )

        if (isNear) {
            BreathingSession(mode = selectedMode)
        } else {
            PlaceYourHandPrompt()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingExercisesScreen(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val proximitySensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) }
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.calm_audio) }

    var isNear by remember { mutableStateOf(false) }
    var selectedMode by remember { mutableStateOf(BreathingMode.CALM) }

    DisposableEffect(Unit) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                val near = event?.values?.get(0) ?: return
                isNear = near < (proximitySensor?.maximumRange ?: 0f)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL)

        onDispose {
            sensorManager.unregisterListener(listener)
            if (mediaPlayer.isPlaying) mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    LaunchedEffect(isNear) {
        if (isNear) {
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
                mediaPlayer.isLooping = true
            }
        } else {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Breathing Exercise",
                        fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null &&
                        currentRoute != AppDestinations.HOME_ROUTE) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali"
                            )
                        }
                    }
                },
                // actions = {
                //     // Tambahkan actions jika diperlukan, seperti di HomeScreen
                // },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DropdownMenuBreathingMode(
                selected = selectedMode,
                onSelected = { selectedMode = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isNear) {
                BreathingSession(mode = selectedMode)
            } else {
                PlaceYourHandPrompt()
            }
        }
    }
}

@Composable
fun BreathingSession(mode: BreathingMode) {
    val scale = remember { Animatable(1f) }
    var phaseText by remember { mutableStateOf("Inhale") }

    LaunchedEffect(mode) {
        while (true) {
            phaseText = "Inhale"
            scale.animateTo(1.5f, animationSpec = tween(mode.inhale))

            if (mode.hold > 0) {
                phaseText = "Hold"
                delay(mode.hold.toLong())
            }

            phaseText = "Exhale"
            scale.animateTo(1f, animationSpec = tween(mode.exhale))
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .scale(scale.value)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = phaseText, color = Color.White, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
fun PlaceYourHandPrompt() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Place your palm near the top of your phone to begin a 3-minute calm session.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DropdownMenuBreathingMode(
    selected: BreathingMode,
    onSelected: (BreathingMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { expanded = true }) {
            Text("Mode: ${selected.label}")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            BreathingMode.values().forEach { mode ->
                DropdownMenuItem(
                    text = { Text(mode.label) },
                    onClick = {
                        onSelected(mode)
                        expanded = false
                    }
                )
            }
        }
    }
}
