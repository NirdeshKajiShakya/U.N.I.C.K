package com.example.unick

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.unick.view.AdminDashboardActivity
import com.example.unick.view.DashboardActivity
import com.example.unick.view.SchoolDashboard
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

    // Test for User login
    @Test
    fun loginUser_navigatesToUserDashboard() {
        composeRule.onNodeWithTag("email").performTextInput("user@test.com")
        composeRule.onNodeWithTag("password").performTextInput("user123")
        composeRule.onNodeWithTag("login").performClick()
        Intents.intended(hasComponent(DashboardActivity::class.java.name))
    }

    // Test for School login
    @Test
    fun loginSchool_navigatesToSchoolDashboard() {
        composeRule.onNodeWithTag("email").performTextInput("school@test.com")
        composeRule.onNodeWithTag("password").performTextInput("school123")
        composeRule.onNodeWithTag("login").performClick()
        Intents.intended(hasComponent(SchoolDashboard::class.java.name))
    }

}
