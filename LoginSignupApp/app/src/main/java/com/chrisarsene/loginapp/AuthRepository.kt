package com.chrisarsene.loginapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(mapError(e))
        }
    }

    suspend fun signup(name: String, email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!
            val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            user.updateProfile(profileUpdate).await()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(mapError(e))
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(mapError(e))
        }
    }

    fun logout() = auth.signOut()

    private fun mapError(e: Exception): Exception {
        val msg = e.message ?: "Something went wrong"
        return when {
            msg.contains("no user record") || msg.contains("user-not-found") ->
                Exception("No account found with this email")
            msg.contains("wrong-password") || msg.contains("invalid-credential") ->
                Exception("Incorrect password")
            msg.contains("email-already-in-use") ->
                Exception("An account with this email already exists")
            msg.contains("weak-password") ->
                Exception("Password must be at least 6 characters")
            msg.contains("invalid-email") ->
                Exception("Please enter a valid email address")
            msg.contains("network") || msg.contains("Network") ->
                Exception("No internet connection")
            else -> Exception(msg)
        }
    }
}
