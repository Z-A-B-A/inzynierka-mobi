package put.inf154030.frog.models.responses

import com.google.gson.annotations.SerializedName
import put.inf154030.frog.models.Parameter

data class PresetParametersResponse(
    @SerializedName("added_parameters") val addedParameters: List<Parameter>
)
