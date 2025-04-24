package put.inf154030.frog.models.requests

data class ScheduleUpdateRequest(
    val execution_time: String?,
    val active: Boolean?
)
