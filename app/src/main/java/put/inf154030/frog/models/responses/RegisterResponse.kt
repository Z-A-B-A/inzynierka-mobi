package put.inf154030.frog.models.responses

import com.google.gson.annotations.SerializedName

// RegisterResponse
data class RegisterResponse(
    val message: String,
    @SerializedName("user_id") val userId: Int
)
