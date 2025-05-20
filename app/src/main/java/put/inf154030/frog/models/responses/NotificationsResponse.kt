package put.inf154030.frog.models.responses

import com.google.gson.annotations.SerializedName
import put.inf154030.frog.models.Notification

data class NotificationsResponse(
    val notifications: List<Notification>,
    @SerializedName("unread_count") val unreadCount: Int
)
