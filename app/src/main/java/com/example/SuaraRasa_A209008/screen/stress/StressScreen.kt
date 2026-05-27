package com.example.SuaraRasa_A209008.screen.stress

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.SuaraRasa_A209008.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StressScreen(navController: NavController) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                        title = {
                            Text(
                                    "DASS-21 Test",
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
                            painter = painterResource(id = R.drawable.stress),
                            contentDescription = "Stress Illustration",
                            modifier = Modifier
                                .size(120.dp)
                                .padding(bottom = 8.dp),
                            contentScale = ContentScale.Fit
                         )
                    Text(
                            text = "Mental Health Assessment",
                            style = typography.headlineSmall.copy(
                                    color = colors.onPrimary,
                                    fontWeight = FontWeight.SemiBold
                                                                 )
                        )
//                    Text(
//                            text = "DASS-21 Questionnaire",
//                            style = typography.displaySmall.copy(
//                                    color = colors.onPrimary,
//                                    fontWeight = FontWeight.Bold,
//                                    center
//                                                                )
//                        )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Content Section
            Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                  ) {
                // What is DASS Card
                InfoCard(
                        title = "What is DASS-21?",
                        iconId = R.drawable.ic_info,
                        iconTint = colors.primary,
                        content = "The DASS-21 is a screening tool used to identify levels of depression, anxiety, and stress. It consists of 21 questions grouped into three main categories.",
                        tags = listOf("Depression", "Anxiety", "Stress"),
                        tagColors = listOf(colors.primary, colors.secondary, colors.tertiary)
                        )

                Spacer(modifier = Modifier.height(16.dp))

                // History Card
                InfoCard(
                        title = "History",
                        iconId = R.drawable.ic_history,
                        iconTint = colors.secondary,
                        content = "Developed by Australian psychologists to assess emotional states. Widely used in mental health screening worldwide.",
                        action = {
                            InfoWithPopup(
                                    title = "Detailed History",
                                    content = "The Depression Anxiety Stress Scales (DASS) was developed at the University of New South Wales.\n\n" +
                                            "• Originally 42 items\n" +
                                            "• Validated across cultures\n" +
                                            "• Used in clinical and research settings"
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
                                "Total Questions" to "21",
                                "Estimated Time" to "5-7 minutes",
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
                        onClick = { navController.navigate("StressQuestionScreen") },
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun InfoCard(
        title: String,
        iconId: Int,
        iconTint: Color,
        content: String,
        tags: List<String> = emptyList(),
        tagColors: List<Color> = emptyList(),
        items: List<Pair<String, String>> = emptyList(),
        action: @Composable (() -> Unit)? = null
                     ) {
    val colors = MaterialTheme.colorScheme

    ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(
                    containerColor = colors.surface,
                    contentColor = colors.onSurface
                                                    ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
               ) {
                Icon(
                        painter = painterResource(iconId),
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                        title,
                        style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                                                                        ),
                        color = iconTint
                    )
            }

            if (content.isNotEmpty()) {
                Text(
                        text = content,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = if (tags.isNotEmpty()) 12.dp else 0.dp)
                    )
            }

            if (tags.isNotEmpty()) {
                FlowRow(
                        modifier = Modifier.padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                       ) {
                    tags.forEachIndexed { index, tag ->
                        Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = tagColors.getOrElse(index) { colors.surfaceVariant },
                                contentColor = colors.onSurface
                               ) {
                            Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                        }
                    }
                }
            }

            if (items.isNotEmpty()) {
                Column {
                    items.forEach { (label, value) ->
                        Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                           ) {
                            Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors.onSurfaceVariant
                                )
                            Text(
                                    text = value,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold
                                                                                    ),
                                    color = colors.onSurface
                                )
                        }
                    }
                }
            }

            action?.invoke()
        }
    }
}

@Composable
fun InfoWithPopup(
        title: String = "More Information",
        content: String
                 ) {
    var showDialog by remember { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme

    TextButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            colors = ButtonDefaults.textButtonColors(
                    contentColor = colors.primary
                                                    )
              ) {
        Text("Read More Details")
        Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Read More",
                modifier = Modifier.size(18.dp)
            )
    }

    if (showDialog) {
        AlertDialog(
                onDismissRequest = { showDialog = false },
                shape = RoundedCornerShape(16.dp),
                title = {
                    Text(
                            title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                                                            )
                        )
                },
                text = {
                    Text(
                            content,
                            style = MaterialTheme.typography.bodyMedium
                        )
                },
                confirmButton = {
                    TextButton(
                            onClick = { showDialog = false },
                            colors = ButtonDefaults.textButtonColors(
                                    contentColor = colors.primary
                                                                    )
                              ) {
                        Text("Got It!")
                    }
                }
                   )
    }
}