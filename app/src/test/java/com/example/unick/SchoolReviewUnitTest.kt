package com.example.unick

import com.example.unick.model.SchoolReviewModel
import org.junit.Assert.assertEquals
import org.junit.Test

class SchoolReviewUnitTest {

    @Test
    fun averageRating_isCalculatedCorrectly() {
        // Arrange
        val reviews = listOf(
            SchoolReviewModel(
                id = "1",
                schoolId = "school_1",
                reviewerUid = "user1",
                rating = 5,
                comment = "Excellent",
                createdAt = 1L
            ),
            SchoolReviewModel(
                id = "2",
                schoolId = "school_1",
                reviewerUid = "user2",
                rating = 3,
                comment = "Good",
                createdAt = 2L
            ),
            SchoolReviewModel(
                id = "3",
                schoolId = "school_1",
                reviewerUid = "user3",
                rating = 4,
                comment = "Nice",
                createdAt = 3L
            )
        )

        // Act
        val averageRating = reviews.map { it.rating }.average()

        // Assert
        assertEquals(4.0, averageRating, 0.0)
    }
}
