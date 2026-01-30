package com.example.unick

import com.example.unick.model.SchoolProfileModel
import com.example.unick.repository.SchoolProfileRepo
import com.example.unick.viewmodel.SchoolDetailViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class SchoolDetailViewModelTest {

    @Test
    fun loadSchoolDetail_success_updatesSchoolProfile() {
        val repo = mock<SchoolProfileRepo>()
        val viewModel = SchoolDetailViewModel(repo)

        val fakeProfile = SchoolProfileModel(
            schoolName = "Test School",
            location = "Kathmandu"
        )

        doAnswer {
            val callback = it.getArgument<(SchoolProfileModel?) -> Unit>(1)
            callback(fakeProfile)
            null
        }.`when`(repo).observeSchoolProfile(any(), any(), any())

        doAnswer { null }.`when`(repo).observeGallery(any(), any(), any())
        doAnswer { null }.`when`(repo).observeReviews(any(), any(), any())

        viewModel.loadSchoolDetail("school123")

        assertNotNull(viewModel.schoolProfile)
        assertEquals("Test School", viewModel.schoolProfile?.schoolName)

        verify(repo).observeSchoolProfile(any(), any(), any())
    }
}
// The test failed because Firebase uses Android OS APIs which are unavailable in JVM unit tests.
//We fixed it by using dependency injection so Firebase is excluded from unit tests.