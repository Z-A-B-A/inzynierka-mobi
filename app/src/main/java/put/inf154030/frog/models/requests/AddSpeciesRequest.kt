package put.inf154030.frog.models.requests

import com.google.gson.annotations.SerializedName

data class AddSpeciesRequest(
    @SerializedName("species_id") val speciesId: Int,
    val count: Int
)
