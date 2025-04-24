package put.inf154030.frog.models.requests

data class ContainerUpdateRequest(
    val name: String?,
    val description: String?,
    val active: Boolean?
)
