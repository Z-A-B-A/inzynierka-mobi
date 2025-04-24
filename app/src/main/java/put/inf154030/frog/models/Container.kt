package put.inf154030.frog.models

data class Container(
    val id: Int,
    val name: String,
    val type: String,
    val description: String?,
    val active: Boolean,
    val created_at: String
)
