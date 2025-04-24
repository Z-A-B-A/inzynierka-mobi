package put.inf154030.frog.models.responses

data class LocationDetailResponse(
    val id: Int,
    val name: String,
    val created_at: String,
    val containers_count: Int
)
