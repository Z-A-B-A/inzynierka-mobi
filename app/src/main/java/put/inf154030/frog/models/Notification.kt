package put.inf154030.frog.models

import com.google.gson.annotations.SerializedName

data class Notification(
    val id: Int,
    val message: String,
    val read: Boolean,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("execution_time") val executionTime: String,
    @SerializedName("schedule_id") val scheduleId: Int?,
    val schedule: ScheduleReference?,
    val container: ContainerReference?
)
