package com.example.unick.repo

import com.example.unick.model.SchoolForm

interface CompareSchoolRepo {
    suspend fun getAllSchools(): Result<List<SchoolForm>>
    suspend fun getSchoolById(schoolId: String): Result<SchoolForm>
}

