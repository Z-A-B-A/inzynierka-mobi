package put.inf154030.frog.models

data class Species(
    val id: Int,
    val name: String,
    val scientific_name: String,
    val description: String,
    val category: String,
    val predefined: Boolean
)
