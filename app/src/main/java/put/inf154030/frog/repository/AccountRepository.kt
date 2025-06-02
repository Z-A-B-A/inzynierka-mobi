package put.inf154030.frog.repository

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

class AccountRepository (
    private val apiService: ApiService = ApiClient.apiService
) {
    fun updateUser (
        request: UserUpdateRequest,
        onResult: (
            success: Boolean,
            errorMessage: String? ) -> Unit
    ) {
        apiService.updateUser(request)
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        // Update session information
                        response.body()?.let {
                            SessionManager.saveUpdatedUserInfo(
                                it.name,
                                it.email
                            )
                        }
                        onResult(
                            true,
                            null
                        )
                    } else {
                        onResult(
                            false,
                            "Could not update user data"
                        )
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    onResult(
                        false,
                        "Network error: ${t.message}"
                    )
                }
            })
    }

    fun loginUser(
        request: LoginRequest,
        onResult: (
            success: Boolean,
            errorMessage: String?) -> Unit
    ) {
        apiService.loginUser(request)
            .enqueue(object : Callback<AuthResponse> {
                override fun onResponse(
                    call: Call<AuthResponse>,
                    response: Response<AuthResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { authResponse ->
                            SessionManager.saveAuthToken(authResponse.token)
                            authResponse.user.let { user ->
                                SessionManager.saveUserInfo(
                                    user.id.toString(),
                                    user.name,
                                    user.email
                                )
                            }
                            onResult(true, null)
                        } ?: onResult(false, "Empty response received")
                    } else {
                        onResult(false,response.errorBody()?.string() ?: "Login failed")
                    }
                }

                override fun onFailure(
                    call: Call<AuthResponse>,
                    t: Throwable
                ) {
                    onResult(false, "Network error: Cannot connect to server")
                }
            })
    }

    fun registerUser(
        request: RegisterRequest,
        onResult: (success: Boolean, errorMessage: String?) -> Unit
    ) {
        onResult(false,  null) // Loading started
        apiService.registerUser(request)
            .enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true,  null)
                    } else {
                        onResult(false,  response.errorBody()?.string() ?: "Registration failed")
                    }
                }
                override fun onFailure(
                    call: Call<RegisterResponse>,
                    t: Throwable
                ) {
                    onResult(false,  "Network error: Cannot connect to server")
                }
            })
    }
}