package put.inf154030.frog.models.responses

import com.google.gson.annotations.SerializedName

data class ParameterResponse(
    @SerializedName("container_id") val containerId: Int,
    val name: String,
    val unit: String,
    @SerializedName("min_value") val minValue: Double?,
    @SerializedName("max_value") val maxValue: Double?,
    @SerializedName("parameter_type") val parameterType: String,
    @SerializedName("updated_at") val timestamp: String?
)
