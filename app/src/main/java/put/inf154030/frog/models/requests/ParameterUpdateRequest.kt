package put.inf154030.frog.models.requests

import com.google.gson.annotations.SerializedName

// ParameterUpdateRequest
data class ParameterUpdateRequest(
    @SerializedName("min_value") val minValue: Double?,
    @SerializedName("max_value") val maxValue: Double?,
)
