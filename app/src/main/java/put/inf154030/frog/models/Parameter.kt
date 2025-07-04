package put.inf154030.frog.models

import com.google.gson.annotations.SerializedName

data class Parameter(
    val name: String,
    @SerializedName("current_value") val currentValue: Double?,
    val unit: String,
    @SerializedName("min_value") val minValue: Double?,
    @SerializedName("max_value") val maxValue: Double?,
    @SerializedName("is_controlled") val isControlled: Boolean,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("parameter_type") val parameterType: String,
    @SerializedName("container") val containerId: Int?
)
