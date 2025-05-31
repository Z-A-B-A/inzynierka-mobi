package put.inf154030.frog.models.responses

import com.google.gson.annotations.SerializedName
import put.inf154030.frog.models.SpeciesReference

// ContainerSpeciesItemResponse
data class ContainerSpeciesItemResponse(
    @SerializedName("container_id") val containerId: Int,
    val species: SpeciesReference,
    val count: Int,
    @SerializedName("added_at") val addedAt: String,
    @SerializedName("compatibility_warnings") val compatibilityWarnings: List<String>
)
