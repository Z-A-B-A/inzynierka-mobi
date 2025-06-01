package put.inf154030.frog.models.responses

import com.google.gson.annotations.SerializedName

// ParameterResponse
data class ParameterResponse(
    @SerializedName("container_id") val containerId: Int,
    @SerializedName("parameter_type") val parameterType: String,
    val name: String,
    val unit: String,
    @SerializedName("min_value") val minValue: Double?,
    @SerializedName("max_value") val maxValue: Double?,
    @SerializedName("updated_at") val timestamp: String?
)
