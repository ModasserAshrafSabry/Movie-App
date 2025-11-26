// app/src/test/java/com/example/movieapp/ui/profile/ProfileViewModelTest.kt

package com.example.movieapp.ui.profile

import app.cash.turbine.test
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var viewModel: ProfileViewModel
    private val userId = "test_user_123"

    private lateinit var mockAuth: FirebaseAuth
    private lateinit var mockUser: FirebaseUser
    private lateinit var mockDb: FirebaseFirestore
    private lateinit var mockCollection: CollectionReference
    private lateinit var mockDocument: DocumentReference
    private lateinit var mockSnapshot: DocumentSnapshot

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        clearAllMocks()

        mockkStatic(FirebaseAuth::class)
        mockkStatic(FirebaseFirestore::class)
        mockkStatic(Tasks::class)

        // Auth
        mockUser = mockk<FirebaseUser>(relaxed = true)
        every { mockUser.uid } returns userId

        mockAuth = mockk(relaxed = true)
        every { FirebaseAuth.getInstance() } returns mockAuth
        every { mockAuth.currentUser } returns mockUser

        // Firestore
        mockSnapshot = mockk(relaxed = true)
        mockDocument = mockk(relaxed = true)
        mockCollection = mockk(relaxed = true)
        mockDb = mockk(relaxed = true)

        every { FirebaseFirestore.getInstance() } returns mockDb
        every { mockDb.collection("users") } returns mockCollection
        every { mockCollection.document(userId) } returns mockDocument

        // Successful tasks
        val getTask: Task<DocumentSnapshot> = Tasks.forResult(mockSnapshot)
        val voidTask: Task<Void> = Tasks.forResult(null)

        every { mockDocument.get() } returns getTask
        every { mockDocument.set(any(), any<SetOptions>()) } returns voidTask
        every { mockDocument.update(any<String>(), any()) } returns voidTask
        every { mockDocument.update(any<Map<String, Any>>()) } returns voidTask

        // Critical: Make Tasks.await() return immediately
        coEvery { Tasks.await(any<Task<*>>()) } coAnswers { }

        // Create ViewModel and wait for init block to complete
        viewModel = ProfileViewModel()
        testScope.advanceUntilIdle() // This makes loadUserProfile() finish instantly
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `loadUserProfile loads existing user successfully`() = testScope.runTest {
        every { mockSnapshot.exists() } returns true
        every { mockSnapshot.data } returns mapOf(
            "username" to "Tom Hardy",
            "favoriteGenres" to listOf("Action", "Drama"),
            "hasCompletedGenreSelection" to true
        )

        viewModel.profileState.test(timeout = 5.seconds) {
            skipItems(1) // skip initial state
            val state = awaitItem()
            assertEquals("Tom Hardy", state.username)
            assertTrue("Action" in state.favoriteGenres)
            assertTrue(state.hasCompletedGenreSelection)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateUsername updates state and Firestore`() = testScope.runTest {
        viewModel.updateUsername("AwesomeUser")

        viewModel.profileState.test(timeout = 5.seconds) {
            skipItems(1)
            assertEquals("AwesomeUser", awaitItem().username)
            cancelAndIgnoreRemainingEvents()
        }

        coVerify { mockDocument.update("username", "AwesomeUser") }
    }

    @Test
    fun `saveFavoriteGenre uses arrayUnion correctly`() = testScope.runTest {
        viewModel.saveFavoriteGenre("Horror")

        viewModel.profileState.test(timeout = 5.seconds) {
            skipItems(1)
            assertTrue("Horror" in awaitItem().favoriteGenres)
            cancelAndIgnoreRemainingEvents()
        }

        val mapSlot = slot<Map<String, Any>>()
        coVerify { mockDocument.set(capture(mapSlot), eq(SetOptions.merge())) }
        val fieldValue = mapSlot.captured["favoriteGenres"]
        assertEquals(FieldValue.arrayUnion("Horror"), fieldValue)
    }

    @Test
    fun `removeFavoriteGenre uses arrayRemove correctly`() = testScope.runTest {
        // Set up the initial state directly in the real ViewModel
        // (we control it via Firestore mock in loadUserProfile)
        every { mockSnapshot.exists() } returns true
        every { mockSnapshot.get("favoriteGenres") } returns listOf("Comedy", "Romance", "Sci-Fi")
        every { mockSnapshot.get("username") } returns "Test User"
        every { mockSnapshot.get("hasCompletedGenreSelection") } returns true

        // Reload profile so the state is populated
        viewModel.loadUserProfile()
        advanceUntilIdle() // wait for emission

        // Now call the method
        viewModel.removeFavoriteGenre("Romance")

        // Assert state update
        viewModel.profileState.test(timeout = 5.seconds) {
            skipItems(1) // skip current state
            val updatedState = awaitItem()
            assertEquals(listOf("Comedy", "Sci-Fi"), updatedState.favoriteGenres.sorted())
            cancelAndIgnoreRemainingEvents()
        }

        // Assert Firestore call
        val slot = slot<Any>()
        coVerify { mockDocument.update("favoriteGenres", capture(slot)) }
        assertEquals(FieldValue.arrayRemove("Romance"), slot.captured)
    }

    @Test
    fun `removeFavoriteCelebrity uses arrayRemove with correct map containing correct data`() = testScope.runTest {
        val celeb = FavoriteCelebrity("123", "Emma Stone", "Actor", "https://img.jpg")

        viewModel.removeFavoriteCelebrity(celeb)

        val slot = slot<Any>()
        coVerify { mockDocument.update("favoriteCelebrities", capture(slot)) }

        val fieldValue = slot.captured as FieldValue
        val str = fieldValue.toString()

        assertTrue(str.contains("arrayRemove"))
        assertTrue(str.contains("\"id\"=\"123\"") || str.contains("id=123"))
        assertTrue(str.contains("Emma Stone"))
        assertTrue(str.contains("Actor"))
        assertTrue(str.contains("https://img.jpg"))
    }
}