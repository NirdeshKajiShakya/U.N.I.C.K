package com.example.unick.ui.tests

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.unick.view.AdminDashboardActivity
import com.example.unick.view.DashboardActivity
import com.example.unick.view.SchoolDetailActivity
import com.example.unick.view.UserLoginActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<UserLoginActivity>()

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun loginUser_navigatesToDashboard() {
        composeRule.onNodeWithTag("email").performTextInput("user@test.com")
        composeRule.onNodeWithTag("password").performTextInput("user123")
        composeRule.onNodeWithTag("login").performClick()

        // Assert correct Activity is opened
        Intents.intended(hasComponent(DashboardActivity::class.java.name))
    }

    @Test
    fun loginSchoolUser_navigatesToSchoolDetail() {
        composeRule.onNodeWithTag("email").performTextInput("school@test.com")
        composeRule.onNodeWithTag("password").performTextInput("school123")
        composeRule.onNodeWithTag("login").performClick()

        Intents.intended(hasComponent(SchoolDetailActivity::class.java.name))
    }

    @Test
    fun loginAdminUser_navigatesToAdminDashboard() {
        composeRule.onNodeWithTag("email").performTextInput("admin@test.com")
        composeRule.onNodeWithTag("password").performTextInput("admin123")
        composeRule.onNodeWithTag("login").performClick()

        Intents.intended(hasComponent(AdminDashboardActivity::class.java.name))
    }

    @Test
    fun emptyFields_showError() {
        composeRule.onNodeWithTag("login").performClick()

        composeRule.onNodeWithText("Please fill in all fields")
            .assertIsDisplayed()
    }
}
