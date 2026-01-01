package com.example.unick.repository.cloudinary

import android.net.Uri

interface CloudinaryRepo {
    suspend fun uploadImage(imageUri: Uri, folder: String): Result<String>
}
