package cl.duoc.dsy1105.moodtracker.data.repository

import cl.duoc.dsy1105.moodtracker.data.local.dao.UserDao
import cl.duoc.dsy1105.moodtracker.data.local.entities.User
import cl.duoc.dsy1105.moodtracker.data.remote.api.UserApiService
import cl.duoc.dsy1105.moodtracker.data.remote.dto.LoginRequest
import cl.duoc.dsy1105.moodtracker.data.remote.dto.RegisterRequest
import cl.duoc.dsy1105.moodtracker.data.remote.dto.UserResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.security.MessageDigest

/**
 * Unit tests for UserRepository
 * Tests the offline-first hybrid sync strategy
 */
class UserRepositoryTest {

    private lateinit var userDao: UserDao
    private lateinit var userApiService: UserApiService
    private lateinit var userRepository: UserRepository

    private val testEmail = "test@example.com"
    private val testPassword = "password123"
    private val testPasswordHash = hashPassword(testPassword)
    private val testUserId = 1L
    private val testCreatedAt = System.currentTimeMillis()

    @Before
    fun setup() {
        userDao = mockk(relaxed = true)
        userApiService = mockk(relaxed = true)
        userRepository = UserRepository(userDao, userApiService)
    }

    // Helper function to hash password (same as in UserRepository)
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    @Test
    fun `registerUser API success should save to local and return userId`() = runTest {
        // Given
        val apiResponse = UserResponse(testUserId, testEmail, testCreatedAt)
        coEvery { userApiService.register(any()) } returns apiResponse
        coEvery { userDao.insertUser(any()) } returns testUserId

        // When
        val result = userRepository.registerUser(testEmail, testPassword)

        // Then
        assertEquals(testUserId, result)
        coVerify { userApiService.register(RegisterRequest(testEmail, testPassword)) }
        coVerify { userDao.insertUser(any()) }
    }

    @Test
    fun `registerUser API failure should fallback to local`() = runTest {
        // Given
        coEvery { userApiService.register(any()) } throws Exception("Network error")
        coEvery { userDao.emailExists(testEmail) } returns false
        coEvery { userDao.insertUser(any()) } returns 2L

        // When
        val result = userRepository.registerUser(testEmail, testPassword)

        // Then
        assertEquals(2L, result)
        coVerify { userApiService.register(any()) }
        coVerify { userDao.emailExists(testEmail) }
        coVerify { userDao.insertUser(any()) }
    }

    @Test
    fun `registerUser with existing local email should return null`() = runTest {
        // Given
        coEvery { userApiService.register(any()) } throws Exception("Network error")
        coEvery { userDao.emailExists(testEmail) } returns true

        // When
        val result = userRepository.registerUser(testEmail, testPassword)

        // Then
        assertNull(result)
        coVerify(exactly = 0) { userDao.insertUser(any()) }
    }

    @Test
    fun `login API success should sync to local and return User`() = runTest {
        // Given
        val apiResponse = UserResponse(testUserId, testEmail, testCreatedAt)
        val expectedUser = User(testUserId, testEmail, testPasswordHash, testCreatedAt)

        coEvery { userApiService.login(any()) } returns apiResponse
        coEvery { userDao.insertUser(any()) } returns testUserId

        // When
        val result = userRepository.login(testEmail, testPassword)

        // Then
        assertNotNull(result)
        assertEquals(testUserId, result?.id)
        assertEquals(testEmail, result?.email)
        coVerify { userApiService.login(LoginRequest(testEmail, testPassword)) }
        coVerify { userDao.insertUser(any()) }
    }

    @Test
    fun `login API failure should fallback to local auth`() = runTest {
        // Given
        val localUser = User(testUserId, testEmail, testPasswordHash, testCreatedAt)
        coEvery { userApiService.login(any()) } throws Exception("Network error")
        coEvery { userDao.login(testEmail, testPasswordHash) } returns localUser

        // When
        val result = userRepository.login(testEmail, testPassword)

        // Then
        assertNotNull(result)
        assertEquals(testUserId, result?.id)
        coVerify { userApiService.login(any()) }
        coVerify { userDao.login(testEmail, testPasswordHash) }
    }

    @Test
    fun `login with invalid local credentials should return null`() = runTest {
        // Given
        coEvery { userApiService.login(any()) } throws Exception("Network error")
        coEvery { userDao.login(any(), any()) } returns null

        // When
        val result = userRepository.login(testEmail, testPassword)

        // Then
        assertNull(result)
    }

    @Test
    fun `getUserById should retrieve from local database`() = runTest {
        // Given
        val user = User(testUserId, testEmail, testPasswordHash, testCreatedAt)
        coEvery { userDao.getUserById(testUserId) } returns user

        // When
        val result = userRepository.getUserById(testUserId)

        // Then
        assertNotNull(result)
        assertEquals(testUserId, result?.id)
        coVerify { userDao.getUserById(testUserId) }
    }

    @Test
    fun `emailExists should check local database`() = runTest {
        // Given
        coEvery { userDao.emailExists(testEmail) } returns true

        // When
        val result = userRepository.emailExists(testEmail)

        // Then
        assertTrue(result)
        coVerify { userDao.emailExists(testEmail) }
    }

    @Test
    fun `password hashing should produce consistent results`() {
        // When
        val hash1 = hashPassword(testPassword)
        val hash2 = hashPassword(testPassword)

        // Then
        assertEquals(hash1, hash2)
        assertEquals(64, hash1.length) // SHA-256 produces 64 hex characters
    }

    @Test
    fun `password hashing should match known SHA-256 value`() {
        // Given
        val knownPassword = "password"
        val knownHash = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8"

        // When
        val hash = hashPassword(knownPassword)

        // Then
        assertEquals(knownHash, hash)
    }
}
