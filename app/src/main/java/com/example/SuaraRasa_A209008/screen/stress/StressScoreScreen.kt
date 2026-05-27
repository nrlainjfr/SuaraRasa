package com.example.SuaraRasa_A209008.screen.stress

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
fun StressScoreScreen(navController: NavController) {
    val savedStateHandle = navController.previousBackStackEntry?.savedStateHandle
    val answersString = savedStateHandle?.get<String>("answersString") ?: ""
    val answers = answersString.split(",").map { it.toIntOrNull() ?: 0 }

    val depressionAnswers = answers.take(7)
    val anxietyAnswers = answers.drop(7).take(7)
    val stressAnswers = answers.drop(14).take(7)

    val depressionScore = depressionAnswers.sum() * 2
    val anxietyScore = anxietyAnswers.sum() * 2
    val stressScore = stressAnswers.sum() * 2

    val overallScore = depressionScore + anxietyScore + stressScore

    fun level(score: Int, type: String): String {
        return when (type) {
            "Depression" -> when {
                score <= 9 -> "Normal"
                score <= 13 -> "Mild"
                score <= 20 -> "Moderate"
                score <= 27 -> "Severe"
                else -> "Extremely Severe"
            }
            "Anxiety" -> when {
                score <= 7 -> "Normal"
                score <= 9 -> "Mild"
                score <= 14 -> "Moderate"
                score <= 19 -> "Severe"
                else -> "Extremely Severe"
            }
            "Stress" -> when {
                score <= 14 -> "Normal"
                score <= 18 -> "Mild"
                score <= 25 -> "Moderate"
                score <= 33 -> "Severe"
                else -> "Extremely Severe"
            }
            else -> "Unknown"
        }
    }

    fun overallStatus(score: Int): Pair<String, Int> {
        return when {
            score <= 50 -> Pair("Good", R.raw.happy_animation)
            score <= 80 -> Pair("Moderate", R.raw.neutral_animation)
            else -> Pair("Critical", R.raw.sad_animation)
        }
    }

    fun suggestion(level: String, category: String): String {
        return when (level) {
            "Normal" -> "You are at a normal level for $category. Continue maintaining a healthy lifestyle."
            "Mild" -> "Mild level for $category. Consider stress-reduction techniques."
            "Moderate" -> "Moderate level for $category. Professional consultation may help."
            "Severe", "Extremely Severe" -> "High level for $category. Please seek professional help."
            else -> ""
        }
    }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val (status, animationRes) = overallStatus(overallScore)

    // Animation based on score
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animationRes))
    val progress by animateLottieCompositionAsState(
            composition,
            iterations = if (status == "Critical") 1 else LottieConstants.IterateForever
                                                   )

    // Pulse animation for critical status
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = if (status == "Critical") 1.1f else 1f,
            animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                                              )
                                                )

    Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                        title = {
                            Text(
                                    "Your Assessment Results",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigate("home") }) {
                                Icon(Icons.Default.Home, contentDescription = "Home")
                            }
                        },
                        actions = {
                            IconButton(onClick = {
                                val resultText = """
                            Overall Score: $overallScore
                            Depression: ${level(depressionScore, "Depression")} ($depressionScore)
                            Anxiety: ${level(anxietyScore, "Anxiety")} ($anxietyScore)
                            Stress: ${level(stressScore, "Stress")} ($stressScore)
                            Status: $status
                        """.trimIndent()
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
            // Animated result section
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

            // Score display with animated appearance
            AnimatedScoreDisplay(overallScore, status)

            Spacer(modifier = Modifier.height(24.dp))

            // Category progress indicators
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
               ) {
                AnimatedProgressCircle("Depression", depressionScore, 42)
                AnimatedProgressCircle("Anxiety", anxietyScore, 42)
                AnimatedProgressCircle("Stress", stressScore, 42)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Detailed results
            listOf(
                    Triple("Depression", depressionScore, "Depression"),
                    Triple("Anxiety", anxietyScore, "Anxiety"),
                    Triple("Stress", stressScore, "Stress")
                  ).forEach { (label, score, type) ->
                val lvl = level(score, type)
                ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.elevatedCardColors(
                                containerColor = when (lvl) {
                                    "Normal" -> MaterialTheme.colorScheme.primaryContainer
                                    "Mild" -> MaterialTheme.colorScheme.secondaryContainer
                                    "Moderate" -> MaterialTheme.colorScheme.tertiaryContainer
                                    else -> MaterialTheme.colorScheme.errorContainer
                                }
                                                                )
                            ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(label, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Score: $score", style = MaterialTheme.typography.bodyLarge)
                        Text("Level: $lvl", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                suggestion(lvl, label),
                                style = MaterialTheme.typography.bodySmall
                            )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Resources button for critical status
            if (status == "Critical") {
                Button(
                        onClick = { /* Open resources */ },
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

@Composable
private fun AnimatedScoreDisplay(score: Int, status: String) {
    val animatedScore by animateIntAsState(
            targetValue = score,
            animationSpec = tween(
                    durationMillis = 1000,
                    easing = FastOutSlowInEasing
                                 )
                                          )
    val colors = MaterialTheme.colorScheme

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
                "Your Score",
                style = MaterialTheme.typography.titleMedium,
                color = colors.onSurface.copy(alpha = 0.8f)
            )

        Text(
                animatedScore.toString(),
                style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = when (status) {
                            "Good" -> colors.primary
                            "Moderate" -> colors.secondary
                            else -> colors.error
                        }
                                                                  )
            )

        Surface(
                shape = RoundedCornerShape(16.dp),
                color = when (status) {
                    "Good" -> colors.primaryContainer
                    "Moderate" -> colors.secondaryContainer
                    else -> colors.errorContainer
                },
                modifier = Modifier.padding(top = 8.dp)
               ) {
            Text(
                    status,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
        }
    }
}

@Composable
private fun AnimatedProgressCircle(label: String, score: Int, maxScore: Int) {
    val progress = (score.toFloat() / maxScore.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(
                    durationMillis = 1000,
                    easing = FastOutSlowInEasing
                                 )
                                               )
    val colors = MaterialTheme.colorScheme

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                    progress = 1f,
                    modifier = Modifier.size(80.dp),
                    color = colors.surfaceVariant,
                    strokeWidth = 8.dp
                                     )
            CircularProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier.size(80.dp),
                    color = when {
                        progress < 0.4f -> colors.primary
                        progress < 0.7f -> colors.secondary
                        else -> colors.error
                    },
                    strokeWidth = 8.dp
                                     )
            Text(
                    "${(animatedProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(score.toString(), style = MaterialTheme.typography.bodySmall)
    }
}
