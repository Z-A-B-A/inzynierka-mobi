package put.inf154030.frog.repository

import put.inf154030.frog.models.requests.UserUpdateRequest
import put.inf154030.frog.models.responses.UserResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.network.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountRepository {
    fun updateUser (
        request: UserUpdateRequest,
        onResult: (
            success: Boolean,
            isLoading: Boolean,
            errorMessage: String? ) -> Unit
    ) {
        ApiClient.apiService.updateUser(request)
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
                            false,
                            null
                        )
                    } else {
                        onResult(
                            false,
                            false,
                            "Could not update user data"
                        )
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    onResult(
                        false,
                        false,
                        "Network error: ${t.message}"
                    )
                }
            })
    }
}