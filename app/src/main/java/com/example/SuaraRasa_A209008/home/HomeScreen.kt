package com.example.SuaraRasa_A209008.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Mood
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.SuaraRasa_A209008.MoodTrackerTopAppBar
import com.example.SuaraRasa_A209008.R
import com.example.SuaraRasa_A209008.data.MoodEntry
import com.example.SuaraRasa_A209008.AppViewModelProvider
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.compose.rememberNavController


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MoodAppHomeScreen(
        navigateToMoodEntry: () -> Unit,
        navigateToMoodUpdate: (Int) -> Unit,
        navigateUp: () -> Unit,
        modifier: Modifier = Modifier,
        viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
              ) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val homeUiState by viewModel.homeUiState.collectAsState()
    val navController = rememberNavController()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MoodTrackerTopAppBar(
                        title = stringResource(R.string.app_name),
                        canNavigateBack = true,
                        scrollBehavior = scrollBehavior,
                                navigateUp = navigateUp
                                    )
            },
            floatingActionButton = {
                FloatingActionButton(
                        onClick = navigateToMoodEntry,
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(72.dp)
                                    ) {
                    Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add mood",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                }
            }
            ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Search Bar
            SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                     )

            // Mood Ranking Section
            val moodRanking = homeUiState.moodList
                .groupBy { it.emotion }
                .mapValues { it.value.size }
                .entries.sortedByDescending { it.value }
                .take(3)

            if (moodRanking.isNotEmpty() && searchQuery.isEmpty()) {
                MoodRankingSection(moodRanking)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Mood Entries List
            val filteredMoodList = if (searchQuery.isEmpty()) {
                homeUiState.moodList
            } else {
                homeUiState.moodList.filter {
                    it.emotion.contains(searchQuery, ignoreCase = true) ||
                            it.activity.contains(searchQuery, ignoreCase = true) ||
                            it.note.contains(searchQuery, ignoreCase = true)
                }
            }

            HomeBody(
                    moodList = filteredMoodList,
                    onMoodClick = navigateToMoodUpdate,
                    modifier = Modifier.weight(1f)
                    )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodTrackerTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {}, // Tambahkan parameter ini
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
        query: String,
        onQueryChange: (String) -> Unit,
        modifier: Modifier = Modifier
             ) {
    TextField(
            value = query,
            onValueChange = onQueryChange,
            leadingIcon = {
                Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
            },
            placeholder = {
                Text("Search moods...")
            },
            colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                                             ),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            modifier = modifier
             )
}

