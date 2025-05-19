package put.inf154030.frog.models

import com.google.gson.annotations.SerializedName

data class Location(
    val id: Int,
    val name: String,
    @SerializedName("created_at") val createdAt: String
)
