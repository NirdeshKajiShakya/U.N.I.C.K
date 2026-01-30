package com.example.unick

import com.example.unick.model.SchoolForm
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchFilterTest {

    @Test
    fun searchBySchoolName_returnsMatchingSchools() {
        // ---------- GIVEN (test data) ----------
        val schools = listOf(
            SchoolForm(
                uid = "1",
                schoolName = "St. Xavier's College",
                location = "Kathmandu"
            ),
            SchoolForm(
                uid = "2",
                schoolName = "Budhanilkantha School",
                location = "Kathmandu"
            ),
            SchoolForm(
                uid = "3",
                schoolName = "Xavier International",
                location = "Lalitpur"
            )
        )

        val query = "Xavier"

        // ---------- WHEN (logic under test) ----------
        val result = schools.filter {
            it.schoolName.contains(query, ignoreCase = true)
        }

        // ---------- THEN (assertions) ----------
        assertEquals(2, result.size)
        assertEquals("St. Xavier's College", result[0].schoolName)
        assertEquals("Xavier International", result[1].schoolName)
    }
}
