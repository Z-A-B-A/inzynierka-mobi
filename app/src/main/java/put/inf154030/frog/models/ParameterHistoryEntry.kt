package put.inf154030.frog.models

import com.google.gson.annotations.SerializedName

data class ParameterHistoryEntry(
    val value: Double,
    @SerializedName("recorded_at") val recordedAt: String
)
