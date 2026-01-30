package com.example.unick

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.unick.view.DashboardActivity
import com.example.unick.view.SchoolDetailActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SchoolProfileInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<DashboardActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun clickingSchoolCard_opensSchoolProfile() {
        // Click a visible school card by name (adjust name if needed)
        composeRule
            .onNodeWithText("College", substring = true)
            .performClick()

        // Verify SchoolDetailActivity is opened
        Intents.intended(hasComponent(SchoolDetailActivity::class.java.name))
    }
}
