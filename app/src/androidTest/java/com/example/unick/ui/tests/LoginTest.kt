package com.example.unick.ui.tests

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.unick.view.UserLoginActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<UserLoginActivity>()

    @Test
    fun successfulLogin_showsWelcome() {
        composeRule.onNodeWithTag("email").performTextInput("test@123gmail.com")
        composeRule.onNodeWithTag("password").performTextInput("test@123")
        composeRule.onNodeWithTag("login").performClick()

        composeRule.onNodeWithText("Welcome").assertIsDisplayed()
    }

    @Test
    fun emptyFields_showError() {
        composeRule.onNodeWithTag("login").performClick()

        composeRule.onNodeWithText("Please fill in all fields")
            .assertIsDisplayed()
    }
}
