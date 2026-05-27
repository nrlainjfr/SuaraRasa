package com.example.SuaraRasa_A209008.screens

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.SuaraRasa_A209008.R
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.navigationBarsPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationListScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var currentSongId by remember { mutableStateOf<Int?>(null) }
    var isPaused by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var currentPosition by remember { mutableStateOf(0) }
    var duration by remember { mutableStateOf(0) }

    val songs = listOf(
        Song("Amberlight", "Scott Buckley", R.drawable.meditation1, R.raw.meditation1),
        Song("Sonder", "Purrple Cat", R.drawable.meditation2, R.raw.meditation2),
        Song("Golden Hour", "Purrple Cat", R.drawable.meditation3, R.raw.meditation3),
        Song("Day Off", "Tokyo Music Walker", R.drawable.meditation4, R.raw.meditation4),
        Song("Permafrost", "Scott Buckley", R.drawable.meditation5, R.raw.meditation5),
        Song("Wildflowers", "Purrple Cat", R.drawable.meditation6, R.raw.meditation6),
    )

    LaunchedEffect(currentSongId, isPaused) {
        while (currentSongId != null && !isPaused) {
            delay(1000)
            mediaPlayer?.let {
                currentPosition = it.currentPosition
                progress = if (duration > 0) currentPosition.toFloat() / duration else 0f
            }
        }
    }

    fun playSong(songId: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, songId).apply {
            setOnCompletionListener {
                currentSongId = null
                isPaused = false
                progress = 0f
                currentPosition = 0
            }
            setOnPreparedListener {
                duration = it.duration
            }
            start()
        }
        currentSongId = songId
        isPaused = false
        progress = 0f
        currentPosition = 0
    }

    fun togglePause() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                isPaused = true
            } else {
                player.start()
                isPaused = false
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Meditation List") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = modifier.fillMaxSize()) {
            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier.weight(1f)
            ) {
                items(songs) { song ->
                    val isPlaying = currentSongId == song.audioResId
                    SongItem(
                        song = song,
                        isCurrentlyPlaying = isPlaying,
                        isPaused = isPaused && isPlaying,
                        onPlayPause = {
                            if (isPlaying) togglePause()
                            else playSong(song.audioResId)
                        }
                    )
                }
            }

            if (currentSongId != null) {
                val currentSong = songs.find { it.audioResId == currentSongId }
                currentSong?.let { song ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .navigationBarsPadding()
                    ) {
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(formatTime(currentPosition), style = MaterialTheme.typography.labelSmall)
                            Text(formatTime(duration), style = MaterialTheme.typography.labelSmall)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = song.imageResId),
                                contentDescription = song.title,
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(end = 12.dp),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(song.title, style = MaterialTheme.typography.titleSmall)
                                Text(song.artist, style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(
                                onClick = { togglePause() },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                                    contentDescription = if (isPaused) "Play" else "Pause"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatTime(ms: Int): String {
    val minutes = (ms / 1000) / 60
    val seconds = (ms / 1000) % 60
    return "%02d:%02d".format(minutes, seconds)
}
