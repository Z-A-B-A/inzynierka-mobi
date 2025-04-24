package put.inf154030.frog.models.responses

data class ScheduleResponse(
    val id: Int,
    val name: String,
    val start_date: String,
    val frequency: String,
    val weekdays: String,
    val execution_time: String,
    val created_at: String
)
