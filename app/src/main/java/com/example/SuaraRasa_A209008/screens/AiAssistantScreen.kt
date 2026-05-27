package com.example.SuaraRasa_A209008.screens // Atau package yang sesuai di proyek SuaraRasa Anda, misal: com.example.suararasa.features.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.*
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.SuaraRasa_A209008.ChatViewModel
import com.example.SuaraRasa_A209008.ChatScreenUiState
import com.example.SuaraRasa_A209008.ChatMessage
import com.example.SuaraRasa_A209008.navigation.AppDestinations
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight
import com.example.SuaraRasa_A209008.BottomNavigationBar
import com.example.SuaraRasa_A209008.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    navController: NavController,
    chatViewModel: ChatViewModel = viewModel()
) {
    val uiState by chatViewModel.uiState.collectAsState()
    var userInput by remember { mutableStateOf("") }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Chat with Ai-man",
                        fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null &&
                        currentRoute != AppDestinations.HOME_ROUTE
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "back",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(
                        onClick = { chatViewModel.clearChat() },
                        enabled = uiState !is ChatScreenUiState.Loading &&
                                (uiState as? ChatScreenUiState.Success)?.messages?.size?.let { it > 1 } == true
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Clear Chat",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },

        bottomBar = {
            // Panggil Composable BottomNavigationBar standar aplikasi Anda
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding) // Terapkan innerPadding dari Scaffold
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
    LazyColumn(
        modifier = Modifier.weight(1f),
        reverseLayout = true
    ) {
        when (val currentState = uiState) {
            is ChatScreenUiState.Initial -> {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Tambahkan gambar profil AI
                        Image(
                            painter = painterResource(id = R.drawable.ai_profile), // Ganti dengan resource gambar Anda
                            contentDescription = "AI Profile",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Hello! my name is Ai-man,your AI assistant. How are you feeling today?",
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            is ChatScreenUiState.Loading -> {
                val currentMessages = (uiState as? ChatScreenUiState.Success)?.messages ?: emptyList()
                if (currentMessages.isNotEmpty()) {
                    items(currentMessages.reversed()) { message ->
                        MessageBubble(message)
                    }
                }
                item {
                    // Bubble untuk animasi typing AI
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                    ) {
                        // Gambar profil AI
                        Image(
                            painter = painterResource(id = R.drawable.ai_profile), // Ganti dengan resource gambar Anda
                            contentDescription = "AI Profile",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Bubble chat dengan animasi typing
                        TypingAnimationBubble()
                    }
                }
            }
            is ChatScreenUiState.Success -> {
                items(currentState.messages.reversed()) { message ->
                    MessageBubble(message)
                }
            }
            is ChatScreenUiState.Error -> {
                item {
                    Text(
                        "Error: ${currentState.errorMessage}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        label = { Text("Type your message...") },
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp)),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        singleLine = false,
                        maxLines = 3
                    )

                    Spacer(Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (userInput.isNotBlank()) {
                                chatViewModel.sendMessage(userInput)
                                userInput = ""
                            }
                        },
                        enabled = userInput.isNotBlank() && uiState !is ChatScreenUiState.Loading,
                        modifier = Modifier
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Send",
                            tint = if (userInput.isNotBlank()) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isUser) {
            // Tambahkan gambar profil AI untuk pesan dari AI
            Image(
                painter = painterResource(id = R.drawable.ai_profile), // Ganti dengan resource gambar Anda
                contentDescription = "AI Profile",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .align(Alignment.Bottom)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (message.isUser)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.secondaryContainer
            ),
            shape = RoundedCornerShape(
                topStart = if (message.isUser) 16.dp else 4.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                style = MaterialTheme.typography.bodyLarge,
                color = if (message.isUser)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun TypingAnimationBubble() {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0.3f at 0 with LinearEasing
                1f at 400 with LinearEasing
                0.3f at 800 with LinearEasing
                0.3f at 1200 with LinearEasing
            },
            repeatMode = RepeatMode.Restart
        )
    )

    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0.3f at 200 with LinearEasing
                1f at 600 with LinearEasing
                0.3f at 1000 with LinearEasing
                0.3f at 1200 with LinearEasing
            },
            repeatMode = RepeatMode.Restart
        )
    )

    val alpha3 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0.3f at 400 with LinearEasing
                1f at 800 with LinearEasing
                0.3f at 1200 with LinearEasing
            },
            repeatMode = RepeatMode.Restart
        )
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Titik-titik animasi typing
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = alpha1))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = alpha2))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = alpha3))
            )
        }
    }
}
