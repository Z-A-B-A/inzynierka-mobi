package put.inf154030.frog.models.requests

import com.google.gson.annotations.SerializedName

data class ScheduleUpdateRequest(
    @SerializedName("execution_time") val executionTime: String?,
    val active: Boolean?
)
