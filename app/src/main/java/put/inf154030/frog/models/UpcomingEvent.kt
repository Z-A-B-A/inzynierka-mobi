package put.inf154030.frog.models

data class UpcomingEvent(
    val id: Int,
    val schedule_id: Int,
    val container: ContainerReference,
    val event_name: String,
    val scheduled_for: String
)
