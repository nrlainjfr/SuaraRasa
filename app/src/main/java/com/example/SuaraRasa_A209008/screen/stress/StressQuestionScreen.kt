package com.example.SuaraRasa_A209008.screen.stress

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun StressQuestionScreen(navController: NavController) {
    val questions = listOf(
            "I found it hard to calm myself down.",
            "I tended to overreact to situations.",
            "I felt I was using a lot of energy to panic.",
            "I felt impatient when faced with delays.",
            "I felt I couldn't relax.",
            "I felt easily annoyed or irritated.",
            "I found it hard to soothe my heart/mind.",
            "I felt overly sensitive to what was happening.",
            "I found it difficult to let go of stress.",
            "I felt so stressed that I couldn't control it.",
            "I felt helpless.",
            "I lost interest in things I usually enjoy.",
            "I was easily scared for no clear reason.",
            "I often felt anxious for no reason.",
            "I had trouble sleeping at night.",
            "I had poor appetite.",
            "I felt hopeless.",
            "I found it hard to make decisions.",
            "I found it difficult to calm down after being stressed.",
            "I felt restless and anxious all the time.",
            "I felt worthless."
                          )

    val totalQuestions = questions.size
    var isWaiting by remember { mutableStateOf(false) }
    var autoNextJob by remember { mutableStateOf<Job?>(null) }
    var showExitDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    var currentQuestion by remember { mutableStateOf(0) }
    val answers = remember { mutableStateListOf<Int>().apply { repeat(totalQuestions) { add(-1) } } }

    // Get colors from theme
    val colors = MaterialTheme.colorScheme
    val primaryColor = colors.primary       // Orange
    val secondaryColor = colors.secondary   // Turquoise

    if (showExitDialog) {
        AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text("Exit Assessment") },
                text = { Text("Are you sure you want to exit this quiz? Your progress will not be saved.") },
                confirmButton = {
                    TextButton(
                            onClick = {
                                showExitDialog = false
                                navController.popBackStack()
                            }
                              ) {
                        Text("Yes, Exit")
                    }
                },
                dismissButton = {
                    TextButton(
                            onClick = { showExitDialog = false }
                              ) {
                        Text("Cancel")
                    }
                }
                   )
    }

    Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                        title = {
                            Text(
                                    "Stress Assessment",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = colors.onPrimary
                                                                                    )
                                )
                        },
                        navigationIcon = {
                            IconButton(onClick = { showExitDialog = true }) {
                                Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = colors.onPrimary
                                    )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = primaryColor,
                                titleContentColor = colors.onPrimary
                                                                               )
                                      )
            }
            ) { padding ->
        Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
              ) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))

                // Progress indicator with percentage
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                   ) {
                    Text(
                            "Question ${currentQuestion + 1}/$totalQuestions",
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.onSurfaceVariant
                        )
                    Text(
                            "${((currentQuestion + 1) * 100 / totalQuestions)}%",
                            style = MaterialTheme.typography.labelMedium,
                            color = secondaryColor
                        )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                        progress = (currentQuestion + 1) / totalQuestions.toFloat(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = secondaryColor,
                        trackColor = secondaryColor.copy(alpha = 0.2f)
                                       )
            }

            AnimatedContent(
                    targetState = currentQuestion,
                    modifier = Modifier.weight(1f)
                           ) { index ->
                Column {
                    if (isWaiting) {
                        Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                           ) {
                            Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                               ) {
                                CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = secondaryColor
                                                         )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                        "Auto-advancing in 1 second...",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = secondaryColor
                                    )
                            }
                        }
                    }

                    Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(containerColor = colors.surface)
                        ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                    "Question ${index + 1}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = primaryColor,
                                    fontWeight = FontWeight.Bold
                                )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                    questions[index],
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Normal,
                                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
                                                                                   ),
                                    textAlign = TextAlign.Start
                                )

                            Spacer(modifier = Modifier.height(24.dp))

                            val options = listOf(
                                    "0 - Never",
                                    "1 - Rarely",
                                    "2 - Sometimes",
                                    "3 - Almost Always"
                                                )

                            Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                  ) {
                                options.forEachIndexed { optIndex, option ->
                                    val isSelected = answers[index] == optIndex

                                    Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .selectable(
                                                        selected = isSelected,
                                                        onClick = {
                                                            if (answers[index] == -1) {
                                                                answers[index] = optIndex
                                                                isWaiting = true

                                                                // Start a 1-second countdown before auto-next
                                                                autoNextJob?.cancel()
                                                                autoNextJob = scope.launch {
                                                                    kotlinx.coroutines.delay(1000)
                                                                    if (currentQuestion < totalQuestions - 1) {
                                                                        currentQuestion++
                                                                    } else {
                                                                        val answersString = answers.joinToString(",")
                                                                        navController.currentBackStackEntry?.savedStateHandle?.set("answersString", answersString)
                                                                        navController.navigate("StressScoreScreen")
                                                                    }
                                                                    isWaiting = false
                                                                }
                                                            }
                                                        }
                                                           ),
                                            shape = MaterialTheme.shapes.medium,
                                            color = if (isSelected) primaryColor.copy(alpha = 0.1f) else colors.surfaceVariant,
                                            border = BorderStroke(
                                                    width = if (isSelected) 1.dp else 0.5.dp,
                                                    color = if (isSelected) secondaryColor else colors.outline.copy(alpha = 0.5f)
                                                                 )
                                           ) {
                                        Row(
                                                modifier = Modifier.padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                           ) {
                                            RadioButton(
                                                    selected = isSelected,
                                                    onClick = null,
                                                    colors = RadioButtonDefaults.colors(
                                                            selectedColor = secondaryColor,
                                                            unselectedColor = colors.onSurfaceVariant
                                                                                       )
                                                       )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                    option,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = if (isSelected) secondaryColor else colors.onSurface
                                                )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
               ) {
                if (currentQuestion > 0) {
                    OutlinedButton(
                            onClick = { currentQuestion-- },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = primaryColor
                                                                        )
                                  ) {
                        Text("Previous")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Button(
                        onClick = {
                            if (answers[currentQuestion] != -1) {
                                // Cancel auto-next if user clicks manually
                                autoNextJob?.cancel()
                                isWaiting = false

                                if (currentQuestion < totalQuestions - 1) {
                                    currentQuestion++
                                } else {
                                    val answersString = answers.joinToString(",")
                                    navController.currentBackStackEntry?.savedStateHandle?.set("answersString", answersString)
                                    navController.navigate("StressScoreScreen")
                                }
                            }
                        },
                        enabled = answers[currentQuestion] != -1,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                                containerColor = secondaryColor,
                                contentColor = colors.onSecondary
                                                            )
                      ) {
                    Text(
                            if (currentQuestion < totalQuestions - 1) "Next" else "Submit",
                            fontWeight = FontWeight.Bold
                        )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}