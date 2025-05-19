package put.inf154030.frog.models.responses

import com.google.gson.annotations.SerializedName

data class ParameterResponse(
    val id: Int,
    val name: String,
    val unit: String,
    @SerializedName("min_value") val minValue: Double?,
    @SerializedName("max_value") val maxValue: Double?,
    @SerializedName("is_controlled") val isControlled: Boolean,
    @SerializedName("parameter_type") val parameterType: String,
    @SerializedName("created_at") val createdAt: String?
)
