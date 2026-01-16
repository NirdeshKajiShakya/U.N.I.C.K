package com.example.unick.model

/**
 * Model representing an application as viewed by the school.
 * This is essentially the same as StudentApplication but can be extended
 * with additional display-only fields if needed.
 */
data class ViewApplicationModel(
    val applicationId: String = "",
    val schoolId: String = "",
    val studentId: String = "",

    // Student Info
    val fullName: String = "",
    val dob: String = "",
    val gender: String = "",
    val bloodGroup: String = "",
    val interests: String = "",

    // Academic Info
    val lastSchoolName: String = "",
    val standard: String = "",

    // Parent Info
    val fatherName: String = "",
    val fatherPhone: String = "",
    val motherName: String = "",
    val motherPhone: String = "",

    // Address
    val presentAddress: String = "",
    val permanentAddress: String = "",
    val schoolBudget: String = "",

    // Status & Review
    val status: String = "pending",  // "pending", "accepted", "rejected"
    val reviewedBy: String = "",
    val reviewedAt: Long = 0L,
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Convert from StudentApplication to ViewApplicationModel
     */
    companion object {
        fun fromStudentApplication(app: StudentApplication): ViewApplicationModel {
            return ViewApplicationModel(
                applicationId = app.applicationId,
                schoolId = app.schoolId,
                studentId = app.studentId,
                fullName = app.fullName,
                dob = app.dob,
                gender = app.gender,
                bloodGroup = app.bloodGroup,
                interests = app.interests,
                lastSchoolName = app.lastSchoolName,
                standard = app.standard,
                fatherName = app.fatherName,
                fatherPhone = app.fatherPhone,
                motherName = app.motherName,
                motherPhone = app.motherPhone,
                presentAddress = app.presentAddress,
                permanentAddress = app.permanentAddress,
                schoolBudget = app.schoolBudget,
                status = app.status,
                reviewedBy = app.reviewedBy,
                reviewedAt = app.reviewedAt,
                timestamp = app.timestamp
            )
        }
    }

    /**
     * Check if application is pending review
     */
    fun isPending(): Boolean = status.lowercase() == "pending"

    /**
     * Check if application was accepted
     */
    fun isAccepted(): Boolean = status.lowercase() == "accepted"

    /**
     * Check if application was rejected
     */
    fun isRejected(): Boolean = status.lowercase() == "rejected"

    /**
     * Get formatted status for display
     */
    fun getDisplayStatus(): String = status.replaceFirstChar { it.uppercase() }
}
