package put.inf154030.frog.models.responses

import com.google.gson.annotations.SerializedName

data class LocationResponse(
    val id: Int,
    val name: String,
    @SerializedName("created_at") val createdAt: String
)
