package put.inf154030.frog.models.responses

import put.inf154030.frog.models.ParameterHistoryEntry
import put.inf154030.frog.models.ParameterInfo

data class ParameterHistoryResponse(
    val parameter: ParameterInfo,
    val history: List<ParameterHistoryEntry>
)
