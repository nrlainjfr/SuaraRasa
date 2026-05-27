package com.example.SuaraRasa_A209008.screen.sleep

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.example.SuaraRasa_A209008.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepScoreScreen(navController: NavController) {
    val savedStateHandle = navController.previousBackStackEntry?.savedStateHandle
    val answersString = savedStateHandle?.get<String>("answersString") ?: ""
    val answers = answersString.split(",").map { it.toIntOrNull() ?: 0 }

    val totalScore = answers.sum()

    fun getSleepLevel(score: Int): String {
        return when {
            score <= 7 -> "No clinically significant insomnia"
            score <= 14 -> "Subthreshold insomnia"
            score <= 21 -> "Moderate insomnia"
            else -> "Severe insomnia"
        }
    }

    fun getSuggestion(level: String): String {
        return when (level) {
            "No clinically significant insomnia" -> "Your sleep pattern is within a healthy range. Keep maintaining good sleep hygiene."
            "Subthreshold insomnia" -> "Some sleep issues are present. Try improving your routine with consistent sleep and relaxation habits."
            "Moderate insomnia" -> "You might benefit from cognitive-behavioral therapy for insomnia (CBT-I). Consider consulting a sleep specialist."
            "Severe insomnia" -> "Severe insomnia detected. It's important to seek professional evaluation or medical help immediately."
            else -> ""
        }
    }

    val sleepLevel = getSleepLevel(totalScore)
    val suggestion = getSuggestion(sleepLevel)

    val (animationRes, statusColor) = when (sleepLevel) {
        "No clinically significant insomnia" -> Pair(R.raw.happy_animation, MaterialTheme.colorScheme.primary)
        "Subthreshold insomnia" -> Pair(R.raw.neutral_animation, MaterialTheme.colorScheme.secondary)
        else -> Pair(R.raw.sad_animation, MaterialTheme.colorScheme.error)
    }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animationRes))
    val progress by animateLottieCompositionAsState(
            composition,
            iterations = if (sleepLevel == "Severe insomnia") 1 else LottieConstants.IterateForever
                                                   )

    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = if (sleepLevel == "Severe insomnia") 1.1f else 1f,
            animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse)
                                                )

    Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                        title = { Text("Sleep Assessment Result", fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigate("home") }) {
                                Icon(Icons.Default.Home, contentDescription = "Home")
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                val resultText = "Sleep Score: $totalScore\nLevel: $sleepLevel\n\n$suggestion"
                                clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(resultText))
                                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.Share, contentDescription = "Share")
                            }
                        }
                                      )
            }
            ) { padding ->
        Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
            Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .scale(pulse),
                    contentAlignment = Alignment.Center
               ) {
                LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(200.dp)
                               )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Your Score", style = MaterialTheme.typography.titleMedium)
            Text(
                    totalScore.toString(),
                    style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                                                                      )
                )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = statusColor.copy(alpha = 0.2f)
                   ) {
                Text(
                        sleepLevel,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = statusColor
                    )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                    suggestion,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

            Spacer(modifier = Modifier.height(24.dp))

            if (sleepLevel == "Severe insomnia") {
                Button(
                        onClick = { /* Link to help or resources */ },
                        colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                                                            ),
                        modifier = Modifier.fillMaxWidth()
                      ) {
                    Text("Get Help Now")
                }
            }
        }
    }
}
