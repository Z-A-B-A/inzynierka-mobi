package put.inf154030.frog.models.requests

data class ContainerCreateRequest(
    val name: String,
    val description: String?,
    val code: String
)
