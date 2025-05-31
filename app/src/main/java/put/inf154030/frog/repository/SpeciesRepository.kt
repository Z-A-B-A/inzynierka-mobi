package put.inf154030.frog.repository

import put.inf154030.frog.models.requests.UpdateSpeciesCountRequest
import put.inf154030.frog.models.responses.ContainerSpeciesUpdateResponse
import put.inf154030.frog.models.responses.MessageResponse
import put.inf154030.frog.network.ApiClient

class SpeciesRepository {
    fun deleteSpeciesFromContainer(
        containerId: Int,
        speciesId: Int,
        onResult: (
            success: Boolean,
            isLoading: Boolean,
            errorMessage: String?
        ) -> Unit
    ) {
        // Indicate loading started
        onResult(false, true, null)
        ApiClient.apiService.deleteSpeciesFromContainer(containerId, speciesId)
            .enqueue(object : retrofit2.Callback<MessageResponse> {
                override fun onResponse(
                    call: retrofit2.Call<MessageResponse>,
                    response: retrofit2.Response<MessageResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true, false, null)
                    } else {
                        onResult(false, false, "Failed to delete species: ${response.message()}")
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<MessageResponse>,
                    t: Throwable
                ) {
                    onResult(false, false, t.message)
                }
            })
    }

    fun updateContainerSpecies(
        containerId: Int,
        speciesId: Int,
        updateRequest: UpdateSpeciesCountRequest,
        onResult: (
            success: Boolean,
            failure: Boolean,
            isLoading: Boolean,
            errorMessage: String?
        ) -> Unit
    ) {
        // Indicate loading started
        onResult(false, false,true, null)
        ApiClient.apiService.updateContainerSpecies(containerId, speciesId, updateRequest)
            .enqueue(object : retrofit2.Callback<ContainerSpeciesUpdateResponse> {
                override fun onResponse(
                    call: retrofit2.Call<ContainerSpeciesUpdateResponse>,
                    response: retrofit2.Response<ContainerSpeciesUpdateResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true, false,false, null)
                    } else {
                        onResult(false, false,false, "Failed to update species: ${response.message()}")
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<ContainerSpeciesUpdateResponse>,
                    t: Throwable
                ) {
                    onResult(false, true,false, "Network error: ${t.message}")
                }
            })
    }
}