package put.inf154030.frog.models.responses

import com.google.gson.annotations.SerializedName
import put.inf154030.frog.models.Notification

data class NotificationsResponse(
    val notifications: List<Notification>? = null,
    @SerializedName("unread_count") val unreadCount: Int
)
