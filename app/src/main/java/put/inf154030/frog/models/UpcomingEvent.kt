package put.inf154030.frog.models

import com.google.gson.annotations.SerializedName

data class UpcomingEvent(
    val id: Int,
    @SerializedName("schedule_id") val scheduleId: Int,
    val container: ContainerReference,
    @SerializedName("event_name") val eventName: String,
    @SerializedName("scheduled_for") val scheduledFor: String
)
