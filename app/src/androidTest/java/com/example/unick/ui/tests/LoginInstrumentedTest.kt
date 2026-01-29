package com.example.unick

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.unick.view.DashboardActivity
import com.example.unick.view.UserLoginActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<UserLoginActivity>()


    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testSuccessfulLogin_navigatesToDashboard() {
        // Type email
        composeRule.onNodeWithTag("email")
            .performTextInput("ram@gmail.com")

        // Type password
        composeRule.onNodeWithTag("password")
            .performTextInput("password")

        // Click login
        composeRule.onNodeWithTag("login")
            .performClick()

        // Verify navigation
        Intents.intended(hasComponent(DashboardActivity::class.java.name))
    }

    @Test
    fun testEmptyFields_showsError() {
        composeRule.onNodeWithTag("login").performClick()

        composeRule.onNodeWithTag("errorText")
            .assertIsDisplayed()
    }

    @Test
    fun testInvalidEmail_showsError() {
        composeRule.onNodeWithTag("email").performTextInput("invalidemail")
        composeRule.onNodeWithTag("password").performTextInput("123456")
        composeRule.onNodeWithTag("login").performClick()

        composeRule.onNodeWithText("Please enter a valid email")
            .assertIsDisplayed()
    }



}
