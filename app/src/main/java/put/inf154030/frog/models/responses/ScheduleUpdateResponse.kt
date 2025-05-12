package put.inf154030.frog.models.responses

data class ScheduleUpdateResponse(
    val id: Int,
    val name: String,
    val execution_time: String,
    val active: Boolean,
    val updatedAt: String
)
