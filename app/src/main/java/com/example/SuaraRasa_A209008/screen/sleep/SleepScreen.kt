package com.example.SuaraRasa_A209008.screen.sleep

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.SuaraRasa_A209008.screen.stress.InfoCard
import com.example.SuaraRasa_A209008.screen.stress.InfoWithPopup
import com.example.SuaraRasa_A209008.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepScreen(navController: NavController) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                        title = {
                            Text(
                                    "ISI Test",
                                    style = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = colors.primary
                                )
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = colors.primary
                                    )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = colors.surface,
                                titleContentColor = colors.primary
                                                                               )
                                      )
            }
            ) { padding ->
        Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(colors.background)
              ) {
            // Hero Section
            Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(
                                brush = Brush.verticalGradient(
                                        colors = listOf(colors.primary, colors.primaryContainer)
                                                              ),
                                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                                   ),
                    contentAlignment = Alignment.Center
               ) {
                Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                      ) {
                    Image(
                            painter = painterResource(id = R.drawable.sleep),
                            contentDescription = "Sleep Illustration",
                            modifier = Modifier
                                .size(120.dp)
                                .padding(bottom = 8.dp),
                            contentScale = ContentScale.Fit
                         )
                    Text(
                            text = "Insomnia Severity Index",
                            style = typography.headlineSmall.copy(
                                    color = colors.onPrimary,
                                    fontWeight = FontWeight.SemiBold
                                                                 )
                        )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Content Section
            Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                  ) {
                // What is ISI Card
                InfoCard(
                        title = "What is ISI?",
                        iconId = R.drawable.ic_info,
                        iconTint = colors.primary,
                        content = "The Insomnia Severity Index (ISI) is a brief screening tool to assess the severity of both nighttime and daytime components of insomnia. It includes 7 items.",
                        tags = listOf("Sleep", "Mental Health", "Assessment"),
                        tagColors = listOf(colors.primary, colors.secondary, colors.tertiary)
                        )

                Spacer(modifier = Modifier.height(16.dp))

                // History Card
                InfoCard(
                        title = "History",
                        iconId = R.drawable.ic_history,
                        iconTint = colors.secondary,
                        content = "ISI has been validated across cultures and used widely in sleep research to evaluate treatment outcomes.",
                        action = {
                            InfoWithPopup(
                                    title = "Detailed History",
                                    content = "The ISI was developed to measure the severity of insomnia as a symptom and disorder. It is widely used for both clinical and research purposes.\n\n• 7 items\n• Validated across multiple populations\n• Measures severity and impact"
                                         )
                        }
                        )

                Spacer(modifier = Modifier.height(16.dp))

                // Test Info Card
                InfoCard(
                        title = "Test Information",
                        iconId = R.drawable.ic_test,
                        iconTint = colors.tertiary,
                        content = "",
                        items = listOf(
                                "Total Questions" to "7",
                                "Estimated Time" to "2-3 minutes",
                                "Confidential" to "Yes"
                                      )
                        )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Start Button
            Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 16.dp)
               ) {
                Button(
                        onClick = { navController.navigate("SleepQuestionScreen") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                                containerColor = colors.primary,
                                contentColor = colors.onPrimary
                                                            ),
                        elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 8.dp,
                                pressedElevation = 4.dp
                                                                  )
                      ) {
                    Text(
                            text = "Begin Assessment",
                            style = typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                }
            }
        }
    }
}
