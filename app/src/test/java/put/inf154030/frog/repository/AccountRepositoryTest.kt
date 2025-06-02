package put.inf154030.frog.repository

import android.annotation.SuppressLint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.never
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import put.inf154030.frog.models.User
import put.inf154030.frog.models.requests.LoginRequest
import put.inf154030.frog.models.requests.RegisterRequest
import put.inf154030.frog.models.requests.UserUpdateRequest
import put.inf154030.frog.models.responses.AuthResponse
import put.inf154030.frog.models.responses.RegisterResponse
import put.inf154030.frog.models.responses.UserResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.network.ApiService
import put.inf154030.frog.network.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

@RunWith(MockitoJUnitRunner::class)
class AccountRepositoryTest {

    @Mock
    private lateinit var mockApiService: ApiService

    @Mock
    private lateinit var mockUpdateCall: Call<UserResponse>

    @Mock
    private lateinit var mockLoginCall: Call<AuthResponse>

    @Mock
    private lateinit var mockRegisterCall: Call<RegisterResponse>

    @Mock
    private lateinit var mockResponseBody: ResponseBody

    private lateinit var accountRepository: AccountRepository

    @SuppressLint("CheckResult")
    @Before
    fun setup() {
        // Setup regular mocks first
        whenever(mockApiService.updateUser(any())).thenReturn(mockUpdateCall)
        whenever(mockApiService.loginUser(any())).thenReturn(mockLoginCall)
        whenever(mockApiService.registerUser(any())).thenReturn(mockRegisterCall)

        // Setup SessionManager static mock properly
        val sessionManagerMock = mockStatic(SessionManager::class.java)

        // Setup ApiClient static mock properly
        val apiClientMock = mockStatic(ApiClient::class.java)
        apiClientMock.`when`<ApiService> { ApiClient.apiService }.thenReturn(mockApiService)

        // Create repository instance
        accountRepository = AccountRepository()
    }

    @Test
    fun `updateUser calls API with correct parameters`() {
        // Given
        val request = UserUpdateRequest("John Doe", "john@example.com")

        // When
        accountRepository.updateUser(request) { _, _ -> }

        // Then
        verify(mockApiService).updateUser(request)
        verify(mockUpdateCall).enqueue(any())
    }

    @Test
    fun `updateUser handles success response correctly`() {
        // Given
        val request = UserUpdateRequest("John Doe", "john@example.com")
        val response = UserResponse(1, "John Doe", "john@example.com", "user")

        // Capture the callback
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<UserResponse>>(0)
            callback.onResponse(mockUpdateCall, Response.success(response))
            null
        }.whenever(mockUpdateCall).enqueue(any())

        // When
        var resultSuccess = false
        var resultError: String? = "not set"
        accountRepository.updateUser(request) { success, error ->
            resultSuccess = success
            resultError = error
        }

        // Then
        verify(SessionManager).saveUpdatedUserInfo("John Doe", "john@example.com")
        assert(resultSuccess)
        assert(resultError == null)
    }

    @Test
    fun `updateUser handles error response correctly`() {
        // Given
        val request = UserUpdateRequest("John Doe", "john@example.com")

        // Create a proper mock for ResponseBody instead of using the @Mock field
        val mockErrorBody = okhttp3.ResponseBody.create(
            "application/json".toMediaTypeOrNull(),
            "{\"error\":\"Invalid data\"}"
        )

        // Capture the callback
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<UserResponse>>(0)
            callback.onResponse(mockUpdateCall, Response.error(400, mockErrorBody))
            null
        }.whenever(mockUpdateCall).enqueue(any())

        // When
        var resultSuccess = true
        var resultError: String? = null
        accountRepository.updateUser(request) { success, error ->
            resultSuccess = success
            resultError = error
        }

        // Then
        verify(SessionManager, never()).saveUpdatedUserInfo(any(), any())
        assert(!resultSuccess)
        assert(resultError == "Could not update user data")
    }

    @Test
    fun `updateUser handles network failure correctly`() {
        // Given
        val request = UserUpdateRequest("John Doe", "john@example.com")
        val exception = IOException("Network error")

        // Capture the callback
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<UserResponse>>(0)
            callback.onFailure(mockUpdateCall, exception)
            null
        }.whenever(mockUpdateCall).enqueue(any())

        // When
        var resultSuccess = true
        var resultError: String? = null
        accountRepository.updateUser(request) { success, error ->
            resultSuccess = success
            resultError = error
        }

        // Then
        assert(!resultSuccess)
        assert(resultError?.contains("Network error") == true)
    }

    @Test
    fun `loginUser calls API with correct parameters`() {
        // Given
        val request = LoginRequest("john@example.com", "password123")

        // When
        accountRepository.loginUser(request) { _, _ -> }

        // Then
        verify(mockApiService).loginUser(request)
        verify(mockLoginCall).enqueue(any())
    }

    @Test
    fun `loginUser handles successful login`() {
        // Given
        val request = LoginRequest("john@example.com", "password123")
        val user = User(1, "John Doe", "john@example.com", "user")
        val authResponse = AuthResponse("token123", user)

        // Capture the callback
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<AuthResponse>>(0)
            callback.onResponse(mockLoginCall, Response.success(authResponse))
            null
        }.whenever(mockLoginCall).enqueue(any())

        // When
        var resultSuccess = false
        var resultError: String? = "not set"
        accountRepository.loginUser(request) { success, error ->
            resultSuccess = success
            resultError = error
        }

        // Then
        verify(SessionManager).saveAuthToken("token123")
        verify(SessionManager).saveUserInfo("1", "John Doe", "john@example.com")
        assert(resultSuccess)
        assert(resultError == null)
    }

    @Test
    fun `registerUser starts with loading state`() {
        // Given
        val request = RegisterRequest("John Doe", "john@example.com", "password123")

        // When
        var resultSuccess = true
        var resultError: String? = "not checked yet"
        accountRepository.registerUser(request) { success, error ->
            // This should be called immediately with loading state
            if (resultError == "not checked yet") {
                resultSuccess = success
                resultError = error
            }
        }

        // Then
        assert(!resultSuccess)
        assert(resultError == null)
    }

    @Test
    fun `registerUser handles successful registration`() {
        // Given
        val request = RegisterRequest("John Doe", "john@example.com", "password123")
        val response = RegisterResponse("Registration successful", 1)

        // Capture the callback
        doAnswer { invocation ->
            val callback = invocation.getArgument<Callback<RegisterResponse>>(0)
            callback.onResponse(mockRegisterCall, Response.success(response))
            null
        }.whenever(mockRegisterCall).enqueue(any())

        // When
        var finalSuccess = false
        accountRepository.registerUser(request) { success, _ ->
            // We're only interested in the final callback after API response
            finalSuccess = success
        }

        // Then
        assert(finalSuccess)
    }
}