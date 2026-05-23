package com.example.hermes_travelapp.data.repository

import android.util.Log
import com.example.hermes_travelapp.data.database.dao.AccessLogDao
import com.example.hermes_travelapp.data.database.entities.AccessLogEntity
import com.example.hermes_travelapp.data.database.entities.UserEntity
import com.example.hermes_travelapp.domain.repository.AuthRepository
import com.example.hermes_travelapp.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val accessLogDao: AccessLogDao
) : AuthRepository {

    private val TAG = "AuthRepository"

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            Log.d(TAG, "Attempting to sign in with email: $email")
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Log.i(TAG, "Successfully signed in user: ${firebaseAuth.currentUser?.uid}")
            
            val uid = firebaseAuth.currentUser?.uid ?: return Result.success(Unit)
            
            // Sync with local database: Ensure user exists in Room
            val localUser = userRepository.getUserById(uid)
            if (localUser == null) {
                Log.d(TAG, "User $uid not found in local DB, creating entry")
                val emailStr = firebaseAuth.currentUser?.email ?: ""
                val usernameStr = emailStr.split("@").firstOrNull() ?: "User"
                val initials = if (usernameStr.length >= 2) usernameStr.substring(0, 2).uppercase() else "U"
                
                val newUser = UserEntity(
                    id = uid,
                    name = usernameStr,
                    email = emailStr,
                    login = emailStr,
                    username = usernameStr,
                    birthdate = 0L,
                    address = "",
                    country = "",
                    phone = "",
                    acceptEmails = false,
                    profileInitials = initials,
                    activeTripCount = 0,
                    countriesVisited = 0
                )
                userRepository.createUser(newUser)
            }

            accessLogDao.insertLog(
                AccessLogEntity(
                    id = UUID.randomUUID().toString(),
                    userId = uid,
                    datetime = System.currentTimeMillis(),
                    type = "IN"
                )
            )
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Sign in failed for email $email: ${e.message}", e)
            Result.failure(e)
        }
    }

    override fun signOut() {
        val uid = firebaseAuth.currentUser?.uid
        Log.d(TAG, "Signing out user: $uid")
        
        if (uid != null) {
            kotlinx.coroutines.runBlocking {
                accessLogDao.insertLog(
                    AccessLogEntity(
                        id = UUID.randomUUID().toString(),
                        userId = uid,
                        datetime = System.currentTimeMillis(),
                        type = "OUT"
                    )
                )
            }
        }
        
        firebaseAuth.signOut()
        Log.i(TAG, "Successfully signed out")
    }

    override fun isLoggedIn(): Boolean {
        val loggedIn = firebaseAuth.currentUser != null
        Log.v(TAG, "Checking login status: $loggedIn")
        return loggedIn
    }

    override fun getCurrentUserId(): String? {
        val uid = firebaseAuth.currentUser?.uid
        Log.v(TAG, "Current user ID: $uid")
        return uid
    }

    override suspend fun register(
        email: String,
        password: String,
        username: String,
        birthDate: String
    ): Result<Unit> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Log.d(TAG, "register: Firebase user created successfully")

            val firebaseUser = firebaseAuth.currentUser ?: throw Exception("User creation failed")

            val birthdateEpoch = if (birthDate.isNotBlank()) {
                try {
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    LocalDate.parse(birthDate, formatter).toEpochDay()
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to parse birthDate: $birthDate", e)
                    0L
                }
            } else {
                0L
            }

            val initials = if (username.length >= 2) {
                username.substring(0, 2).uppercase()
            } else if (username.isNotEmpty()) {
                username.uppercase()
            } else {
                "??"
            }

            val userEntity = UserEntity(
                id = firebaseUser.uid,
                name = username, // Using username as name since name is not collected during registration
                email = email,
                login = email,
                username = username,
                birthdate = birthdateEpoch,
                address = "",
                country = "",
                phone = "",
                acceptEmails = false,
                profileInitials = initials,
                activeTripCount = 0,
                countriesVisited = 0
            )

            val roomResult = userRepository.createUser(userEntity)
            Log.i(TAG, "Room registration result for ${userEntity.id}: $roomResult")

            // Log access as user is automatically signed in after registration
            accessLogDao.insertLog(
                AccessLogEntity(
                    id = UUID.randomUUID().toString(),
                    userId = firebaseUser.uid,
                    datetime = System.currentTimeMillis(),
                    type = "IN"
                )
            )

            sendEmailVerification()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "register: failed - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun sendEmailVerification(): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user == null) {
                Result.failure(Exception("No user logged in"))
            } else {
                user.sendEmailVerification().await()
                Log.d(TAG, "sendEmailVerification: email sent")
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Log.e(TAG, "sendEmailVerification: failed - ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun isEmailVerified(): Boolean {
        return firebaseAuth.currentUser?.isEmailVerified ?: false
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Log.d(TAG, "sendPasswordResetEmail: email sent to $email")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "sendPasswordResetEmail: failed - ${e.message}")
            Result.failure(e)
        }
    }
}
