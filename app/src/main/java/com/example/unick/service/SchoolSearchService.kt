package com.example.unick.service

import com.example.unick.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SchoolSearchService(
    private val groqApiKey: String
) {
    private val groqApi = RetrofitClient.getGroqRetrofit(groqApiKey).create(GroqService::class.java)

    suspend fun searchSchools(locationName: String): String = withContext(Dispatchers.IO) {
        try {
            // Use Groq AI to answer school-related questions directly
            val prompt = """
                Please provide information about schools in $locationName.
                List some well-known schools, colleges, or educational institutions in that area.
                Include a brief description if possible.
                Keep the response concise and helpful.
            """.trimIndent()

            return@withContext chatWithGroq(prompt)

        } catch (e: Exception) {
            return@withContext "Error searching for schools: ${e.message}. Please try again."
        }
    }
    
    suspend fun chatWithGroq(userMessage: String, context: String = ""): String = withContext(Dispatchers.IO) {
        try {
            val messages = mutableListOf<LegacyGroqMessage>()

            if (context.isNotEmpty()) {
                messages.add(LegacyGroqMessage("system", context))
            }
            
            messages.add(LegacyGroqMessage("user", userMessage))

            val request = LegacyGroqRequest(
                model = "llama-3.3-70b-versatile",
                messages = messages
            )
            
            val response = groqApi.createChatCompletion(request)

            if (response.choices.isEmpty()) {
                return@withContext "I couldn't generate a response. Please try again."
            }
            
            return@withContext response.choices[0].message.content

        } catch (e: Exception) {
            return@withContext "Error communicating with AI: ${e.message}"
        }
    }

    fun isSchoolSearchQuery(query: String): Boolean {
        val keywords = listOf(
            "\\bschool\\b", "\\bschools\\b", "\\beducation\\b", "\\bacademy\\b", 
            "\\bacademies\\b", "\\bcollege\\b", "\\bcolleges\\b",
            "\\buniversity\\b", "\\buniversities\\b", "\\binstitute\\b", "\\binstitutes\\b", 
            "\\blearning\\s+center\\b", "\\blearning\\s+centers\\b"
        )
        val locationKeywords = listOf("near", "in", "around", "at", "close to")
        
        val lowerQuery = query.lowercase()
        val hasSchoolKeyword = keywords.any { Regex(it).containsMatchIn(lowerQuery) }
        val hasLocationKeyword = locationKeywords.any { lowerQuery.contains(it) }
        
        return hasSchoolKeyword && hasLocationKeyword
    }
    
    fun extractLocationFromQuery(query: String): String {
        // Simple extraction - looks for location after keywords
        val keywords = listOf("near", "in", "around", "at", "close to")
        val lowerQuery = query.lowercase()
        
        for (keyword in keywords) {
            val index = lowerQuery.indexOf(keyword)
            if (index != -1) {
                val afterKeyword = query.substring(index + keyword.length).trim()
                // Remove common trailing words
                return afterKeyword
                    .replace(Regex("\\?.*$"), "")
                    .replace(Regex("please.*$", RegexOption.IGNORE_CASE), "")
                    .trim()
            }
        }
        
        return query.trim()
    }
}
