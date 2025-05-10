package put.inf154030.frog.models.requests

data class UserUpdateRequest(
    val name: String? = null,
    val email: String? = null
)