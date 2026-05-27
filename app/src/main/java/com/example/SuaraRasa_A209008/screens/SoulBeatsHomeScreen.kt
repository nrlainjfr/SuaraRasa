package com.example.SuaraRasa_A209008.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberImagePainter
import androidx.navigation.NavController
import com.example.SuaraRasa_A209008.PlaylistViewModel
import com.example.SuaraRasa_A209008.R
import com.example.SuaraRasa_A209008.data.MusicCategory
import com.example.SuaraRasa_A209008.data.Song
import com.example.SuaraRasa_A209008.navigation.AppDestinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoulBeatsHomeScreen(
    navController: NavController,
    onInstrumentalClick: () -> Unit,
    onLofiClick: () -> Unit,
    onLullabyClick: () -> Unit,
    onMeditationClick: () -> Unit,
    onSadClick: () -> Unit,
    onSleepClick: () -> Unit,
    onAddBeatsClick: () -> Unit,
    onPlaylistClick: (MusicCategory) -> Unit,
    onEditPlaylist: (MusicCategory) -> Unit,
    playlistViewModel: PlaylistViewModel,
    modifier: Modifier = Modifier
) {
    // Define all song lists
    val instrumentalSongs = listOf(
        Song("Heart Full of Love", "Artist 1", R.drawable.instrumental, R.raw.instrumentone),
        Song("Lullaby of The Stars", "Artist 2", R.drawable.lofi, R.raw.instrument2),
        Song("Natural Beauty", "Artist 3", R.drawable.lullaby, R.raw.instrument3),
        Song("Instrumental", "Artist 4", R.drawable.meditation, R.raw.instrument4),
        Song("Thinking of Good Time", "Artist 5", R.drawable.sad, R.raw.instrument5)
    )

    val lofiSongs = listOf(
        Song("Jay", "Lukrembo", R.drawable.lofi, R.raw.lofi1),
        Song("Gingersweet", "Massobeats", R.drawable.lofi, R.raw.lofi2),
        Song("Coming of Age", "Hazelwood", R.drawable.lofi, R.raw.lofi3),
        Song("Time", "Avanti", R.drawable.lofi, R.raw.lofi4),
        Song("A Beautiful Garden", "Aventure", R.drawable.lofi, R.raw.lofi5),
        Song("Dreamy Skies", "Pufino", R.drawable.lofi, R.raw.lofi6),
        Song("Florida Keys", "Hoffy Beats", R.drawable.lofi, R.raw.lofi7),
        Song("Marshmallow", "Lukrembo", R.drawable.lofi, R.raw.lofi8),
        Song("Park Vibes", "Filo Starquez", R.drawable.lofi, R.raw.lofi9),
        Song("Love in Japan", "Milky Wayvers", R.drawable.lofi, R.raw.lofi10)
    )

    val lullabySongs = listOf(
        Song("2-Slow", "Lloyd Rodgers", R.drawable.lullaby, R.raw.lullaby1),
        Song("Child Dreams", "Keys of Moon", R.drawable.lullaby, R.raw.lullaby2),
        Song("Silent Night", "Kevin MacLeod", R.drawable.lullaby, R.raw.lullaby3),
        Song("Lullaby", "Keys of Moon", R.drawable.lullaby, R.raw.lullaby4),
        Song("Moonlight", "Scott Buckley", R.drawable.lullaby, R.raw.lullaby5),
        Song("Twinkle Like A Star", "Monday Hopes", R.drawable.lullaby, R.raw.lullaby6)
    )

    val meditationSongs = listOf(
        Song("Amberlight", "Scott Buckley", R.drawable.meditation, R.raw.meditation1),
        Song("Sonder", "Purrple Cat", R.drawable.meditation, R.raw.meditation2),
        Song("Golden Hour", "Purrple Cat", R.drawable.meditation, R.raw.meditation3),
        Song("Day Off", "Tokyo Music Walker", R.drawable.meditation, R.raw.meditation4),
        Song("Permafrost", "Scott Buckley", R.drawable.meditation, R.raw.meditation5),
        Song("Wildflowers", "Purrple Cat", R.drawable.meditation, R.raw.meditation6)
    )

    val sadSongs = listOf(
        Song("Sad Sorrowful", "Artist 1", R.drawable.sad, R.raw.sad1),
        Song("Gingersweet", "Artist 2", R.drawable.sad, R.raw.sad2),
        Song("Ashes of Hiroshima of Age", "Artist 3", R.drawable.sad, R.raw.sad3),
        Song("Dedpression", "Artist 4", R.drawable.sad, R.raw.sad4),
        Song("Loneliness Long", "Artist 5", R.drawable.sad, R.raw.sad5),
        Song("Forgotten Memories", "Artist 6", R.drawable.sad, R.raw.sad6),
        Song("For When It Rains", "Artist 7", R.drawable.sad, R.raw.sad7),
        Song("Sad Violin", "Artist 8", R.drawable.sad, R.raw.sad8),
        Song("Lost Time", "Artist 9", R.drawable.sad, R.raw.sad9)
    )

    val sleepSongs = listOf(
        Song("Sleep Music Vol.5", "Artist 1", R.drawable.sleep, R.raw.sleep1),
        Song("Forest Lullaby", "Artist 2", R.drawable.sleep, R.raw.sleep2),
        Song("Healing Sleep Atmosphere", "Artist 3", R.drawable.sleep, R.raw.sleep3),
        Song("Peaceful Sleep", "Artist 4", R.drawable.sleep, R.raw.sleep4),
        Song("Sleep Music Vol.11", "Artist 5", R.drawable.sleep, R.raw.sleep5)
    )

    // Create default categories with unique IDs
    val defaultCategories = listOf(
        MusicCategory(id = "instrumental_1", name = "Instrumental", imageResId = R.drawable.instrumental, songs = instrumentalSongs),
        MusicCategory(id = "lofi_1", name = "Lofi", imageResId = R.drawable.lofi, songs = lofiSongs),
        MusicCategory(id = "lullaby_1", name = "Lullaby", imageResId = R.drawable.lullaby, songs = lullabySongs),
        MusicCategory(id = "meditation_1", name = "Meditation", imageResId = R.drawable.meditation, songs = meditationSongs),
        MusicCategory(id = "sad_1", name = "Sad", imageResId = R.drawable.sad, songs = sadSongs),
        MusicCategory(id = "sleep_1", name = "Sleep", imageResId = R.drawable.sleep, songs = sleepSongs)
    )

    // Combine default categories with user playlists
    val allCategories = defaultCategories + playlistViewModel.playlists
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // State for dialog
    var showDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<MusicCategory?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Soul Beats",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null &&
                        currentRoute != AppDestinations.HOME_ROUTE) {
                        IconButton(onClick = { navController.popBackStack() }) {
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
    ){ innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier.padding(innerPadding),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(allCategories) { category ->
                val isCustomPlaylist = !defaultCategories.any { it.id == category.id }

                MusicCategoryItem(
                    category = category,
                    onClick = {
                        when (category.id) {
                            "instrumental_1" -> onInstrumentalClick()
                            "lofi_1" -> onLofiClick()
                            "lullaby_1" -> onLullabyClick()
                            "meditation_1" -> onMeditationClick()
                            "sad_1" -> onSadClick()
                            "sleep_1" -> onSleepClick()
                            else -> onPlaylistClick(category)
                        }
                    },
                    onLongPress = if (isCustomPlaylist) {
                        {
                            selectedCategory = category
                            showDialog = true
                        }
                    } else null,
                    isCustomPlaylist = isCustomPlaylist
                )
            }

            item {
                AddYourBeatCard(onClick = onAddBeatsClick)
            }
        }

        // Delete/Edit Dialog
        if (showDialog && selectedCategory != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(
                        "Edit Playlist",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                text = {
                    Text(
                        "What do you want to do with \"${selectedCategory?.name.orEmpty()}\"?",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                confirmButton = {
                    OutlinedButton(
                        onClick = {
                            onEditPlaylist(selectedCategory!!)
                            showDialog = false
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Edit")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            playlistViewModel.deletePlaylist(selectedCategory!!.id)
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text("Delete")
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
fun MusicCategoryItem(
    category: MusicCategory,
    onClick: () -> Unit,
    onLongPress: (() -> Unit)? = null,
    isCustomPlaylist: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() }
        ) {
            // Background image with overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
            ) {
                if (category.imageUri != null) {
                    Image(
                        painter = rememberImagePainter(category.imageUri),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Image(
                        painter = painterResource(id = category.imageResId),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Stronger gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.5f)
                                ),
                                startY = 0.4f
                            )
                        )
                )
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top-right menu button
                if (isCustomPlaylist) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        IconButton(
                            onClick = { onLongPress?.invoke() },
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                    shape = CircleShape
                                ),
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Text content
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.8f),
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${category.songs.size} songs",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.7f),
                                offset = Offset(1f, 1f),
                                blurRadius = 2f
                            )
                        )
                    )
                }
            }
        }
    }
}


@Composable
fun AddYourBeatCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 6.dp,
        shadowElevation = 10.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() }
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                )
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(6.dp, CircleShape)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Beat",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Add Your Beat",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.3f),
                            offset = Offset(1f, 1f),
                            blurRadius = 2f
                        )
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Create playlist",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                    )
                )
            }
        }
    }
}
