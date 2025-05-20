package put.inf154030.frog.models.requests

import com.google.gson.annotations.SerializedName

data class ContainerUpdateRequest(
    val name: String?,
    val description: String?,
    val active: Boolean?,
    @SerializedName("location_id") val locationId: Int?
)
