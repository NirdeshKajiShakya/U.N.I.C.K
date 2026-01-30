package com.example.unick

import com.example.unick.model.SchoolForm
import org.junit.Assert.assertEquals
import org.junit.Test

class SchoolDashboardUnitTest {

    @Test
    fun loggedInSchoolUser_seesOnlyOwnSchool() {
        // Arrange (fake logged-in school UID)
        val currentUid = "school_123"

        val schools = listOf(
            SchoolForm(
                uid = "school_123",
                schoolName = "My School",
                location = "Kathmandu",
                verified = true
            ),
            SchoolForm(
                uid = "school_456",
                schoolName = "Other School",
                location = "Pokhara",
                verified = true
            )
        )

        // Act (dashboard logic)
        val mySchool = schools.find { it.uid == currentUid }

        // Assert
        assertEquals("My School", mySchool?.schoolName)
    }
}
