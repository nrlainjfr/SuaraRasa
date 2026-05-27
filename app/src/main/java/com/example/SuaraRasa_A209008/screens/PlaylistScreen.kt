package com.example.SuaraRasa_A209008.screens

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.SuaraRasa_A209008.R
import com.example.SuaraRasa_A209008.data.MusicCategory
import com.example.SuaraRasa_A209008.data.Song
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    playlist: MusicCategory,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var currentSong by remember { mutableStateOf<Song?>(null) }
    var isPaused by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var currentPosition by remember { mutableStateOf(0) }
    var duration by remember { mutableStateOf(0) }

    // Timer to update progress
    LaunchedEffect(currentSong, isPaused) {
        while (currentSong != null && !isPaused) {
            delay(1000) // Update every second
            mediaPlayer?.let {
                currentPosition = it.currentPosition
                duration = it.duration
                progress = if (duration > 0) currentPosition.toFloat() / duration else 0f
            }
        }
    }

    fun playSong(song: Song) {
        mediaPlayer?.release()
        mediaPlayer = if (song.audioUri != null) {
            MediaPlayer().apply {
                setDataSource(context, song.audioUri)
                prepare()
                start()
                setOnCompletionListener {
                    currentSong = null
                    isPaused = false
                    progress = 0f
                    currentPosition = 0
                }
                setOnPreparedListener {
                    duration = it.duration
                }
            }
        } else {
            MediaPlayer.create(context, song.audioResId).apply {
                start()
                setOnCompletionListener {
                    currentSong = null
                    isPaused = false
                    progress = 0f
                    currentPosition = 0
                }
                setOnPreparedListener {
                    duration = it.duration
                }
            }
        }
        currentSong = song
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
                title = {
                    Text(
                        text = playlist.name,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.shadow(8.dp)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(modifier = modifier.fillMaxSize()) {
            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(playlist.songs) { song ->
                    val isCurrentlyPlaying = currentSong == song
                    SongItem(
                        song = song,
                        isCurrentlyPlaying = isCurrentlyPlaying,
                        isPaused = isPaused && isCurrentlyPlaying,
                        onPlayPause = {
                            if (isCurrentlyPlaying) {
                                togglePause()
                            } else {
                                playSong(song)
                            }
                        }
                    )
                }
            }

            // Spotify-like player bar (visible when a song is playing)
            if (currentSong != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    tonalElevation = 8.dp,
                    shadowElevation = 12.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = formatTime(currentPosition),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatTime(duration),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .shadow(4.dp, RoundedCornerShape(12.dp))
                            ) {
                                Image(
                                    painter = if (currentSong!!.imageResId > 0)
                                        painterResource(id = currentSong!!.imageResId)
                                    else
                                        painterResource(id = R.drawable.ic_default_song),
                                    contentDescription = currentSong!!.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Gradient overlay
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.3f)
                                                )
                                            )
                                        )
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp)
                            ) {
                                Text(
                                    text = currentSong!!.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = currentSong!!.artist,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            IconButton(
                                onClick = { togglePause() },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                                    contentDescription = if (isPaused) "Play" else "Pause",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SongItem(
    song: Song,
    isCurrentlyPlaying: Boolean,
    isPaused: Boolean,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .shadow(4.dp, RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = if (song.imageResId > 0)
                        painterResource(id = song.imageResId)
                    else
                        painterResource(id = R.drawable.ic_default_song),
                    contentDescription = song.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.3f)
                                )
                            )
                        )
                )

                // Play button overlay
                if (!isCurrentlyPlaying || isPaused) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        shadow = if (isCurrentlyPlaying) Shadow(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            offset = Offset(1f, 1f),
                            blurRadius = 4f
                        ) else null
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (isCurrentlyPlaying) {
                    Text(
                        text = if (isPaused) "Paused" else "Now Playing",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isPaused) MaterialTheme.colorScheme.onSurfaceVariant
                            else MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            IconButton(
                onClick = onPlayPause,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (isCurrentlyPlaying) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (isCurrentlyPlaying && !isPaused) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isCurrentlyPlaying && !isPaused) "Pause" else "Play",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun formatTime(milliseconds: Int): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / (1000 * 60)) % 60
    return String.format("%02d:%02d", minutes, seconds)
}
