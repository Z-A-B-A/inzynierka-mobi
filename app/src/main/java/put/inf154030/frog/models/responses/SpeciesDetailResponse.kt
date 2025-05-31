package put.inf154030.frog.models.responses

import com.google.gson.annotations.SerializedName
import put.inf154030.frog.models.CompatibleSpecies
import put.inf154030.frog.models.IncompatibleSpecies

// SpeciesDetailResponse
data class SpeciesDetailResponse(
    val id: Int,
    val name: String,
    @SerializedName("scientific_name") val scientificName: String,
    val description: String,
    val category: String,
    val predefined: Boolean,
    @SerializedName("compatible_species") val compatibleSpecies: List<CompatibleSpecies>?,
    @SerializedName("incompatible_species") val incompatibleSpecies: List<IncompatibleSpecies>?
)
