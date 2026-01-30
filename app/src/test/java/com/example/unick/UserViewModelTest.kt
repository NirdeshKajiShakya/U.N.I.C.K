package com.example.unick.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UserLoginViewModelTest {

    private lateinit var viewModel: UserLoginViewModel

    @Before
    fun setup() {
        viewModel = UserLoginViewModel()
    }

    @Test
    fun emptyFields_showsError() {
        viewModel.login()
        assertEquals(
            "Email and password cannot be empty",
            viewModel.errorMessage.value
        )
    }

    @Test
    fun invalidEmail_showsError() {
        viewModel.onEmailChange("invalidemail")
        viewModel.onPasswordChange("123456")
        viewModel.login()
        assertEquals(
            "Please enter a valid email",
            viewModel.errorMessage.value
        )
    }

    @Test
    fun validEmail_noError() {
        viewModel.onEmailChange("user@test.com")
        viewModel.onPasswordChange("user123")
        viewModel.login()

        assertEquals(null, viewModel.errorMessage.value)
    }
}
