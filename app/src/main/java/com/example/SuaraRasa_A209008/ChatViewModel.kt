package com.example.SuaraRasa_A209008

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.SuaraRasa_A209008.BuildConfig.apiKey
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.plus
import kotlin.collections.toTypedArray
import kotlin.let
import kotlin.run
import kotlin.text.isNotBlank


data class ChatMessage(
    val text: String,
    val isUser: Boolean
)


sealed interface ChatScreenUiState {
    data object Initial : ChatScreenUiState
    data object Loading : ChatScreenUiState
    data class Success(val messages: List<ChatMessage>) : ChatScreenUiState
    data class Error(val errorMessage: String) : ChatScreenUiState
}

class ChatViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<ChatScreenUiState>
    val uiState: StateFlow<ChatScreenUiState>

    private var generativeModel: GenerativeModel
    private val chatHistory = mutableListOf<Content>()


    init {
        val systemBehavior = ChatBotConfig.DEFAULT_SYSTEM_BEHAVIOR


        generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = apiKey,
            systemInstruction = if (systemBehavior.isNotBlank()) {
                content { text(systemBehavior) }
            } else {
                null
            }
        )
        val initialBotMessageText = "Hello! my name is Ai-man,your AI assistant. How are you feeling today?"
        val initialBotMessage = ChatMessage(initialBotMessageText, isUser = false)

        chatHistory.add(content("model") { text(initialBotMessageText) })


        _uiState = MutableStateFlow(ChatScreenUiState.Success(listOf(initialBotMessage)))
        uiState = _uiState.asStateFlow()
    }

    fun sendMessage(userPrompt: String) {
            val currentUserMessages =
                (_uiState.value as? ChatScreenUiState.Success)?.messages ?: emptyList()
            val userMessage = ChatMessage(userPrompt, isUser = true)
            _uiState.value = ChatScreenUiState.Success(currentUserMessages + userMessage)

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    chatHistory.add(content("user") { text(userPrompt) })

                    val response = generativeModel.generateContent(*chatHistory.toTypedArray())

                    response.text?.let { outputContent ->
                        chatHistory.add(content("model") { text(outputContent) })
                        val updatedMessages =
                            (_uiState.value as? ChatScreenUiState.Success)?.messages ?: listOf(
                                userMessage
                            )
                        val botMessage = ChatMessage(outputContent, isUser = false)
                        _uiState.value = ChatScreenUiState.Success(updatedMessages + botMessage)
                    } ?: run {

                        val messagesOnError = (_uiState.value as? ChatScreenUiState.Success)?.messages ?: listOf(
                            userMessage
                        )

                        _uiState.value = ChatScreenUiState.Error("Received an empty response from the AI.")
                    }
                } catch (e: Exception) {

                    val messagesOnError = (_uiState.value as? ChatScreenUiState.Success)?.messages ?: listOf(
                        userMessage
                    )

                    _uiState.value = ChatScreenUiState.Error(e.localizedMessage ?: "An unknown error occurred")
                }
            }
    }


        fun clearChat() {
            chatHistory.clear()

            val initialBotMessageText = "Hi, my name is Aiman, how are you feeling today?"
            val initialBotMessage = ChatMessage(initialBotMessageText, isUser = false)

            chatHistory.add(content("model") { text(initialBotMessageText) })
            _uiState.value = ChatScreenUiState.Success(listOf(initialBotMessage))
        }
}