package put.inf154030.frog.models.requests

data class ParameterCreateRequest(
    val name: String,
    val unit: String,
    val min_value: Double?,
    val max_value: Double?,
    val is_controlled: Boolean,
    val parameter_type: String
)
