package put.inf154030.frog.models.requests

data class ParameterUpdateRequest(
    val name: String?,
    val min_value: Double?,
    val max_value: Double?,
    val is_controlled: Boolean?
)
