package put.inf154030.frog.models.responses

import com.google.gson.annotations.SerializedName

// ContainerResponse
data class ContainerResponse(
    val id: Int,
    val name: String,
    val type: String,
    val description: String?,
    val active: Boolean,
    @SerializedName("created_at") val createdAt: String
)
