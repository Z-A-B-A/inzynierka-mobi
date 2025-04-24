package put.inf154030.frog.models

data class Schedule(
    val id: Int,
    val name: String,
    val start_date: String,
    val frequency: String,
    val weekdays: String,
    val execution_time: String,
    val last_executed: String?,
    val created_at: String
)
