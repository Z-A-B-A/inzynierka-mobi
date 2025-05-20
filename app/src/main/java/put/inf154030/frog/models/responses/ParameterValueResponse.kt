package put.inf154030.frog.models.responses

import com.google.gson.annotations.SerializedName

data class ParameterValueResponse(
    val id: Int,
    val name: String,
    @SerializedName("current_value") val currentValue: Double,
    @SerializedName("updated_at") val updatedAt: String,
    val status: String
)
