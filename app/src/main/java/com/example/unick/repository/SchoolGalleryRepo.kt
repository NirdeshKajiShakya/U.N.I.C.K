package com.example.unick.repository

interface SchoolGalleryRepo {
    suspend fun getGallery(schoolId: String): Result<List<String>>
    suspend fun addImages(schoolId: String, newUrls: List<String>): Result<List<String>>
}
