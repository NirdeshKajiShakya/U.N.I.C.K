package com.example.unick.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.unick.view.SchoolDataShortlist

class ShortlistViewModel : ViewModel() {
    // UI State
    private val _schools = mutableStateOf<List<SchoolDataShortlist>>(emptyList())
    val schools: State<List<SchoolDataShortlist>> = _schools

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    init {
        loadShortlistedSchools()
    }

    private fun loadShortlistedSchools() {
        try {
            _isLoading.value = true
            // TEMP: Fake data (later replace with Firebase / API)
            _schools.value = getSampleSchools()
            _isLoading.value = false
        } catch (e: Exception) {
            _error.value = e.message
            _isLoading.value = false
        }
    }

    private fun getSampleSchools(): List<SchoolDataShortlist> {
        return listOf(
            SchoolDataShortlist(
                id = "1",
                name = "St. Xavier's College",
                type = "Maitighar • +2 Science/A-Levels",
                distance = "0.8 km",
                rating = "4.9",
                match = "98% match",
                imageUrl = "https://images.unsplash.com/photo-1562774053-701939374585?w=400",
                isFavorited = true
            ),
            SchoolDataShortlist(
                id = "2",
                name = "Budhanilkantha School",
                type = "Kathmandu • National Curriculum",
                distance = "5.4 km",
                rating = "4.8",
                match = "95% match",
                imageUrl = "https://images.unsplash.com/photo-1541339907198-e08756dedf3f?w=400",
                isFavorited = true
            ),
            SchoolDataShortlist(
                id = "3",
                name = "Ullens School",
                type = "Khumaltar • IB Diploma Program",
                distance = "3.2 km",
                rating = "4.7",
                match = "92% match",
                imageUrl = "https://images.unsplash.com/photo-1498243691581-b145c3f54a5a?w=400",
                isFavorited = true
            ),
            SchoolDataShortlist(
                id = "4",
                name = "Little Angels' School",
                type = "Hattiban • School to Bachelors",
                distance = "4.5 km",
                rating = "4.7",
                match = "90% rank",
                imageUrl = "https://images.unsplash.com/photo-1509062522246-3755977927d7?w=400",
                isFavorited = true
            ),
            SchoolDataShortlist(
                id = "5",
                name = "Trinity International",
                type = "Dillibazar • +2 & A-Levels",
                distance = "1.1 km",
                rating = "4.6",
                match = "88% rank",
                imageUrl = "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=400",
                isFavorited = true
            ),
            SchoolDataShortlist(
                id = "6",
                name = "Rato Bangala School",
                type = "Patan • A-Levels Center",
                distance = "2.9 km",
                rating = "4.8",
                match = "87% rank",
                imageUrl = "https://images.unsplash.com/photo-1546410531-bb4caa6b424d?w=400",
                isFavorited = true
            ),
            SchoolDataShortlist(
                id = "7",
                name = "Gandaki Boarding",
                type = "Pokhara • National Curriculum",
                distance = "200 km",
                rating = "4.7",
                match = "Elite",
                imageUrl = "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=400",
                isFavorited = true
            ),
            SchoolDataShortlist(
                id = "8",
                name = "Siddhartha Boarding",
                type = "Butwal • Science/Management",
                distance = "260 km",
                rating = "4.5",
                match = "Top Pick",
                imageUrl = "https://images.unsplash.com/photo-1571260899304-425eee4c7efc?w=400",
                isFavorited = true
            ),
            SchoolDataShortlist(
                id = "9",
                name = "SOS Hermann Gmeiner",
                type = "Pokhara • Science Streams",
                distance = "198 km",
                rating = "4.6",
                match = "Scholarship",
                imageUrl = "https://images.unsplash.com/photo-1580582932707-520aed937b7b?w=400",
                isFavorited = true
            )
        )
    }

    // Remove school from shortlist by ID
    fun removeFromShortlist(schoolId: String) {
        _schools.value = _schools.value.filter { it.id != schoolId }
        // TODO: Update backend/database when implemented
    }

    // Toggle favorite status
    fun toggleFavorite(schoolId: String) {
        _schools.value = _schools.value.map { school ->
            if (school.id == schoolId) {
                school.copy(isFavorited = !school.isFavorited)
            } else {
                school
            }
        }
        // TODO: Update backend/database when implemented
    }

    // Refresh shortlist
    fun refresh() {
        loadShortlistedSchools()
    }
}