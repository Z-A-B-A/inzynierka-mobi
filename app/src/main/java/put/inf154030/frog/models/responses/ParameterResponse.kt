package put.inf154030.frog.models.responses

data class ParameterResponse(
    val id: Int,
    val name: String,
    val unit: String,
    val min_value: Double?,
    val max_value: Double?,
    val is_controlled: Boolean,
    val parameter_type: String,
    val created_at: String?
)
