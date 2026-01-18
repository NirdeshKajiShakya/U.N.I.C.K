package com.example.unick.api

import com.google.gson.annotations.SerializedName

// Gemini API Models
data class GeminiRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)

data class GenerationConfig(
    val temperature: Double = 0.7,
    val maxOutputTokens: Int = 1024
)

data class GeminiResponse(
    val candidates: List<Candidate>
)

data class Candidate(
    val content: Content,
    val finishReason: String?,
    val safetyRatings: List<SafetyRating>?
)

data class SafetyRating(
    val category: String,
    val probability: String
)

// Google Places API Models
data class PlacesSearchResponse(
    val results: List<PlaceResult>,
    val status: String,
    @SerializedName("error_message")
    val errorMessage: String?
)

data class PlaceResult(
    val name: String,
    val vicinity: String,
    val geometry: Geometry,
    val rating: Double?,
    @SerializedName("user_ratings_total")
    val userRatingsTotal: Int?,
    val types: List<String>?
)

data class Geometry(
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)

data class GeocodeResponse(
    val results: List<GeocodeResult>,
    val status: String
)

data class GeocodeResult(
    val geometry: Geometry,
    @SerializedName("formatted_address")
    val formattedAddress: String
)
