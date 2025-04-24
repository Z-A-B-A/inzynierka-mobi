package put.inf154030.frog.models.responses

data class ParameterValueResponse(
    val id: Int,
    val name: String,
    val current_value: Double,
    val updated_at: String,
    val status: String
)
