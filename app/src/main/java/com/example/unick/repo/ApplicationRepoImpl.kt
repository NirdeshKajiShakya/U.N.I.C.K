package com.example.unick.repo

import com.example.unick.model.StudentApplication
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Firestore-backed implementation of [ApplicationRepo].
 *
 * Notes:
 * - Make sure Firebase is initialized (google-services.json + Firebase setup) in the app.
 * - This class uses the Firestore Kotlin extensions (ktx) and kotlinx-coroutines-play-services
 */
class ApplicationRepoImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ApplicationRepo {

    private val COLLECTION = "student_applications"

    override suspend fun submitApplication(
        application: StudentApplication
    ): Result<Unit> {
        return try {
            // Add application to Firestore; await ensures we run this in coroutines safely
            firestore.collection(COLLECTION)
                .add(application)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
