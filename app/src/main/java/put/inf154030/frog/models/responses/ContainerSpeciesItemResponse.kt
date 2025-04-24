package put.inf154030.frog.models.responses

import put.inf154030.frog.models.SpeciesReference

data class ContainerSpeciesItemResponse(
    val id: Int,
    val container_id: Int,
    val species: SpeciesReference,
    val count: Int,
    val added_at: String,
    val compatibility_warnings: List<String>
)
