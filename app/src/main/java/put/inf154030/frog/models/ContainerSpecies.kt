package put.inf154030.frog.models

import com.google.gson.annotations.SerializedName

data class ContainerSpecies(
    val id: Int,
    @SerializedName("species_id") val speciesId: Int,
    val name: String,
    val count: Int,
    @SerializedName("added_at") val addedAt: String
)
