package put.inf154030.frog.models.requests

data class ContainerCreateRequest(
    val name: String,
    val type: String,
    val description: String?
)
