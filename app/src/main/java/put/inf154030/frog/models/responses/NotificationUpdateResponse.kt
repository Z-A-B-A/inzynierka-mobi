package put.inf154030.frog.models.responses

import com.google.gson.annotations.SerializedName

// NotificationUpdateResponse
data class NotificationUpdateResponse(
    val id: Int,
    val read: Boolean,
    @SerializedName("updated_at") val updatedAt: String
)
