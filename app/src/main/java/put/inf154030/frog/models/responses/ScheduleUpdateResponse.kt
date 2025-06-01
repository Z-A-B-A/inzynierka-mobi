package put.inf154030.frog.models.responses

import com.google.gson.annotations.SerializedName

data class ScheduleUpdateResponse(
    val id: Int,
    val name: String,
    @SerializedName("execution_time") val executionTime: String,
    @SerializedName("updated_at") val updatedAt: String
)
