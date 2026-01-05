package com.example.unick.view

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ---------------- DATA MODEL ----------------
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isTyping: Boolean = false,
    val id: Long = System.nanoTime() // Changed to nanoTime for better uniqueness
)

// ---------------- AI LOGIC ----------------
fun getAiReply(input: String): String {
    val msg = input.lowercase().trim()
    return when {
        msg in listOf("hi", "hello", "hey", "hii", "helo") -> "Hello! 👋 How can I help you today?"
        msg.contains("private school") -> "Sure 😊 Which grade are you looking for?"
        msg.contains("grade 9") || msg.contains("grade 10") ||
                msg.contains("grade 11") || msg.contains("grade 12") ->
            "Got it 👍 Do you want schools near you or in a specific area?"
        msg.contains("map") -> "I can show the schools on the map 🗺️"
        msg.contains("thank") -> "You're welcome 💙"
        msg.contains("bye") -> "Goodbye! Feel free to come back anytime! 👋"
        else -> "I can help you search schools by grade, tuition, or location 📚"
    }
}

// ---------------- MAIN SCREEN ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiChatScreen(
    onBackPressed: () -> Unit = {}
) {
    var inputText by rememberSaveable { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Handle back press
    BackHandler {
        onBackPressed()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("AI Chat", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF0A73FF),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF6F7FA))
        ) {
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "👋 Hi! Ask me about schools!",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
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
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Say hi 👋 or ask about schools") },
                    maxLines = 3,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(Modifier.width(4.dp))

                IconButton(
                    onClick = {
                        val userMsg = inputText.trim()
                        if (userMsg.isNotBlank()) {
                            inputText = ""

                            // Add user message
                            messages.add(ChatMessage(userMsg, isUser = true))

                            // Add typing indicator
                            val typingMsg = ChatMessage("", isUser = false, isTyping = true)
                            messages.add(typingMsg)

                            // Simulate AI response
                            coroutineScope.launch {
                                delay(1200)
                                messages.remove(typingMsg)
                                messages.add(
                                    ChatMessage(
                                        text = getAiReply(userMsg),
                                        isUser = false
                                    )
                                )
                            }
                        }
                    },
                    enabled = inputText.isNotBlank()
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank()) Color(0xFF0A73FF) else Color.Gray
                    )
                }
            }
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
                text = "Typing${".".repeat(dotCount)}",
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