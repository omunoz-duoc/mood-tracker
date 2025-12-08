package cl.duoc.dsy1105.moodtracker.data.repository

import cl.duoc.dsy1105.moodtracker.data.local.dao.MoodDao
import cl.duoc.dsy1105.moodtracker.data.local.entities.MoodEntry
import cl.duoc.dsy1105.moodtracker.data.remote.api.MoodApiService
import cl.duoc.dsy1105.moodtracker.data.remote.dto.MoodEntryResponse
import cl.duoc.dsy1105.moodtracker.domain.model.MoodType
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for MoodRepository
 * Tests the offline-first hybrid sync strategy
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MoodRepositoryTest {

    private lateinit var moodDao: MoodDao
    private lateinit var moodApiService: MoodApiService
    private lateinit var moodRepository: MoodRepository

    private val testUserId = 1L
    private val testMoodId = 1L
    private val testDate = System.currentTimeMillis()
    private val testNote = "Feeling great!"

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        moodDao = mockk(relaxed = true)
        moodApiService = mockk(relaxed = true)
        moodRepository = MoodRepository(moodDao, moodApiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveMoodEntry should save to local immediately`() = runTest {
        // Given
        val expectedMoodEntry = MoodEntry.create(testUserId, MoodType.HAPPY, testNote)
        coEvery { moodDao.insertMoodEntry(any()) } returns testMoodId
        coEvery { moodApiService.createMood(any()) } returns MoodEntryResponse(
            testMoodId, testUserId, "HAPPY", testNote, null, null, null, testDate
        )

        // When
        val result = moodRepository.saveMoodEntry(testUserId, MoodType.HAPPY, testNote)

        // Then
        assertEquals(testMoodId, result)
        coVerify { moodDao.insertMoodEntry(any()) }

        // Advance time to allow background coroutine to execute
        advanceUntilIdle()

        // Verify API call was made in background
        coVerify { moodApiService.createMood(any()) }
    }

    @Test
    fun `saveMoodEntry should continue even if API sync fails`() = runTest {
        // Given
        coEvery { moodDao.insertMoodEntry(any()) } returns testMoodId
        coEvery { moodApiService.createMood(any()) } throws Exception("Network error")

        // When
        val result = moodRepository.saveMoodEntry(testUserId, MoodType.HAPPY, testNote)

        // Then - should still return local ID
        assertEquals(testMoodId, result)
        coVerify { moodDao.insertMoodEntry(any()) }

        // Advance time to allow background coroutine to execute
        advanceUntilIdle()

        // Verify API call was attempted
        coVerify { moodApiService.createMood(any()) }
    }

    @Test
    fun `getAllMoodEntriesForUser should return local Flow`() = runTest {
        // Given
        val moodEntries = listOf(
            MoodEntry(1L, testUserId, "HAPPY", testNote, null, null, null, testDate),
            MoodEntry(2L, testUserId, "SAD", "Feeling down", null, null, null, testDate - 1000)
        )
        coEvery { moodDao.getAllMoodEntriesForUser(testUserId) } returns flowOf(moodEntries)
        coEvery { moodApiService.getUserMoods(testUserId) } returns emptyList()

        // When
        val flow = moodRepository.getAllMoodEntriesForUser(testUserId)

        // Then
        assertNotNull(flow)
        coVerify { moodDao.getAllMoodEntriesForUser(testUserId) }

        // Advance time to allow background sync
        advanceUntilIdle()

        // Verify API fetch was attempted
        coVerify { moodApiService.getUserMoods(testUserId) }
    }

    @Test
    fun `getAllMoodEntriesForUser should update local cache from API`() = runTest {
        // Given
        val apiMoods = listOf(
            MoodEntryResponse(1L, testUserId, "HAPPY", testNote, null, null, null, testDate),
            MoodEntryResponse(2L, testUserId, "EXCITED", "Great day!", null, null, null, testDate - 1000)
        )

        val localMoods = apiMoods.map { dto ->
            MoodEntry(dto.id, dto.userId, dto.moodType, dto.note, dto.audioUri,
                      dto.audioDuration, dto.imageUris, dto.date)
        }

        coEvery { moodDao.getAllMoodEntriesForUser(testUserId) } returns flowOf(localMoods)
        coEvery { moodApiService.getUserMoods(testUserId) } returns apiMoods
        coEvery { moodDao.deleteAllMoodEntriesForUser(testUserId) } just Runs
        coEvery { moodDao.insertMoodEntry(any()) } returns 1L

        // When
        val flow = moodRepository.getAllMoodEntriesForUser(testUserId)

        // Then
        assertNotNull(flow)

        // Advance time to allow background sync
        advanceUntilIdle()

        // Verify local cache was cleared and updated
        coVerify { moodDao.deleteAllMoodEntriesForUser(testUserId) }
        coVerify(exactly = apiMoods.size) { moodDao.insertMoodEntry(any()) }
    }

    @Test
    fun `getAllMoodEntriesForUser should handle API failure gracefully`() = runTest {
        // Given
        val localMoods = listOf(
            MoodEntry(1L, testUserId, "HAPPY", testNote, null, null, null, testDate)
        )

        coEvery { moodDao.getAllMoodEntriesForUser(testUserId) } returns flowOf(localMoods)
        coEvery { moodApiService.getUserMoods(testUserId) } throws Exception("Network error")

        // When
        val flow = moodRepository.getAllMoodEntriesForUser(testUserId)

        // Then - should still return local Flow
        assertNotNull(flow)

        // Advance time
        advanceUntilIdle()

        // Local cache should NOT be cleared on API failure
        coVerify(exactly = 0) { moodDao.deleteAllMoodEntriesForUser(any()) }
    }

    @Test
    fun `deleteMoodEntry should delete from local immediately`() = runTest {
        // Given
        coEvery { moodDao.deleteMoodEntry(testMoodId) } just Runs
        coEvery { moodApiService.deleteMood(testMoodId) } returns mockk()

        // When
        moodRepository.deleteMoodEntry(testMoodId)

        // Then
        coVerify { moodDao.deleteMoodEntry(testMoodId) }

        // Advance time for background sync
        advanceUntilIdle()

        // Verify API deletion was attempted
        coVerify { moodApiService.deleteMood(testMoodId) }
    }

    @Test
    fun `deleteMoodEntry should continue even if API delete fails`() = runTest {
        // Given
        coEvery { moodDao.deleteMoodEntry(testMoodId) } just Runs
        coEvery { moodApiService.deleteMood(testMoodId) } throws Exception("Network error")

        // When
        moodRepository.deleteMoodEntry(testMoodId)

        // Then - should still delete locally
        coVerify { moodDao.deleteMoodEntry(testMoodId) }

        // Advance time
        advanceUntilIdle()

        // Verify API deletion was attempted
        coVerify { moodApiService.deleteMood(testMoodId) }
    }

    @Test
    fun `getRecentMoodEntries should retrieve from local database`() = runTest {
        // Given
        val recentMoods = listOf(
            MoodEntry(1L, testUserId, "HAPPY", testNote, null, null, null, testDate)
        )
        coEvery { moodDao.getRecentMoodEntries(testUserId, 10) } returns recentMoods

        // When
        val result = moodRepository.getRecentMoodEntries(testUserId, 10)

        // Then
        assertEquals(1, result.size)
        assertEquals("HAPPY", result[0].moodType)
        coVerify { moodDao.getRecentMoodEntries(testUserId, 10) }
    }

    @Test
    fun `getMoodEntryById should retrieve from local database`() = runTest {
        // Given
        val moodEntry = MoodEntry(testMoodId, testUserId, "HAPPY", testNote, null, null, null, testDate)
        coEvery { moodDao.getMoodEntryById(testMoodId) } returns moodEntry

        // When
        val result = moodRepository.getMoodEntryById(testMoodId)

        // Then
        assertNotNull(result)
        assertEquals(testMoodId, result?.id)
        assertEquals("HAPPY", result?.moodType)
        coVerify { moodDao.getMoodEntryById(testMoodId) }
    }

    @Test
    fun `getMoodEntryCount should retrieve from local database`() = runTest {
        // Given
        coEvery { moodDao.getMoodEntryCount(testUserId) } returns 5

        // When
        val result = moodRepository.getMoodEntryCount(testUserId)

        // Then
        assertEquals(5, result)
        coVerify { moodDao.getMoodEntryCount(testUserId) }
    }

    @Test
    fun `updateMoodEntry should update in local database`() = runTest {
        // Given
        val moodEntry = MoodEntry(testMoodId, testUserId, "HAPPY", testNote, null, null, null, testDate)
        coEvery { moodDao.updateMoodEntry(moodEntry) } just Runs

        // When
        moodRepository.updateMoodEntry(moodEntry)

        // Then
        coVerify { moodDao.updateMoodEntry(moodEntry) }
    }
}
