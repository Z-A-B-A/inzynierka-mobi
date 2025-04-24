package put.inf154030.frog.models.responses

import put.inf154030.frog.models.CompatibleSpecies
import put.inf154030.frog.models.IncompatibleSpecies

data class SpeciesDetailResponse(
    val id: Int,
    val name: String,
    val scientific_name: String,
    val description: String,
    val category: String,
    val predefined: Boolean,
    val compatible_species: List<CompatibleSpecies>?,
    val incompatible_species: List<IncompatibleSpecies>?
)
