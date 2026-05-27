package com.example.SuaraRasa_A209008.screen.home
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.SuaraRasa_A209008.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun skrinminda(navController: NavController, modifier: Modifier = Modifier) {
    Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                        title = {
                            Text(
                                    "Mental Wellness",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                                                                       )
                                )
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                titleContentColor = MaterialTheme.colorScheme.primary
                                                                               )
                                      )
            }
            ) { innerPadding ->
        Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.surface)
              ) {
            // Hero section
            Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                Text(
                        text = "Mental Health Assessment",
                        style = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                                                                          ),
                        textAlign = TextAlign.Center
                    )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        text = "Understand your mental wellbeing with our assessments",
                        style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                                                       ),
                        textAlign = TextAlign.Center
                    )
            }

            // Assessment grid
            val assessments = listOf(
                    AssessmentItem("DASS-21 Test", R.drawable.stress, "stress"),
                    AssessmentItem("ISI Test", R.drawable.sleepaalice, "sleep"),
                    // Uncomment these when ready
                    // AssessmentItem("Depression", R.drawable.depression, "depression"),
                    // AssessmentItem("Anxiety", R.drawable.anxiety, "anxiety"),
                    // AssessmentItem("Mood", R.drawable.adhd, "mood"),
                    // AssessmentItem("Self-Esteem", R.drawable.selfesteem, "selfesteem")
                                    )

            LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                items(assessments) { assessment ->
                    AssessmentCard(
                            assessment = assessment,
                            onClick = { navController.navigate(assessment.route) }
                                  )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AssessmentCard(assessment: AssessmentItem, onClick: () -> Unit) {
    Card(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.9f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                            ),
            elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp,
                    pressedElevation = 4.dp
                                                  )
        ) {
        Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
              ) {
            Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
               ) {
                Image(
                        painter = painterResource(id = assessment.imageRes),
                        contentDescription = assessment.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                     )
            }

            Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                Text(
                        text = assessment.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                                                                         ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                        text = " ",
                        style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                                                                         )
                    )
            }
        }
    }
}

data class AssessmentItem(
        val title: String,
        val imageRes: Int,
        val route: String
                         )