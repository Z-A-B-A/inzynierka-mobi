package put.inf154030.frog.models.responses

import put.inf154030.frog.models.Species

data class SpeciesListResponse(
    val species: List<Species>
)
