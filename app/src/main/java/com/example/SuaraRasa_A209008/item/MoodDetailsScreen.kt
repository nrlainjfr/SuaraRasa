package com.example.SuaraRasa_A209008.item

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.SuaraRasa_A209008.MoodTrackerTopAppBar
import com.example.SuaraRasa_A209008.R
import com.example.SuaraRasa_A209008.data.MoodEntry
import com.example.SuaraRasa_A209008.AppViewModelProvider
import com.example.SuaraRasa_A209008.navigation.NavigationDestination
import kotlinx.coroutines.launch

object MoodDetailsDestination : NavigationDestination {
    override val route = "mood_details"
    override val titleRes = R.string.mood_detail_title
    const val moodIdArg = "moodId"
    val routeWithArgs = "$route/{$moodIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodDetailsScreen(
    navigateToEditMood: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MoodDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    Log.d("MoodDetailsScreen", "Loaded mood ID: ${uiState.moodDetails.id}")

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            MoodTrackerTopAppBar(
                title = stringResource(MoodDetailsDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        floatingActionButton = {
            val moodId = uiState.moodDetails.id
            if (moodId != 0) {
                FloatingActionButton(
                    onClick = { navigateToEditMood(moodId) },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(60.dp) // Slightly smaller than home screen FAB
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit_mood_title),
                        modifier = Modifier.size(40.dp), // Proportional icon size
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        MoodDetailsBody(
            moodDetailsUiState = uiState,
            onDelete = {
                coroutineScope.launch {
                    viewModel.deleteMood()
                    navigateBack()
                }
            },
            modifier = Modifier
                .padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = innerPadding.calculateTopPadding(),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                )
                .verticalScroll(rememberScrollState())
        )
    }
}

@Composable
private fun MoodDetailsBody(
    moodDetailsUiState: MoodDetailsUiState,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

        MoodDetails(
            mood = moodDetailsUiState.moodDetails.toMoodEntry(),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { deleteConfirmationRequired = true },
            //shape = MaterialTheme.shapes.medium,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.delete))
        }

        if (deleteConfirmationRequired) {
            DeleteConfirmationDialog(
                onDeleteConfirm = {
                    deleteConfirmationRequired = false
                    onDelete()
                },
                onDeleteCancel = { deleteConfirmationRequired = false },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun MoodDetails(
    mood: MoodEntry,
    modifier: Modifier = Modifier
) {
    val cardColor = when (mood.emotion) {
        "Happy" -> Color(0xFFFFF9C4) // Light yellow
        "Sad" -> Color(0xFFE1F5FE)   // Light blue
        "Angry" -> Color(0xFFFFEBEE) // Light red
        "Excited" -> Color(0xFFF3E5F5) // Light purple
        "Tired" -> Color(0xFFEFEBE9) // Light beige
        "Calm" -> Color(0xFFE0F7FA)  // Light cyan
        else -> MaterialTheme.colorScheme.surface
    }

    val emojiMap = mapOf(
        "Happy" to "😊",
        "Sad" to "😢",
        "Angry" to "😠",
        "Excited" to "😄",
        "Tired" to "😴",
        "Calm" to "😌"
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = cardColor,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with emoji and emotion
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                ) {
                    Text(
                        text = emojiMap[mood.emotion] ?: "🙂",
                        style = MaterialTheme.typography.displayMedium
                    )
                }
                Column {
                    Text(
                        text = mood.emotion,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${mood.date} • ${mood.time}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Divider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            // Mood details
            MoodDetailsRow(
                labelResID = R.string.activity,
                moodDetail = mood.activity,
                icon = Icons.Default.DirectionsRun
            )

            if (mood.note.isNotBlank()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.note),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = mood.note,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun MoodDetailsRow(
    @StringRes labelResID: Int,
    moodDetail: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp))
        } ?: Spacer(modifier = Modifier.size(24.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(labelResID),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = moodDetail,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDeleteCancel,
        title = {
            Text(
                text = stringResource(R.string.attention),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = stringResource(R.string.delete_question),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDeleteConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(text = stringResource(R.string.yes))
            }
        }
    )
}
