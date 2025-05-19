package put.inf154030.frog.models

import com.google.gson.annotations.SerializedName

data class Schedule(
    val id: Int,
    val name: String,
    @SerializedName("start_date") val startDate: String,
    val frequency: String,
    val weekdays: String,
    @SerializedName("execution_time") val executionTime: String,
    @SerializedName("last_executed") val lastExecuted: String?,
    @SerializedName("created_at") val createdAt: String
)
