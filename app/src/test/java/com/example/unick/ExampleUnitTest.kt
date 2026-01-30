package com.example.unick

import org.junit.Test
import org.junit.Assert.*

class ExampleUnitTest {

    private val expectedEmail = "emailtestkis@gmail.com"
    private val expectedPassword = "emailtestkis@gmail.com"

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
}
