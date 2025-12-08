package cl.duoc.dsy1105.moodtracker.data.repository

import android.util.Log
import cl.duoc.dsy1105.moodtracker.data.local.dao.UserDao
import cl.duoc.dsy1105.moodtracker.data.local.entities.User
import cl.duoc.dsy1105.moodtracker.data.remote.NetworkModule
import cl.duoc.dsy1105.moodtracker.data.remote.api.UserApiService
import cl.duoc.dsy1105.moodtracker.data.remote.dto.LoginRequest
import cl.duoc.dsy1105.moodtracker.data.remote.dto.RegisterRequest
import java.security.MessageDigest

class UserRepository(
    private val userDao: UserDao,
    private val userApiService: UserApiService = NetworkModule.userApiService
) {

    /**
     * Register a new user with email and password
     * Offline-first strategy: Try API first, save to local Room, fallback to local-only if API fails
     * @return User ID if successful, null if email already exists
     */
    suspend fun registerUser(email: String, password: String): Long? {
        return try {
            // Try API registration first
            val response = userApiService.register(RegisterRequest(email, password))

            // Save to local Room for offline access (with backend-generated ID)
            val user = User(
                id = response.id,
                email = response.email,
                passwordHash = hashPassword(password),
                createdAt = response.createdAt
            )
            userDao.insertUser(user)

            Log.d("UserRepository", "User registered via API: ${response.id}")
            response.id
        } catch (e: Exception) {
            Log.e("UserRepository", "API register failed, trying local", e)

            // Fallback to local-only registration
            try {
                if (userDao.emailExists(email)) {
                    null // Email already exists locally
                } else {
                    val passwordHash = hashPassword(password)
                    val user = User(
                        email = email,
                        passwordHash = passwordHash
                    )
                    val localId = userDao.insertUser(user)
                    Log.d("UserRepository", "User registered locally: $localId")
                    localId
                }
            } catch (localException: Exception) {
                Log.e("UserRepository", "Local register also failed", localException)
                null
            }
        }
    }

    /**
     * Authenticate user with email and password
     * Offline-first strategy: Try API first, sync to local Room, fallback to local if API fails
     * @return User if credentials are valid, null otherwise
     */
    suspend fun login(email: String, password: String): User? {
        return try {
            // Try API login first
            val response = userApiService.login(LoginRequest(email, password))

            // Sync to local Room for offline access
            val user = User(
                id = response.id,
                email = response.email,
                passwordHash = hashPassword(password),
                createdAt = response.createdAt
            )
            userDao.insertUser(user)  // Insert or update

            Log.d("UserRepository", "User logged in via API: ${response.id}")
            user
        } catch (e: Exception) {
            Log.e("UserRepository", "API login failed, trying local", e)

            // Fallback to local authentication
            val passwordHash = hashPassword(password)
            val localUser = userDao.login(email, passwordHash)

            if (localUser != null) {
                Log.d("UserRepository", "User logged in locally: ${localUser.id}")
            } else {
                Log.w("UserRepository", "Local login also failed")
            }

            localUser
        }
    }

    /**
     * Get user by ID
     */
    suspend fun getUserById(userId: Long): User? {
        return userDao.getUserById(userId)
    }

    /**
     * Check if email already exists
     */
    suspend fun emailExists(email: String): Boolean {
        return userDao.emailExists(email)
    }

    /**
     * Hash password using SHA-256
     * Note: In production, use a more secure hashing algorithm like BCrypt
     */
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
