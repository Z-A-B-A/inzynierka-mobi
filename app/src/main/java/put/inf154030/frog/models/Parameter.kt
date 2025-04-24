package put.inf154030.frog.models

data class Parameter(
    val id: Int,
    val name: String,
    val current_value: Double?,
    val unit: String,
    val min_value: Double?,
    val max_value: Double?,
    val is_controlled: Boolean,
    val control_device: String?,
    val updated_at: String?,
    val parameter_type: String?
)
