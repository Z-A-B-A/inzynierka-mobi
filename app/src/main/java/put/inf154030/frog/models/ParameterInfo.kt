package put.inf154030.frog.models

import com.google.gson.annotations.SerializedName

data class ParameterInfo(
    val name: String,
    val unit: String,
    @SerializedName("parameter_type") val parameterType: String
)
