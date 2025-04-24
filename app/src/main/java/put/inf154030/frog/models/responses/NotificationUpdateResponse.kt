package put.inf154030.frog.models.responses

data class NotificationUpdateResponse(
    val id: Int,
    val read: Boolean,
    val updated_at: String
)
