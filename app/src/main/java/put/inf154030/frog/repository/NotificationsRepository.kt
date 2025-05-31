package put.inf154030.frog.repository

import put.inf154030.frog.models.Notification
import put.inf154030.frog.models.requests.DeviceTokenRequest
import put.inf154030.frog.models.responses.MessageResponse
import put.inf154030.frog.models.responses.NotificationMarkAllReadResponse
import put.inf154030.frog.models.responses.NotificationUpdateResponse
import put.inf154030.frog.models.responses.NotificationsResponse
import put.inf154030.frog.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsRepository {
    fun updateDeviceToken(
        request: DeviceTokenRequest,
        onResult: (errorMessage: String?) -> Unit
    ) {
        ApiClient.apiService.updateDeviceToken(request)
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

//    TODO("Czekam na query wtedy w notifications zrobię to_date do teraz i będzie git")
    fun getNotifications(
        unreadOnly: Boolean = true,
        onResult: (notifications: List<Notification>?, errorMessage: String?) -> Unit
    ) {
        ApiClient.apiService.getNotifications(unreadOnly)
            .enqueue(object : Callback<NotificationsResponse> {
                override fun onResponse(
                    call: Call<NotificationsResponse>,
                    response: Response<NotificationsResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(response.body()?.notifications ?: emptyList(), null)
                    } else {
                        onResult(null, "Failed to load notifications")
                    }
                }

                override fun onFailure(call: Call<NotificationsResponse>, t: Throwable) {
                    onResult(null, t.message ?: "Network error")
                }
            })
    }

    fun markNotificationAsRead(
        notificationId: Int,
        onResult: (success: Boolean, errorMessage: String?) -> Unit
    ) {
        ApiClient.apiService.markNotificationAsRead(notificationId)
            .enqueue(object : Callback<NotificationUpdateResponse> {
                override fun onResponse(
                    call: Call<NotificationUpdateResponse>,
                    response: Response<NotificationUpdateResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true, null)
                    } else {
                        onResult(false, "Failed to mark notification as read")
                    }
                }

                override fun onFailure(
                    call: Call<NotificationUpdateResponse>,
                    t: Throwable
                ) {
                    onResult(false, t.message ?: "Network error")
                }
            })
    }

    fun markAllNotificationsAsRead(
        onResult: (success: Boolean, errorMessage: String?) -> Unit
    ) {
        ApiClient.apiService.markAllNotificationsAsRead()
            .enqueue(object : Callback<NotificationMarkAllReadResponse> {
                override fun onResponse(
                    call: Call<NotificationMarkAllReadResponse>,
                    response: Response<NotificationMarkAllReadResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true, null)
                    } else {
                        onResult(false, "Failed to mark all notifications as read")
                    }
                }

                override fun onFailure(
                    call: Call<NotificationMarkAllReadResponse>,
                    t: Throwable
                ) {
                    onResult(false, t.message ?: "Network error")
                }
            })
    }
}