@Composable
fun MoodRankingSection(ranking: List<Map.Entry<String, Int>>) {
    Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
          ) {
        Text(
                text = "Your Top Moods",
                style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                                                                 ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
           ) {
            // Show in order: 2nd, 1st, 3rd place
            val orderedRanking = listOf(
                    ranking.getOrNull(1),
                    ranking.firstOrNull(),
                    ranking.getOrNull(2)
                                       ).filterNotNull()

            orderedRanking.forEachIndexed { index, entry ->
                val isFirstPlace = index == 1
                val emojiSize = if (isFirstPlace) 48.dp else 40.dp
                val cardColor = when (entry.key) {
                    "Happy" -> Color(0xFFFFD54F)
                    "Sad" -> Color(0xFF81D4FA)
                    "Angry" -> Color(0xFFFF8A80)
                    "Excited" -> Color(0xFFCE93D8)
                    "Tired" -> Color(0xFFE0E0E0)
                    "Calm" -> Color(0xFFA5D6A7)
                    else -> MaterialTheme.colorScheme.surface
                }

                Box(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(
                                    elevation = if (isFirstPlace) 8.dp else 4.dp,
                                    shape = RoundedCornerShape(16.dp)
                                   )
                            .background(
                                    color = cardColor.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(16.dp)
                                       )
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                   ) {
                    Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                          ) {
                        Text(
                                text = when (index) {
                                    0 -> "🥈"
                                    1 -> "🥇"
                                    else -> "🥉"
                                },
                                style = MaterialTheme.typography.headlineSmall
                            )
                        Text(
                                text = when (entry.key) {
                                    "Happy" -> "😊"
                                    "Sad" -> "😢"
                                    "Angry" -> "😡"
                                    "Excited" -> "🤩"
                                    "Tired" -> "😴"
                                    "Calm" -> "😌"
                                    else -> "🙂"
                                },
                                style = MaterialTheme.typography.displayLarge,
                                modifier = Modifier.size(emojiSize)
                            )
                        Text(
                                text = entry.key,
                                style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold
                                                                                )
                            )
                        Text(
                                text = "${entry.value} entries",
                                style = MaterialTheme.typography.labelSmall
                            )
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodCard(
        mood: MoodEntry,
        onMoodClick: (MoodEntry) -> Unit,
        modifier: Modifier = Modifier
                    ) {
    // Enhanced emoji selection with more expressive options
    val emojiMap = mapOf(
            "Happy" to "😊",    // Friendly smile
            "Sad" to "😢",      // Crying face
            "Angry" to "😡",    // Very angry face
            "Excited" to "🤩",  // Star-struck
            "Tired" to "😴",    // Sleeping face
            "Calm" to "😌"      // Relieved face
                        )

    // Gradient colors for each emotion
    val cardColors = when (mood.emotion) {
        "Happy" -> listOf(Color(0xFFFFD54F), Color(0xFFFFB74D))
        "Sad" -> listOf(Color(0xFF81D4FA), Color(0xFF4FC3F7))
        "Angry" -> listOf(Color(0xFFFF8A80), Color(0xFFFF5252))
        "Excited" -> listOf(Color(0xFFCE93D8), Color(0xFFBA68C8))
        "Tired" -> listOf(Color(0xFFE0E0E0), Color(0xFFBDBDBD))
        "Calm" -> listOf(Color(0xFFA5D6A7), Color(0xFF81C784))
        else -> listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surface)
    }

    Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { onMoodClick(mood) }
                .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(24.dp)
                       ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                                            )
        ) {
        Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                            brush = Brush.verticalGradient(
                                    colors = cardColors,
                                    startY = 0f,
                                    endY = 500f
                                                          ),
                            shape = RoundedCornerShape(24.dp)
                               )
           ) {
            Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                  ) {
                // Header with emoji and emotion
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                   ) {
                    Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(72.dp)  // Larger emoji container
                                .clip(CircleShape)
                                .background(
                                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                                        shape = CircleShape
                                           )
                       ) {
                        Text(
                                text = emojiMap[mood.emotion] ?: "🙂",
                                style = MaterialTheme.typography.displayLarge.copy(
                                        fontSize = 36.sp  // Larger emoji size
                                                                                  ),
                                modifier = Modifier.padding(8.dp)
                            )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = mood.emotion.uppercase(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 22.sp  // Larger text size
                                                                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        Text(
                                text = "${mood.date} • ${mood.time}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        fontSize = 16.sp  // Larger text size
                                                                                 )
                            )
                    }
                }

                // Activity section
                Text(
                        text = "Activity:",
                        style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                fontSize = 14.sp  // Larger text size
                                                                        )
                    )
                Text(
                        text = mood.activity,
                        style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 18.sp  // Larger text size
                                                                       )
                    )

                // Note section (only if exists)
                if (mood.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                            text = "Note:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    fontSize = 14.sp  // Larger text size
                                                                            )
                        )
                    Text(
                            text = mood.note,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 16.sp  // Larger text size
                                                                            ),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                }
            }
        }
    }
}

@Composable
private fun HomeBody(
        moodList: List<MoodEntry>,
        onMoodClick: (Int) -> Unit,
        modifier: Modifier = Modifier
                    ) {
    Box(modifier = modifier.fillMaxSize()) {
        if (moodList.isEmpty()) {
            EmptyStateScreen()
        } else {
            LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                      ) {
                items(items = moodList, key = { it.id }) { mood ->
                    MoodCard(
                            mood = mood,
                            onMoodClick = { onMoodClick(it.id) }
                            )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateScreen() {
    Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
        Icon(
                imageVector = Icons.Rounded.Mood,
                contentDescription = "No entries",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                modifier = Modifier.size(120.dp)
            )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
                text = "No Mood Entries Yet",
                style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                                                )
            )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
                text = "Tap the + button to record your first mood",
                style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                                                ),
                textAlign = TextAlign.Center
            )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun MoodRankingPreview() {
//    MoodTrackerTheme {
//        MoodRankingSection(listOf(
//                Map.entry("Happy", 5),
//                Map.entry("Sad", 3),
//                Map.entry("Calm", 2)
//                                 ))
//    }
//}