package put.inf154030.frog.models

data class Notification(
    val id: Int,
    val message: String,
    val read: Boolean,
    val created_at: String,
    val schedule_id: Int?,
    val schedule: ScheduleReference?,
    val container: ContainerReference?
)
