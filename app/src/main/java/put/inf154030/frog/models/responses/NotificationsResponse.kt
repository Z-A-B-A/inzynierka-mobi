package put.inf154030.frog.models.responses

import put.inf154030.frog.models.Notification

data class NotificationsResponse(
    val notifications: List<Notification>,
    val unread_count: Int
)
