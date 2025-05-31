package put.inf154030.frog.models.requests

import com.google.gson.annotations.SerializedName

// ScheduleCreateRequest
data class ScheduleCreateRequest(
    val name: String,
    @SerializedName("start_date") val startDate: String,
    val frequency: String,
    val weekdays: String,
    @SerializedName("execution_time") val executionTime: String
)
