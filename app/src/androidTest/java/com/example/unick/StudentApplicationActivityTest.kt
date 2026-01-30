package com.example.unick.view

import android.content.Intent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertTrue

@RunWith(AndroidJUnit4::class)
class StudentApplicationActivityTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<StudentApplicationActivity>()

    @Before
    fun setup() {
        Intents.init()

        // Launch with schoolId right from the beginning
        val intent = Intent(composeRule.activity.applicationContext, StudentApplicationActivity::class.java)
        intent.putExtra("schoolId", "test_school_12345")

        // Restart activity with our intent
        composeRule.activityScenario.close()
        composeRule.activityScenario = androidx.test.core.app.ActivityScenario.launch(intent)

        composeRule.waitForIdle()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun canReachSubmitButton_afterFillingMinimalForm() {
        composeRule.waitForIdle()

        // Step 1
        composeRule
            .onNodeWithText("Full Name*", useUnmergedTree = true)
            .performTextReplacement("Test Student")

        composeRule
            .onNodeWithText("Gender*", useUnmergedTree = true)
            .performClick()

        composeRule
            .onNodeWithText("Male")
            .performClick()

        composeRule
            .onNodeWithText("Next")
            .performClick()

        // Step 2
        composeRule
            .onNodeWithText("Standard / Class*", useUnmergedTree = true)
            .performTextReplacement("7")

        composeRule
            .onNodeWithText("Next")
            .performClick()

        // Step 3 – minimal parents
        composeRule
            .onNodeWithText("Father's Name*", useUnmergedTree = true)
            .performTextReplacement("Father Name")

        composeRule
            .onNodeWithText("Father's Phone*", useUnmergedTree = true)
            .performTextReplacement("9999999999")

        composeRule
            .onNodeWithText("Next")
            .performClick()

        // Step 4 – address + budget
        composeRule
            .onNodeWithText("Present Address*", useUnmergedTree = true)
            .performTextReplacement("Test Address 123")

        composeRule
            .onNodeWithText("Yearly School Budget (INR)*", useUnmergedTree = true)
            .performTextReplacement("120000")

        // Final check
        composeRule
            .onNodeWithText("Submit")
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun clickingNextWithoutInput_showsErrorMessages() {
        composeRule.waitForIdle()

        composeRule
            .onNodeWithText("Next")
            .performClick()

        // At least one "Required" message should appear
        val errors = composeRule
            .onAllNodesWithText("Required", useUnmergedTree = true)
            .fetchSemanticsNodes()

        assertTrue("No validation errors appeared", errors.isNotEmpty())
    }
}