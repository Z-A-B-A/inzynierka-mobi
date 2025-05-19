package put.inf154030.frog.models

import com.google.gson.annotations.SerializedName

data class Species(
    val id: Int,
    val name: String,
    @SerializedName("scientific_name") val scientificName: String,
    val description: String,
    val category: String,
    val predefined: Boolean
)
