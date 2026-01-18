package com.example.unick.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Interceptor to add the Authorization header for OpenAI
    class AuthInterceptor(private val apiKey: String) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $apiKey")
                .build()
            return chain.proceed(request)
        }
    }

    // We need a separate client for OpenAI to add the auth header
    private fun createOpenAiClient(apiKey: String): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(apiKey))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    // Standard client for non-authenticated APIs
    private val standardClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val nominatimRetrofit = Retrofit.Builder()
        .baseUrl("https://nominatim.openstreetmap.org/")
        .client(standardClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val overpassRetrofit = Retrofit.Builder()
        .baseUrl("https://overpass-api.de/")
        .client(standardClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Function to get a Retrofit instance for Groq (Free AI)
    fun getGroqRetrofit(apiKey: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.groq.com/")
            .client(createOpenAiClient(apiKey))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val nominatimApi: NominatimService by lazy {
        nominatimRetrofit.create(NominatimService::class.java)
    }

    val overpassApi: OverpassService by lazy {
        overpassRetrofit.create(OverpassService::class.java)
    }
}
