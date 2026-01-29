package com.example.unick.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unick.api.GroqApiClient
import com.example.unick.api.GroqChatRequest
import com.example.unick.api.GroqMessage
import com.example.unick.model.SchoolForm
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val isTyping: Boolean = false,
    val id: Long = System.nanoTime()
)

class AIChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val conversationHistory = mutableListOf<GroqMessage>()
    private val schools = mutableListOf<SchoolForm>()

    companion object {
        private const val TAG = "AIChatViewModel"
        private const val SYSTEM_PROMPT = """You are UNICK AI, a helpful assistant for a school search application in Nepal. 

Your role is to help students and parents find the best schools based on their requirements.

Available school information includes:
- School name, location, and contact details
- Grade levels and curriculum (e.g., CBSE, Montessori, A-Levels)
- Tuition fees and admission fees
- Facilities (transport, hostel, scholarships)
- Total students and established year
- Programs offered and extracurricular activities

When users ask about schools:
1. Ask clarifying questions about their preferences (grade, location, budget, curriculum)
2. Search and recommend schools from the database that match their criteria
3. Provide detailed information about specific schools
4. Compare schools if asked
5. Be friendly, concise, and helpful

Always format school information clearly and highlight key details like fees, location, and facilities.
If you don't have specific school data in context, acknowledge that and ask the user to search or filter."""
    }

    init {
        // Add system prompt to conversation
        conversationHistory.add(
            GroqMessage(
                role = "system",
                content = SYSTEM_PROMPT
            )
        )
        // Fetch schools from Firebase
        fetchSchoolsFromFirebase()
    }

    private fun fetchSchoolsFromFirebase() {
        viewModelScope.launch {
            try {
                val database = FirebaseDatabase.getInstance().getReference("schools")
                val snapshot = database.get().await()

                schools.clear()
                for (childSnapshot in snapshot.children) {
                    val school = childSnapshot.getValue(SchoolForm::class.java)
                    school?.let { schools.add(it) }
                }

                Log.d(TAG, "Fetched ${schools.size} schools from Firebase")
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching schools: ${e.message}", e)
            }
        }
    }

    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return

        // Add user message to UI
        _messages.value = _messages.value + ChatMessage(userMessage, isUser = true)

        // Add typing indicator
        val typingMessage = ChatMessage("", isUser = false, isTyping = true)
        _messages.value = _messages.value + typingMessage

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // Add user message to conversation history
                conversationHistory.add(
                    GroqMessage(role = "user", content = userMessage)
                )

                // Build context with school data if relevant
                val contextualizedMessages = buildContextualizedMessages(userMessage)

                // Call Grok API
                val request = GroqChatRequest(
                    messages = contextualizedMessages,
                    temperature = 0.7,
                    maxTokens = 1024
                )

                val response = GroqApiClient.apiService.createChatCompletion(request)
                val aiReply = response.choices.firstOrNull()?.message?.content
                    ?: "I'm sorry, I couldn't generate a response. Please try again."

                // Add AI response to conversation history
                conversationHistory.add(
                    GroqMessage(role = "assistant", content = aiReply)
                )

                // Remove typing indicator and add actual response
                _messages.value = _messages.value.filterNot { it.isTyping }
                _messages.value = _messages.value + ChatMessage(aiReply, isUser = false)

                Log.d(TAG, "AI Response: $aiReply")

            } catch (e: Exception) {
                Log.e(TAG, "Error sending message: ${e.message}", e)
                _error.value = "Failed to get response: ${e.message}"

                // Remove typing indicator and show error
                _messages.value = _messages.value.filterNot { it.isTyping }
                _messages.value = _messages.value + ChatMessage(
                    "Sorry, I encountered an error. Please check your connection and try again.",
                    isUser = false
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun buildContextualizedMessages(userMessage: String): List<GroqMessage> {
        val messages = conversationHistory.toMutableList()

        if (isSchoolSearchQuery(userMessage) && schools.isNotEmpty()) {
            // Filter schools based on query keywords
            val relevantSchools = filterRelevantSchools(userMessage)

            if (relevantSchools.isNotEmpty()) {
                val schoolContext = buildSchoolContext(relevantSchools)
                // Insert context before the user's message
                val lastIndex = messages.size - 1
                messages.add(
                    lastIndex,
                    GroqMessage(
                        role = "system",
                        content = "Here are the schools from our database that might be relevant:\n\n$schoolContext"
                    )
                )
            }
        }

        return messages
    }

    private fun isSchoolSearchQuery(query: String): Boolean {
        // Robust detection using regex patterns
        val keywords = listOf(
            "\\bschool\\b", "\\bschools\\b", "\\beducation\\b", "\\bacademy\\b",
            "\\bacademies\\b", "\\bcollege\\b", "\\bcolleges\\b",
            "\\buniversity\\b", "\\buniversities\\b", "\\binstitute\\b", "\\binstitutes\\b",
            "\\blearning\\s+center\\b", "\\blearning\\s+centers\\b",
            "grade", "tuition", "fee", "location", "curriculum", "scholarship", "admission"
        )
        // Also check for general intent words combined with context
        val intentWords = listOf("find", "search", "recommend", "best", "good", "near")

        val lowerQuery = query.lowercase()
        val hasKeyword = keywords.any { Regex(it).containsMatchIn(lowerQuery) || lowerQuery.contains(it) }
        val hasIntent = intentWords.any { lowerQuery.contains(it) }

        return hasKeyword || (hasIntent && conversationHistory.any { it.role == "assistant" })
    }

    private fun filterRelevantSchools(query: String): List<SchoolForm> {
        val lowerQuery = query.lowercase()

        return schools.filter { school ->
            // Match by location
            (lowerQuery.contains(school.location.lowercase())) ||
            // Match by grade/curriculum
            (lowerQuery.contains(school.curriculum.lowercase())) ||
            // Match by programs
            (lowerQuery.contains(school.programsOffered.lowercase())) ||
            // Match by school name
            (school.schoolName.lowercase().contains(lowerQuery.split(" ").firstOrNull() ?: ""))
        }.take(5) // Limit to 5 most relevant schools
    }

    private fun buildSchoolContext(schools: List<SchoolForm>): String {
        return schools.joinToString("\n\n") { school ->
            buildString {
                append("**${school.schoolName}**\n")
                append("Location: ${school.location}\n")
                if (school.curriculum.isNotBlank()) append("Curriculum: ${school.curriculum}\n")
                if (school.programsOffered.isNotBlank()) append("Programs: ${school.programsOffered}\n")
                if (school.tuitionFee.isNotBlank()) append("Tuition Fee: ${school.tuitionFee}\n")
                if (school.admissionFee.isNotBlank()) append("Admission Fee: ${school.admissionFee}\n")
                append("Facilities: ")
                val facilities = mutableListOf<String>()
                if (school.transportFacility) facilities.add("Transport")
                if (school.hostelFacility) facilities.add("Hostel")
                if (school.scholarshipAvailable) facilities.add("Scholarship")
                if (school.facilities.isNotBlank()) facilities.add(school.facilities)
                append(facilities.joinToString(", ").ifEmpty { "Not specified" })
                append("\n")
                if (school.contactNumber.isNotBlank()) append("Contact: ${school.contactNumber}\n")
                if (school.description.isNotBlank()) append("Description: ${school.description}\n")
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearConversation() {
        _messages.value = emptyList()
        conversationHistory.clear()
        conversationHistory.add(
            GroqMessage(role = "system", content = SYSTEM_PROMPT)
        )
    }
}

