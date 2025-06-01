package put.inf154030.frog.models.responses

import com.google.gson.annotations.SerializedName

// ContainerUpdateResponse
data class ContainerUpdateResponse(
    val id: Int,
    val name: String,
    val type: String,
    val description: String?,
    val active: Boolean,
    @SerializedName("location_id") val locationId: Int,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)
