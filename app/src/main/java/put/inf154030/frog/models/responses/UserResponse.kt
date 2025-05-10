package put.inf154030.frog.models.responses

data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val role: String
)