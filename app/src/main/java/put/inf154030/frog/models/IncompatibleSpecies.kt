package put.inf154030.frog.models

data class IncompatibleSpecies(
    val id: Int,
    val name: String,
    val compatible: Boolean,
    val notes: String?
)
