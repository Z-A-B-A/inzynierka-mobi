package put.inf154030.frog.models.responses

import com.google.gson.annotations.SerializedName
import put.inf154030.frog.models.ContainerSpecies
import put.inf154030.frog.models.LocationReference
import put.inf154030.frog.models.Parameter

// ContainerDetailResponse
data class ContainerDetailResponse(
    val id: Int,
    val name: String,
    val type: String,
    val description: String?,
    val active: Boolean,
    @SerializedName("created_at") val createdAt: String,
    val location: LocationReference,
    val parameters: List<Parameter>,
    val species: List<ContainerSpecies>?
)
