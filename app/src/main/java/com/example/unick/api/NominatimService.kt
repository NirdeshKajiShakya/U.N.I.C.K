package com.example.unick.api

import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimService {
    @GET("search?format=json")
    suspend fun search(@Query("q") query: String): List<NominatimResponse>
}

data class NominatimResponse(
    val lat: String,
    val lon: String,
    val display_name: String
)
