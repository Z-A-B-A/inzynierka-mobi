package put.inf154030.frog.models.responses

import com.google.gson.annotations.SerializedName

// LocationDetailResponse
data class LocationDetailResponse(
    val id: Int,
    val name: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("containers_count") val containersCount: Int
)
