package com.example.unick.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.unick.repository.SchoolGalleryRepo
import com.example.unick.repository.SchoolGalleryRepoImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SchoolGalleryViewModel(
    context: Context
) {
    private val repo: SchoolGalleryRepo = SchoolGalleryRepoImpl()

    var loading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var galleryImageUrls by mutableStateOf<List<String>>(emptyList())
        private set

    fun loadGallery(schoolId: String) {
        if (schoolId.isBlank()) return
        loading = true
        error = null

        CoroutineScope(Dispatchers.IO).launch {
            val res = repo.getGallery(schoolId)
            CoroutineScope(Dispatchers.Main).launch {
                loading = false
                res.onSuccess { list ->
                    galleryImageUrls = list
                }.onFailure { e ->
                    error = e.message ?: "Failed to load gallery"
                }
            }
        }
    }

    fun addImagesToGallery(schoolId: String, newUrls: List<String>) {
        if (schoolId.isBlank() || newUrls.isEmpty()) return
        loading = true
        error = null

        CoroutineScope(Dispatchers.IO).launch {
            val res = repo.addImages(schoolId, newUrls)
            CoroutineScope(Dispatchers.Main).launch {
                loading = false
                res.onSuccess { updated ->
                    galleryImageUrls = updated
                }.onFailure { e ->
                    error = e.message ?: "Failed to update gallery"
                }
            }
        }
    }
}
