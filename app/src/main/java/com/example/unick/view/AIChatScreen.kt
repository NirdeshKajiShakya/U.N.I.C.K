package com.example.unick.view

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unick.viewmodel.AIChatViewModel
import com.example.unick.viewmodel.ChatMessage
import kotlinx.coroutines.launch

// ---------------- MAIN SCREEN ----------------
@Composable
fun AiChatScreen(
    onBackPressed: () -> Unit = {},
    viewModel: AIChatViewModel = viewModel()
) {
    var inputText by rememberSaveable { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    // Show error snackbar if any
    error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            // Error is already shown in chat, just clear it
            viewModel.clearError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7FA))
    ) {
        // Header with clear button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "UNICK AI Assistant",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF0A73FF)
            )

            IconButton(onClick = { viewModel.clearConversation() }) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Clear conversation",
                    tint = Color(0xFF0A73FF)
                )
            }
        }

        Divider(color = Color.LightGray.copy(alpha = 0.3f))

        // Chat messages area
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Welcome message
            if (messages.isEmpty()) {
                item {
                    WelcomeMessage()
                }
            }

            items(messages, key = { it.id }) { msg ->
                if (msg.isTyping) {
                    TypingBubble()
                } else {
                    ChatBubble(msg)
                }
            }
        }

        // Input area
        InputArea(
            inputText = inputText,
            onInputChange = { inputText = it },
            onSend = {
                val message = inputText.trim()
                if (message.isNotBlank()) {
                    inputText = ""
                    viewModel.sendMessage(message)
                }
            },
            isLoading = isLoading
        )
    }
}

@Composable
fun WelcomeMessage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "👋 Hi! I'm UNICK AI",
            fontSize = 20.sp,
            color = Color(0xFF0A73FF),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "I can help you find the perfect school!",
            fontSize = 16.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Quick suggestions
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Try asking:",
                fontSize = 14.sp,
                color = Color.Gray
            )
            SuggestionChip("Find schools in Kathmandu")
            SuggestionChip("Show me schools with Grade 10")
            SuggestionChip("Which schools have scholarships?")
            SuggestionChip("Compare schools with A-Levels")
        }
    }
}

@Composable
fun SuggestionChip(text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFE8F4FF),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "💬 $text",
            modifier = Modifier.padding(12.dp),
            fontSize = 14.sp,
            color = Color(0xFF0A73FF)
        )
    }
}

@Composable
fun InputArea(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = onInputChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Ask about schools...") },
            maxLines = 3,
            enabled = !isLoading,
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White
            )
        )

        Spacer(Modifier.width(4.dp))

        IconButton(
            onClick = onSend,
            enabled = inputText.isNotBlank() && !isLoading
        ) {
            Icon(
                Icons.Default.Send,
                contentDescription = "Send",
                tint = if (inputText.isNotBlank() && !isLoading)
                    Color(0xFF0A73FF) else Color.Gray
            )
        }
    }
}

// ---------------- CHAT BUBBLE ----------------
@Composable
fun ChatBubble(message: ChatMessage) {
    val bg = if (message.isUser) Color(0xFF0A73FF) else Color.White
    val textColor = if (message.isUser) Color.White else Color.Black
    val alignment = if (message.isUser) Arrangement.End else Arrangement.Start

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isUser) 16.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 16.dp
                    )
                )
                .background(bg)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.text,
                color = textColor,
                fontSize = 15.sp
            )
        }
    }
}

// ---------------- TYPING ANIMATION ----------------
@Composable
fun TypingBubble() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val dotCount by infiniteTransition.animateValue(
        initialValue = 0,
        targetValue = 3,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dots"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp))
                .background(Color.White)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = "Thinking${".".repeat(dotCount)}",
                fontSize = 15.sp,
                color = Color.Gray
            )
        }
    }
}

// ---------------- PREVIEW ----------------
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AiChatScreenPreview() {
    MaterialTheme {
        AiChatScreen()
    }
}

