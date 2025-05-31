package put.inf154030.frog.models.responses

import put.inf154030.frog.models.User

// AuthResponse
data class AuthResponse(
    val token: String,
    val user: User
)
