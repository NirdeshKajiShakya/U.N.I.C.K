package com.example.unick.api

import retrofit2.http.GET
import retrofit2.http.Query

interface OverpassService {
    @GET("api/interpreter")
    suspend fun interpreter(@Query("data") data: String): OverpassResponse
}

data class OverpassResponse(
    val elements: List<Element>
)

data class Element(
    val type: String,
    val id: Long,
    val lat: Double?,
    val lon: Double?,
    val tags: Map<String, String>,
    val center: Center?
)

data class Center(
    val lat: Double,
    val lon: Double
)

