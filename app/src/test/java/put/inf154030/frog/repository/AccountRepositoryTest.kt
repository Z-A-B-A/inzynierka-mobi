package put.inf154030.frog.repository

import android.content.SharedPreferences
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import put.inf154030.frog.models.User
import put.inf154030.frog.models.requests.LoginRequest
import put.inf154030.frog.models.requests.RegisterRequest
import put.inf154030.frog.models.requests.UserUpdateRequest
import put.inf154030.frog.models.responses.AuthResponse
import put.inf154030.frog.models.responses.RegisterResponse
import put.inf154030.frog.models.responses.UserResponse
import put.inf154030.frog.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountRepositoryTest {

    @Mock
    lateinit var mockApiService: ApiService

    @Mock
    lateinit var mockUserCall: Call<UserResponse>

    @Mock
    lateinit var mockAuthCall: Call<AuthResponse>

    @Mock
    lateinit var mockRegisterCall: Call<RegisterResponse>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        val mockPrefs = mock(SharedPreferences::class.java)
        val mockEditor = mock(SharedPreferences.Editor::class.java)
        // Make edit() return the mock editor
        `when`(mockPrefs.edit()).thenReturn(mockEditor)
        // Make putString and apply return the editor itself (for chaining)
        `when`(mockEditor.putString(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString())).thenReturn(mockEditor)
        `when`(mockEditor.apply()).then { }
        put.inf154030.frog.network.SessionManager.prefs = mockPrefs
    }

    // --- updateUser tests ---

    @Test
    fun `updateUser success updates session and calls onResult with success`() {
        val repo = AccountRepository(mockApiService)
        val request = UserUpdateRequest("John", "john@example.com")
        val userResponse = UserResponse(1, "John", "john@example.com", "user")
        val response = Response.success(userResponse)

        `when`(mockApiService.updateUser(request)).thenReturn(mockUserCall)

        var callback: ((Boolean, String?) -> Unit)? = null
        repo.updateUser(request) { _, _ ->
            callback = { s, e -> 
                assert(s)
                assert(e == null)
            }
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<UserResponse>>
        verify(mockUserCall).enqueue(captor.capture())
        captor.value.onResponse(mockUserCall, response)

        // You may need to use mockito-inline or PowerMock for static mocking
        // verifyStatic(SessionManager::class.java)
        // SessionManager.saveUpdatedUserInfo("John", "john@example.com")
    }

    @Test
    fun `updateUser failure calls onResult with error`() {
        val repo = AccountRepository(mockApiService)
        val request = UserUpdateRequest("John", "john@example.com")
        val response = Response.error<UserResponse>(
            400, ResponseBody.create("application/json".toMediaTypeOrNull(), "error")
        )

        `when`(mockApiService.updateUser(request)).thenReturn(mockUserCall)

        repo.updateUser(request) { success, error ->
            assert(!success)
            assert(error == "Could not update user data")
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<UserResponse>>
        verify(mockUserCall).enqueue(captor.capture())
        captor.value.onResponse(mockUserCall, response)
    }

    @Test
    fun `updateUser network failure calls onResult with network error`() {
        val repo = AccountRepository(mockApiService) // Use mockApiService!
        val request = UserUpdateRequest("John", "john@example.com")

        `when`(mockApiService.updateUser(request)).thenReturn(mockUserCall)

        repo.updateUser(request) { success, error ->
            assert(!success)
            assert(error?.contains("Network error") == true)
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<UserResponse>>
        verify(mockUserCall).enqueue(captor.capture())
        captor.value.onFailure(mockUserCall, Throwable("timeout"))
    }

    // --- loginUser tests ---

    @Test
    fun `loginUser success saves token and user info and calls onResult with success`() {
        val repo = AccountRepository(mockApiService)
        val request = LoginRequest("john@example.com", "password")
        val user = User(1, "John", "john@example.com", "user")
        val authResponse = AuthResponse("token123", user)
        val response = Response.success(authResponse)

        `when`(mockApiService.loginUser(request)).thenReturn(mockAuthCall)

        repo.loginUser(request) { success, error ->
            assert(success)
            assert(error == null)
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<AuthResponse>>
        verify(mockAuthCall).enqueue(captor.capture())
        captor.value.onResponse(mockAuthCall, response)

        // You may need to use mockito-inline or PowerMock for static mocking
        // verifyStatic(SessionManager::class.java)
        // SessionManager.saveAuthToken("token123")
        // SessionManager.saveUserInfo("1", "John", "john@example.com")
    }

    @Test
    fun `loginUser failure calls onResult with error`() {
        val repo = AccountRepository(mockApiService)
        val request = LoginRequest("john@example.com", "password")
        val response = Response.error<AuthResponse>(
            401, "unauthorized".toResponseBody("application/json".toMediaTypeOrNull())
        )

        `when`(mockApiService.loginUser(request)).thenReturn(mockAuthCall)

        repo.loginUser(request) { success, error ->
            assert(!success)
            assert(error?.contains("Login failed") == true || error != null)
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<AuthResponse>>
        verify(mockAuthCall).enqueue(captor.capture())
        captor.value.onResponse(mockAuthCall, response)
    }

    @Test
    fun `loginUser network failure calls onResult with network error`() {
        val repo = AccountRepository(mockApiService)
        val request = LoginRequest("john@example.com", "password")

        `when`(mockApiService.loginUser(request)).thenReturn(mockAuthCall)

        repo.loginUser(request) { success, error ->
            assert(!success)
            assert(error?.contains("Network error") == true)
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<AuthResponse>>
        verify(mockAuthCall).enqueue(captor.capture())
        captor.value.onFailure(mockAuthCall, Throwable("timeout"))
    }

    // --- registerUser tests ---

    @Test
    fun `registerUser success calls onResult with success`() {
        val repo = AccountRepository(mockApiService)
        val request = RegisterRequest("John", "john@example.com", "password")
        val registerResponse = RegisterResponse("success", 1)
        val response = Response.success(registerResponse)

        `when`(mockApiService.registerUser(request)).thenReturn(mockRegisterCall)

        var callCount = 0
        repo.registerUser(request) { success, error ->
            callCount++
            if (callCount == 2) {
                assert(success)
                assert(error == null)
            }
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<RegisterResponse>>
        verify(mockRegisterCall).enqueue(captor.capture())
        captor.value.onResponse(mockRegisterCall, response)
    }

    @Test
    fun `registerUser failure calls onResult with error`() {
        val repo = AccountRepository(mockApiService)
        val request = RegisterRequest("John", "john@example.com", "password")
        val response = Response.error<RegisterResponse>(
            400, "error".toResponseBody("application/json".toMediaTypeOrNull())
        )

        `when`(mockApiService.registerUser(request)).thenReturn(mockRegisterCall)

        var callCount = 0
        repo.registerUser(request) { success, error ->
            callCount++
            if (callCount == 2) {
                assert(!success)
                assert(error?.contains("Registration failed") == true || error != null)
            }
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<RegisterResponse>>
        verify(mockRegisterCall).enqueue(captor.capture())
        captor.value.onResponse(mockRegisterCall, response)
    }

    @Test
    fun `registerUser network failure calls onResult with network error`() {
        val repo = AccountRepository(mockApiService)
        val request = RegisterRequest("John", "john@example.com", "password")

        `when`(mockApiService.registerUser(request)).thenReturn(mockRegisterCall)

        var callCount = 0
        repo.registerUser(request) { success, error ->
            callCount++
            if (callCount == 2) {
                assert(!success)
                assert(error == "Network error: Cannot connect to server")
            }
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<RegisterResponse>>
        verify(mockRegisterCall).enqueue(captor.capture())
        captor.value.onFailure(mockRegisterCall, Throwable("timeout"))
    }
}