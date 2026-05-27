package com.example.SuaraRasa_A209008.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.*
import androidx.core.content.ContextCompat
import com.example.SuaraRasa_A209008.data.DiaryDatabase
import com.example.SuaraRasa_A209008.data.LetterDao
import com.example.SuaraRasa_A209008.data.LetterEntity
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CapsuleScreen(navController: NavController) {
    val context = LocalContext.current
    val dao = DiaryDatabase.getInstance(context).letterDao()
    val innerNavController = rememberNavController()

    val currentRoute = innerNavController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentRoute) {
                            "compose" -> "Write to Your Future Self"
                            "history" -> "Past Letters"
                            else -> "Mini Games"
                        },
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    if (innerNavController.previousBackStackEntry != null &&
                        currentRoute != "compose"
                    ) {
                        IconButton(onClick = { innerNavController.popBackStack() }) {
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
        NavHost(
            navController = innerNavController,
            startDestination = "compose",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("compose") {
                ComposeScreen(letterDao = dao, navController = innerNavController)
            }
            composable("history") {
                HistoryScreen(letterDao = dao, navController = innerNavController)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeScreen(letterDao: LetterDao, navController: NavController) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var title by remember { mutableStateOf("Dear FutureMe,") }
    var content by remember { mutableStateOf("") }

    // Mood emojis
    val moods = listOf("😄", "😢", "😠", "😌", "🤔", "❤️")
    var selectedMood by remember { mutableStateOf(moods.first()) }

    // Delivery logic
    val durations = listOf("6 months", "1 year", "3 years", "5 years", "10 years")
    var selectedDuration by remember { mutableStateOf("6 months") }
    var deliveryMillis by remember { mutableStateOf(com.example.SuaraRasa_A209008.screens.calculateFutureMillis("6 months")) }

    var choosingDate by remember { mutableStateOf(false) }
    var customDateText by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val greetings = listOf(
        "Who's Your Crush?", "Are We Doing Fine?", "Are We Still Hurting?",
        "Is Mom And Dad Still Alive?", "Are You Okay?", "Do We Have New Friends?"
    )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        TextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )
                    }
                }
                // Content Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        TextField(
                            value = content,
                            onValueChange = { content = it },
                            label = { Text("Letter Content") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )
                    }
                }

                // Mood Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("How are you feeling today?", style = MaterialTheme.typography.labelLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            moods.forEach {
                                Button(
                                    onClick = { selectedMood = it },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedMood == it) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.secondaryContainer
                                    ),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(it, style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }
                }

                // Delivery Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (!choosingDate) {
                            Text("When should we deliver this?", style = MaterialTheme.typography.labelLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                durations.forEach { duration ->
                                    Button(
                                        onClick = {
                                            selectedDuration = duration
                                            deliveryMillis = calculateFutureMillis(duration)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selectedDuration == duration) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.secondaryContainer
                                        ),
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(duration)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Prefer a specific date? ")
                                TextButton(onClick = { choosingDate = true }) {
                                    Text("Pick a date")
                                }
                            }
                        } else {
                            Text("Choose your delivery date", style = MaterialTheme.typography.labelLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    val calendar = Calendar.getInstance()
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, day ->
                                            calendar.set(year, month, day, 0, 0)
                                            deliveryMillis = calendar.timeInMillis
                                            customDateText = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                                .format(calendar.time)
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    ).apply {
                                        datePicker.minDate = System.currentTimeMillis()
                                    }.show()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (customDateText.isNotEmpty()) "Selected: $customDateText" else "Choose date")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = {
                                    choosingDate = false
                                    customDateText = ""
                                    deliveryMillis = calculateFutureMillis(selectedDuration)
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Back to durations")
                            }
                        }
                    }
                }

                // Action buttons
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { title = greetings.random() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Inspire Me ✨")
                        }

                        Button(
                            onClick = {
                                if (content.isNotBlank()) {
                                    val finalLetter = LetterEntity(
                                        title = title,
                                        content = content,
                                        mood = selectedMood,
                                        dateToOpen = deliveryMillis
                                    )

                                    CoroutineScope(Dispatchers.IO).launch {
                                        letterDao.insert(finalLetter)
                                        withContext(Dispatchers.Main) {
                                            showSuccessDialog = true
                                        }
                                    }
                                } else {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        snackbarHostState.showSnackbar("Please write your letter first!")
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            enabled = content.isNotBlank()
                        ) {
                            Text("Send to Future")
                        }
                    }

                    Button(
                        onClick = { navController.navigate("history") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("View Past Letters 📜")
                    }
                }
            }

            // Success Dialog
            if (showSuccessDialog) {
                Dialog(
                    onDismissRequest = { showSuccessDialog = false },
                    properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                modifier = Modifier.size(48.dp),
                                tint = Color(0xFF4CAF50)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "Letter Scheduled!",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Your future self will receive this on:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                    .format(Date(deliveryMillis)),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { showSuccessDialog = false },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("OK")
                            }
                        }
                    }
                }
            }
        }



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(letterDao: LetterDao, navController: NavController) {
    val pastLetters = remember { mutableStateListOf<LetterEntity>() }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Happy", "Sad", "Neutral", "Excited", "Angry", "Love")

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        letterDao.getPastLetters(System.currentTimeMillis()).collectLatest { list ->
            pastLetters.clear()
            pastLetters.addAll(list)
        }
    }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
// Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search letters...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true,
                shape = MaterialTheme.shapes.extraLarge,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    disabledIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter chips
            Text("Filter by mood:", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        leadingIcon = if (selectedFilter == filter) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Letters list
            if (pastLetters.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Center
                ) {
                    Column(horizontalAlignment = CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "View Past Letters",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No past letters yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                val filteredLetters = pastLetters.filter { letter ->
                    val matchesSearch = searchQuery.isEmpty() ||
                            letter.title.contains(searchQuery, ignoreCase = true) ||
                            letter.content.contains(searchQuery, ignoreCase = true)

                    val matchesMood = selectedFilter == "All" ||
                            emojiToMoodName(letter.mood)
                                .equals(selectedFilter, ignoreCase = true)

                    matchesSearch && matchesMood
                }

                if (filteredLetters.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Center
                    ) {
                        Text(
                            "No letters match your search",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredLetters) { letter ->
                            ElevatedCard(
                                onClick = { /* Handle click if needed */ },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = letter.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            maxLines = 1,
                                            modifier = Modifier.weight(1f)
                                        )

                                        // Mood indicator
                                        Box(
                                            modifier = Modifier
                                                .padding(start = 8.dp)
                                                .background(
                                                    color = when (letter.mood.toLowerCase(Locale.ROOT)) {
                                                        "happy" -> Color(0xFF4CAF50)
                                                        "sad" -> Color(0xFF2196F3)
                                                        "neutral" -> Color(0xFF9E9E9E)
                                                        "excited" -> Color(0xFFFF9800)
                                                        "angry" -> Color(0xFFF44336)
                                                        else -> MaterialTheme.colorScheme.primary
                                                    }.copy(alpha = 0.2f),
                                                    shape = MaterialTheme.shapes.small
                                                )
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = letter.mood,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Letter content preview
                                    Text(
                                        text = letter.content.take(150) + if (letter.content.length > 150) "..." else "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Date and action buttons
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
                                                .format(Date(letter.dateToOpen)),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )

                                        val context = LocalContext.current

                                        Row {
                                            IconButton(
                                                onClick = {
                                                    shareLetter(
                                                        context = context,
                                                        letter = letter
                                                    )
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Share,
                                                    contentDescription = "Share",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

private fun calculateFutureMillis(duration: String): Long {
    val calendar = Calendar.getInstance()
    when (duration) {
        "6 months" -> calendar.add(Calendar.MONTH, 6)
        "1 year" -> calendar.add(Calendar.YEAR, 1)
        "3 years" -> calendar.add(Calendar.YEAR, 3)
        "5 years" -> calendar.add(Calendar.YEAR, 5)
        "10 years" -> calendar.add(Calendar.YEAR, 10)
    }
    return calendar.timeInMillis
}

fun shareLetter(context: Context, letter: LetterEntity) {
    val shareText = """
        **${letter.title}**
        
        ${letter.content}
        
        *Written on: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(letter.dateToOpen))}*
    """.trimIndent()

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Share Letter")
    ContextCompat.startActivity(context, shareIntent, null)
}
fun emojiToMoodName(emoji: String): String = when (emoji) {
    "😄" -> "Happy"
    "😢" -> "Sad"
    "😠" -> "Angry"
    "😌" -> "Neutral"
    "🤔" -> "Excited"
    "❤️" -> "Love"
    else -> "Unknown"
}
