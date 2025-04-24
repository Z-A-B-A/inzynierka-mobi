package put.inf154030.frog.models.responses

data class ContainerSpeciesUpdateResponse(
    val id: Int,
    val container_id: Int,
    val species_id: Int,
    val count: Int,
    val updated_at: String
)
