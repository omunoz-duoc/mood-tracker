package cl.duoc.dsy1105.moodtracker.data.repository

import cl.duoc.dsy1105.moodtracker.data.local.dao.UserDao
import cl.duoc.dsy1105.moodtracker.data.local.entities.User
import java.security.MessageDigest

class UserRepository(private val userDao: UserDao) {

    /**
     * Register a new user with email and password
     * @return User ID if successful, null if email already exists
     */
    suspend fun registerUser(email: String, password: String): Long? {
        return try {
            if (userDao.emailExists(email)) {
                null // Email already exists
            } else {
                val passwordHash = hashPassword(password)
                val user = User(
                    email = email,
                    passwordHash = passwordHash
                )
                userDao.insertUser(user)
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Authenticate user with email and password
     * @return User if credentials are valid, null otherwise
     */
    suspend fun login(email: String, password: String): User? {
        val passwordHash = hashPassword(password)
        return userDao.login(email, passwordHash)
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
