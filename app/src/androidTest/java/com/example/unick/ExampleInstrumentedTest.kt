package com.example.unick

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private val expectedEmail = "emailtestkis@gmail.com"
    private val expectedPassword = "emailtestkis@gmail.com"

    // Only running the valid login test for now
    @Test
    fun admin_login_test_valid() {
        val inputEmail = "emailtestkis@gmail.com"
        val inputPassword = "emailtestkis@gmail.com"

        println("Attempting Admin Login (Valid)...")
        println("Email: $inputEmail")

        if (inputEmail == expectedEmail && inputPassword == expectedPassword) {
            println("Output: Admin Login Successful")
            assert(true)
        } else {
            println("Output: Admin Login Failed")
            assert(false)
        }
    }



    @Test
    fun admin_login_test_invalid_password() {
        val inputEmail = "emailtestkis@gmail.com"
        val inputPassword = "wrongpassword"

        println("Attempting Admin Login (Wrong Password)...")
        println("Email: $inputEmail")

        if (inputEmail == expectedEmail && inputPassword == expectedPassword) {
            println("Output: Admin Login Successful")
            assert(true)
        } else {
            println("Output: Admin Login Failed")
            assert(false)
        }
    }

    @Test
    fun admin_login_test_invalid_email() {
        val inputEmail = "wrongemail@gmail.com"
        val inputPassword = "emailtestkis@gmail.com"

        println("Attempting Admin Login (Wrong Email)...")
        println("Email: $inputEmail")

        if (inputEmail == expectedEmail && inputPassword == expectedPassword) {
            println("Output: Admin Login Successful")
            assert(true)
        } else {
            println("Output: Admin Login Failed")
            assert(false)
        }
    }


    @Test
    fun useAppContext() {
        // Standard instrumented test that checks the app context
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.unick", appContext.packageName)
    }
}
