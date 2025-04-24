package put.inf154030.frog.models.responses

import put.inf154030.frog.models.ContainerSpecies
import put.inf154030.frog.models.LocationReference
import put.inf154030.frog.models.Parameter

data class ContainerDetailResponse(
    val id: Int,
    val name: String,
    val type: String,
    val description: String?,
    val active: Boolean,
    val created_at: String,
    val location: LocationReference,
    val parameters: List<Parameter>,
    val species: List<ContainerSpecies>
)
