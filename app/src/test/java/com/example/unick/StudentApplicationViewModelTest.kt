package com.example.unick.viewmodel

import android.util.Log
import com.example.unick.model.StudentApplication
import com.example.unick.repo.ApplicationRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.junit.Assert.*
import org.mockito.ArgumentMatchers.anyString

@OptIn(ExperimentalCoroutinesApi::class)
class StudentApplicationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: StudentApplicationViewModel
    private val mockRepo: ApplicationRepo = mock()

    private lateinit var logMock: MockedStatic<Log>

    @Before
    fun setup() {
        // Mock static Log methods to prevent "Method e in android.util.Log not mocked" crash
        logMock = Mockito.mockStatic(Log::class.java)
        logMock.`when`<Int> { Log.e(anyString(), anyString()) }.thenReturn(0)
        logMock.`when`<Int> { Log.e(anyString(), anyString(), anyOrNull()) }.thenReturn(0)
        logMock.`when`<Int> { Log.d(anyString(), anyString()) }.thenReturn(0)
        logMock.`when`<Int> { Log.w(anyString(), anyString()) }.thenReturn(0)

        Dispatchers.setMain(testDispatcher)
        viewModel = StudentApplicationViewModel(mockRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        if (::logMock.isInitialized) {
            logMock.close()
        }
    }

    @Test
    fun submitApplication_success_updatesToSuccessState() = runTest(testDispatcher) {
        // Given
        val application = StudentApplication(
            schoolId = "school_test_001",
            studentId = "student_test_123",
            fullName = "Test Student",
            standard = "7"
            // Add other required fields with defaults if your data class requires them
        )

        whenever(mockRepo.submitApplication(any()))
            .thenReturn(Result.success(Unit))

        // When
        viewModel.submitApplication(application)
        advanceUntilIdle()

        // Then
        val state = viewModel.submitState.value
        assertTrue("Expected Success state, got $state", state is SubmitState.Success)

        verify(mockRepo).submitApplication(eq(application))
    }

    @Test
    fun submitApplication_failure_updatesToErrorState() = runTest(testDispatcher) {
        // Given
        val application = StudentApplication(
            schoolId = "school_test_001",
            fullName = "Test Student"
        )
        val errorMsg = "Permission denied - check Firebase rules"

        whenever(mockRepo.submitApplication(any()))
            .thenReturn(Result.failure(RuntimeException(errorMsg)))

        // When
        viewModel.submitApplication(application)
        advanceUntilIdle()

        // Then
        val state = viewModel.submitState.value
        assertTrue("Expected Error state, got $state", state is SubmitState.Error)

        val errorState = state as SubmitState.Error
        assertEquals("Error message mismatch", errorMsg, errorState.message)

        verify(mockRepo).submitApplication(any())
    }

    @Test
    fun resetState_setsStateToIdle() = runTest(testDispatcher) {
        // Given - force error state
        whenever(mockRepo.submitApplication(any()))
            .thenReturn(Result.failure(Exception("temp error")))

        viewModel.submitApplication(StudentApplication())
        advanceUntilIdle()

        // Verify we reached error state
        assertTrue(
            "Should be in Error state before reset, was ${viewModel.submitState.value}",
            viewModel.submitState.value is SubmitState.Error
        )

        // When
        viewModel.resetState()

        // Then
        assertTrue(
            "Should be Idle after reset, was ${viewModel.submitState.value}",
            viewModel.submitState.value is SubmitState.Idle
        )
    }
}