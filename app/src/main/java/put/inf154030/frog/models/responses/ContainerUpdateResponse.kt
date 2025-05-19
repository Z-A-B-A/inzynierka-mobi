package put.inf154030.frog.models.responses

import com.google.gson.annotations.SerializedName

data class ContainerUpdateResponse(
    val id: Int,
    val name: String,
    val type: String,
    val description: String?,
    val active: Boolean,
    @SerializedName("created_at") val createdAt: String,
    val updatedAt: String
)
