package put.inf154030.frog.models.requests

import com.google.gson.annotations.SerializedName

// DeviceTokenRequest
data class DeviceTokenRequest(
    @SerializedName("device_token") val deviceToken: String
)
