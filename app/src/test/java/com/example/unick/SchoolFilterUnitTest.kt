package com.example.unick

import com.example.unick.model.SchoolForm
import org.junit.Assert.assertEquals
import org.junit.Test

class SchoolFilterUnitTest {

    @Test
    fun filterByLocation_returnsOnlyMatchingSchools() {
        // Arrange (fake data)
        val schools = listOf(
            SchoolForm(
                uid = "1",
                schoolName = "St. Xavier's College",
                location = "Kathmandu",
                tuitionFee = "200000",
                verified = true
            ),
            SchoolForm(
                uid = "2",
                schoolName = "Everest College",
                location = "Pokhara",
                tuitionFee = "150000",
                verified = true
            )
        )

        // Act (apply location filter)
        val filtered = schools.filter {
            it.location.contains("Kathmandu", ignoreCase = true)
        }

        // Assert (verify result)
        assertEquals(1, filtered.size)
        assertEquals("St. Xavier's College", filtered[0].schoolName)
    }

    @Test
    fun filterByFeeRange_underThreeLakhs_returnsCorrectSchools() {
        // Arrange
        val schools = listOf(
            SchoolForm(
                uid = "1",
                schoolName = "Budget College",
                location = "Kathmandu",
                tuitionFee = "100000",
                verified = true
            ),
            SchoolForm(
                uid = "2",
                schoolName = "Expensive College",
                location = "Kathmandu",
                tuitionFee = "600000",
                verified = true
            )
        )

        // Act (fee filter logic)
        val filtered = schools.filter {
            val fee = it.tuitionFee.filter { ch -> ch.isDigit() }.toIntOrNull() ?: 0
            fee in 1..300000
        }

        // Assert
        assertEquals(1, filtered.size)
        assertEquals("Budget College", filtered[0].schoolName)
    }
}
