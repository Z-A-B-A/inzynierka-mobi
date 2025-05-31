package put.inf154030.frog.models.responses

import com.google.gson.annotations.SerializedName

// ScheduleResponse
data class ScheduleResponse(
    val id: Int,
    val name: String,
    @SerializedName("start_date") val startDate: String,
    val frequency: String,
    val weekdays: String,
    @SerializedName("execution_time") val executionTime: String,
    @SerializedName("created_at") val createdAt: String
)
