package put.inf154030.frog.repository

import put.inf154030.frog.models.requests.DeviceTokenRequest
import put.inf154030.frog.models.responses.MessageResponse
import put.inf154030.frog.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsRepository {
    fun updateDeviceToken(
        tokenRequest: DeviceTokenRequest,
        onResult: (errorMessage: String?) -> Unit
    ) {
        ApiClient.apiService.updateDeviceToken(tokenRequest)
            .enqueue(object : Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(null)
                    } else {
                        onResult(response.errorBody()?.string() ?: "Failed to send token")
                    }
                }

                override fun onFailure(
                    call: Call<MessageResponse>,
                    t: Throwable
                ) {
                    onResult("Network error: ${t.message}")
                }
            })
    }
}