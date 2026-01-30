package com.example.unick

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.unick.view.DashboardActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SchoolDashboardInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<DashboardActivity>()

    @Test
    fun dashboardScreen_isDisplayed() {
        // Assert that dashboard greeting text is visible
        composeRule
            .onNodeWithText("Explore the best colleges in Nepal", substring = true)
            .assertIsDisplayed()
    }
}
