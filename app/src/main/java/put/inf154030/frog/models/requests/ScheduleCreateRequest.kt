package put.inf154030.frog.models.requests

data class ScheduleCreateRequest(
    val name: String,
    val start_date: String,
    val frequency: String,
    val weekdays: String,
    val execution_time: String
)
