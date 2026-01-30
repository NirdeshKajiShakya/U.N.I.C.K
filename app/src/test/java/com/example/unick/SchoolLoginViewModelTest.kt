package com.example.unick.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SchoolLoginViewModelTest {

    private lateinit var viewModel: UserLoginViewModel

    @Before
    fun setup() {
        viewModel = UserLoginViewModel()
    }

    @Test
    fun emptyFields_schoolLogin_showsError() {
        viewModel.login()

        assertEquals(
            "Email and password cannot be empty",
            viewModel.errorMessage.value
        )
    }

    @Test
    fun invalidEmail_schoolLogin_showsError() {
        viewModel.onEmailChange("schoolemail")
        viewModel.onPasswordChange("school123")

        viewModel.login()

        assertEquals(
            "Please enter a valid email",
            viewModel.errorMessage.value
        )
    }

    @Test
    fun validSchoolLogin_noValidationError() {
        viewModel.onEmailChange("school@test.com")
        viewModel.onPasswordChange("school123")

        viewModel.login()

        // Unit test ONLY checks validation result
        assertEquals(null, viewModel.errorMessage.value)
    }
}
