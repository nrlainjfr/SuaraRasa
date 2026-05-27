package com.example.a209020Lab6.ui.mood

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.SuaraRasa_A209008.MoodTrackerTopAppBar
import com.example.SuaraRasa_A209008.R
import com.example.SuaraRasa_A209008.AppViewModelProvider
import com.example.SuaraRasa_A209008.navigation.NavigationDestination
import kotlinx.coroutines.launch
import java.util.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.SuaraRasa_A209008.item.MoodDetails
import com.example.SuaraRasa_A209008.item.MoodEntryViewModel
import com.example.SuaraRasa_A209008.item.MoodUiState


object MoodEntryDestination : NavigationDestination {
    override val route = "mood_entry"
    override val titleRes = R.string.mood_entry_title
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MoodEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: MoodEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            MoodTrackerTopAppBar(
                title = stringResource(MoodEntryDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) { innerPadding ->
        MoodEntryBody(
            moodUiState = viewModel.moodUiState,
            onMoodValueChange = viewModel::updateUiState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.saveMood()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
    }
}

@Composable
fun MoodEntryBody(
    moodUiState: MoodUiState,
    onMoodValueChange: (MoodDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier.padding(24.dp)
    ) {
        // Header


        var isAnimated by remember { mutableStateOf(false) }
        val offsetY by animateDpAsState(
            targetValue = if (isAnimated) 4.dp else 0.dp,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse
            ),
            label = "textAnimation"
        )

        LaunchedEffect(Unit) { isAnimated = true }

        Text(
            text = "How are you feeling?",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Black,
                shadow = Shadow(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = offsetY)
                .padding(vertical = 16.dp),
            textAlign = TextAlign.Center
        )

        CenteredEmojiPicker(
            selectedEmotion = moodUiState.moodDetails.emotion,
            onEmotionSelected = { selected ->
                onMoodValueChange(moodUiState.moodDetails.copy(emotion = selected))
            }
        )

        // Date and Time Pickers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                DatePickerButton(
                    selectedDate = moodUiState.moodDetails.date,
                    onDateSelected = { selected ->
                        onMoodValueChange(moodUiState.moodDetails.copy(date = selected))
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            ElevatedCard(
                modifier = Modifier.weight(1f),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                TimePickerButton(
                    selectedTime = moodUiState.moodDetails.time,
                    onTimeSelected = { selected ->
                        onMoodValueChange(moodUiState.moodDetails.copy(time = selected))
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Activity Input
        OutlinedTextField(
            value = moodUiState.moodDetails.activity,
            onValueChange = { onMoodValueChange(moodUiState.moodDetails.copy(activity = it)) },
            label = { Text("What were you doing?") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Notes Input
        OutlinedTextField(
            value = moodUiState.moodDetails.note,
            onValueChange = { onMoodValueChange(moodUiState.moodDetails.copy(note = it)) },
            label = { Text("Additional notes") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Save Button
        Button(
            onClick = onSaveClick,
            enabled = moodUiState.isEntryValid,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Text(
                text = "Save Mood Entry",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CenteredEmojiPicker(
    selectedEmotion: String,
    onEmotionSelected: (String) -> Unit
) {
    val emojiMap = mapOf(
        "Happy" to "😊",
        "Sad" to "😢",
        "Angry" to "😠",
        "Excited" to "😄",
        "Tired" to "😴",
        "Calm" to "😌"
    )

    val emojiList = emojiMap.entries.toList()
    val selectedIndex = emojiList.indexOfFirst { it.key == selectedEmotion }

    val listState = rememberLazyListState()

    // Auto scroll to selected emoji when recomposed
    LaunchedEffect(selectedIndex) {
        if (selectedIndex >= 0) {
            listState.animateScrollToItem(selectedIndex)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LazyRow(
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 64.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            items(emojiList) { entry ->
                val label = entry.key
                val emoji = entry.value
                val isSelected = selectedEmotion == label

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = if (isSelected) 6.dp else 0.dp,
                        shadowElevation = if (isSelected) 6.dp else 0.dp,
                        modifier = Modifier
                            .size(120.dp) // Besarkan emoji container
                            .clickable { onEmotionSelected(label) }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = emoji,
                                style = MaterialTheme.typography.displayLarge // Guna saiz besar
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = label, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        // Dot indicator bawah emoji
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            emojiList.forEachIndexed { index, _ ->
                val color = if (emojiList[index].key == selectedEmotion)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)

                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(8.dp)
                        .background(color = color, shape = CircleShape)
                )
            }
        }
    }
}



@Composable
fun DatePickerButton(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.clickable { showDatePicker = true },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = "Select date",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(32.dp)
                .padding(bottom = 8.dp)
        )
        Text(
            text = selectedDate,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Change Date",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val selected = "%04d-%02d-%02d".format(year, month + 1, day)
                onDateSelected(selected)
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}

@Composable
fun TimePickerButton(
    selectedTime: String,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var showTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.clickable { showTimePicker = true },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = "Select time",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(32.dp)
                .padding(bottom = 8.dp)
        )
        Text(
            text = selectedTime,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Change Time",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }

    if (showTimePicker) {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                val selected = String.format("%02d:%02d", hour, minute)
                onTimeSelected(selected)
                showTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }
}

