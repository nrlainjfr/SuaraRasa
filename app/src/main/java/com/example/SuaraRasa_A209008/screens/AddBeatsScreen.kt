package com.example.SuaraRasa_A209008.screens

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.SuaraRasa_A209008.R
import com.example.SuaraRasa_A209008.data.MusicCategory
import com.example.SuaraRasa_A209008.data.Song
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBeatsScreen(
    onBackClick: () -> Unit,
    onConfirmClick: (MusicCategory) -> Unit,
    playlistToEdit: MusicCategory? = null,
    modifier: Modifier = Modifier
) {
    var topBarTitle by remember { mutableStateOf(playlistToEdit?.name ?: "Your Beats") }
    var playlistImageUri by remember { mutableStateOf<Uri?>(playlistToEdit?.imageUri) }
    var addedSongs by remember { mutableStateOf(playlistToEdit?.songs ?: emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("recommended") }
    var isEditingTitle by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val recommendedSongs = listOf(
        Song("Spring Day", "BTS", R.drawable.springday, R.raw.springday),
        Song("Moon", "Jin", R.drawable.moon, R.raw.moon),
        Song("Epiphany", "Jin", R.drawable.epiphany, R.raw.epiphany),
        Song("Memories", "Maroon 5", R.drawable.memories, R.raw.memories),
        Song("Night Changes", "One Direction", R.drawable.night, R.raw.nightchanges),
        Song("Highway 1009", "Enhypen", R.drawable.highway, R.raw.highway1009),
        Song("Magic Shop", "BTS", R.drawable.magicshop, R.raw.magicshop),
        Song("Drunk Text", "Henry Moodie", R.drawable.drunktext, R.raw.drunktext),
        Song("To My Youth", "bol4", R.drawable.youth, R.raw.youth),
        Song("How Do I Say Goodbye", "Dean Lewis", R.drawable.goodbye, R.raw.goodbye),
        Song("Blue", "Yung Kai", R.drawable.blue, R.raw.blue),
    )

    val filteredSongs = recommendedSongs.filter {
        it.title.contains(searchQuery, ignoreCase = true) || it.artist.contains(searchQuery, ignoreCase = true)
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = { uris ->
            val newSongs = uris.mapNotNull { uri ->
                // Take persistable permission
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )

                val name = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    cursor.moveToFirst()
                    cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                } ?: uri.lastPathSegment ?: "Unknown"

                Song(
                    title = name,
                    artist = "From Device",
                    imageResId = R.drawable.ic_default_song,
                    audioResId = -1,
                    audioUri = uri
                )
            }

            addedSongs = addedSongs + newSongs
        }
    )


    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                // Persist permission
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                playlistImageUri = it
            }
        }
    )


    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                isSearching = false
                searchQuery = ""
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Search bar in the bottom sheet
                    if (isSearching) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search songs...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Clear",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TabItem("Recommended", selectedTab == "recommended") { selectedTab = "recommended" }
                        TabItem("From Device", selectedTab == "device") { selectedTab = "device" }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (selectedTab) {
                        "recommended" -> {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(filteredSongs) { song ->
                                    SongItem(
                                        song = song,
                                        onAddSong = {
                                            addedSongs = addedSongs + song
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Beat successfully added")
                                            }
                                        },
                                        showAdd = true
                                    )
                                }
                            }
                        }
                        "device" -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                OutlinedButton(
                                    onClick = { filePickerLauncher.launch(arrayOf("audio/*")) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                ) {
                                    Icon(Icons.Default.AudioFile, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Select Audio Files")
                                }
                            }
                        }
                    }
                }

                // Snackbar in sheet
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    when {
                        isSearching -> TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search songs...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedTextColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Clear",
                                            tint = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                            }
                        )
                        isEditingTitle -> BasicTextField(
                            value = topBarTitle,
                            onValueChange = { topBarTitle = it },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.headlineMedium.copy(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        else -> Text(
                            topBarTitle,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isSearching) {
                            isSearching = false
                            searchQuery = ""
                            showBottomSheet = false
                        } else {
                            onBackClick()
                        }
                    }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    if (!isSearching) {
                        IconButton(onClick = { isEditingTitle = !isEditingTitle }) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Edit Title",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        IconButton(onClick = {
                            isSearching = !isSearching
                            showBottomSheet = isSearching
                            if (!isSearching) {
                                searchQuery = ""
                            }
                        }) {
                            Icon(
                                Icons.Filled.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
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
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Playlist cover
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable { imagePickerLauncher.launch("image/*") }
                    .shadow(8.dp, RoundedCornerShape(20.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    if (playlistImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(playlistImageUri),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_default_song),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Gradient overlay like in HomeScreen
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

                Text(
                    "Tap to change cover",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.7f),
                            offset = Offset(1f, 1f),
                            blurRadius = 2f
                        )
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Your Beats (${addedSongs.size})",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
            ) {
                items(addedSongs) { song ->
                    SongItem(
                        song = song,
                        onRemoveSong = { addedSongs = addedSongs - song },
                        showRemove = true
                    )
                }
            }

            // Add Songs button
            Button(
                onClick = {
                    showBottomSheet = true
                    isSearching = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Songs", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Confirm button
            Button(
                onClick = {
                    if (addedSongs.isNotEmpty()) {
                        onConfirmClick(
                            MusicCategory(
                                name = topBarTitle.ifBlank { "My Playlist" },
                                imageUriString = playlistImageUri?.toString(),
                                songs = addedSongs
                            )
                        )

                    }
                },
                enabled = addedSongs.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    if (playlistToEdit != null) "UPDATE (${addedSongs.size})"
                    else "CONFIRM (${addedSongs.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun TabItem(label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        if (selected) {
            Divider(
                color = MaterialTheme.colorScheme.primary,
                thickness = 2.dp,
                modifier = Modifier.width(40.dp).padding(top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongItem(
    song: Song,
    onAddSong: (() -> Unit)? = null,
    onRemoveSong: (() -> Unit)? = null,
    showAdd: Boolean = false,
    showRemove: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }
    var isPlaying by remember { mutableStateOf(false) }

    fun toggleSong() {
        if (isPlaying) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            isPlaying = false
        } else {
            mediaPlayer?.release()
            mediaPlayer = if (song.audioUri != null) {
                MediaPlayer().apply {
                    setDataSource(context, song.audioUri)
                    prepare()
                    start()
                }
            } else {
                MediaPlayer.create(context, song.audioResId).apply {
                    start()
                }
            }
            mediaPlayer?.setOnCompletionListener {
                isPlaying = false
            }
            isPlaying = true
        }
    }

    DisposableEffect(song) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { toggleSong() }
                ) {
                    Image(
                        painter = if (song.imageResId > 0) {
                            painterResource(id = song.imageResId)
                        } else {
                            painterResource(id = R.drawable.ic_default_song)
                        },
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    if (isPlaying) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_pause),
                                contentDescription = "Pause",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                }
            }

            if (showAdd && onAddSong != null) {
                IconButton(
                    onClick = onAddSong,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add Song",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (showRemove && onRemoveSong != null) {
                IconButton(
                    onClick = onRemoveSong,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Remove Song",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}