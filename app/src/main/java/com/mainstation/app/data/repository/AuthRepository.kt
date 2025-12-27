package com.mainstation.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.mainstation.app.data.model.AuthResponse
import com.mainstation.app.data.model.LoginRequest
import com.mainstation.app.data.model.RegisterRequest
import com.mainstation.app.data.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {
    suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val result = auth.signInWithEmailAndPassword(request.email, request.password).await()
            val user = result.user
            if (user != null) {
                // For now, simpler mapping. In real app, might fetch extra user data from Firestore here.
                val role = if (user.email == "admin@mainstation.com") "ADMIN" else "USER"
                
                Result.success(
                    AuthResponse(
                        token = user.uid,
                        user = User(
                            id = user.uid,
                            name = user.displayName ?: "User",
                            email = user.email ?: "",
                            role = role
                        )
                    )
                )
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<AuthResponse> {
        return try {
            val result = auth.createUserWithEmailAndPassword(request.email, request.password).await()
            val user = result.user
            if (user != null) {
                Result.success(
                    AuthResponse(
                        token = user.uid,
                        user = User(
                            id = user.uid,
                            name = request.name,
                            email = user.email ?: "",
                            role = "USER"
                        )
                    )
                )
            } else {
                Result.failure(Exception("Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun logout() {
        auth.signOut()
    }
    
    fun getCurrentUser(): User? {
        val user = auth.currentUser ?: return null
        val role = if (user.email == "admin@mainstation.com") "ADMIN" else "USER"
        return User(
             id = user.uid,
             name = user.displayName ?: "User",
             email = user.email ?: "",
             role = role
        )
    }
}
