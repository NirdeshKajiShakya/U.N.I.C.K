package com.example.unick.repository.cloudinary

import android.content.Context
import android.net.Uri
import com.example.unick.utils.CloudinaryConfig
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CloudinaryRepoImpl(
    private val context: Context
) : CloudinaryRepo {

    override suspend fun uploadImage(imageUri: Uri, folder: String): Result<String> {
        return try {
            val file = uriToTempFile(context, imageUri) ?: return Result.failure(
                IllegalStateException("Could not read image")
            )

            val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, reqFile)

            val presetBody = CloudinaryConfig.UPLOAD_PRESET.toRequestBody("text/plain".toMediaTypeOrNull())
            val folderBody = folder.toRequestBody("text/plain".toMediaTypeOrNull())

            val res = CloudinaryClient.api.uploadImage(
                cloudName = CloudinaryConfig.CLOUD_NAME,
                file = filePart,
                uploadPreset = presetBody,
                folder = folderBody
            )

            val url = res.secure_url ?: res.url
            if (url.isNullOrBlank()) Result.failure(IllegalStateException("Upload ok but URL missing"))
            else Result.success(url)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun uriToTempFile(context: Context, uri: Uri): File? {
        val input = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
        tempFile.outputStream().use { out -> input.use { it.copyTo(out) } }
        return tempFile
    }
}
