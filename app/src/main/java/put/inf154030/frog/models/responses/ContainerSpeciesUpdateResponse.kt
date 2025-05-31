package put.inf154030.frog.models.responses

import com.google.gson.annotations.SerializedName

// ContainerSpeciesUpdateResponse
data class ContainerSpeciesUpdateResponse(
    @SerializedName("container_id") val containerId: Int,
    @SerializedName("species_id") val speciesId: Int,
    val count: Int,
    @SerializedName("updated_at") val updatedAt: String
)
