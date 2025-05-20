package put.inf154030.frog.models.requests

import com.google.gson.annotations.SerializedName

data class ParameterValueUpdateRequest(
    val value: Double,
    @SerializedName("device_id") val deviceId: String
)
