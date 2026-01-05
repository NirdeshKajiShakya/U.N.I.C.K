package com.example.unick.repository.cloudinary

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CloudinaryClient {

    private const val BASE_URL = "https://api.cloudinary.com/"

    private val logger = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    val api: CloudinaryApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CloudinaryApi::class.java)
    }
}
