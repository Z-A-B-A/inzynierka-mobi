package put.inf154030.frog.repository

import put.inf154030.frog.models.Species
import put.inf154030.frog.models.requests.AddSpeciesRequest
import put.inf154030.frog.models.requests.UpdateSpeciesCountRequest
import put.inf154030.frog.models.responses.ContainerSpeciesItemResponse
import put.inf154030.frog.models.responses.ContainerSpeciesUpdateResponse
import put.inf154030.frog.models.responses.MessageResponse
import put.inf154030.frog.models.responses.SpeciesListResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.network.ApiService

class SpeciesRepository (
    private val apiService: ApiService = ApiClient.apiService
) {
    fun deleteSpeciesFromContainer(
        containerId: Int,
        speciesId: Int,
        onResult: (
            success: Boolean,
            errorMessage: String?
        ) -> Unit
    ) {
        // Indicate loading started
        onResult(false, null)
        apiService.deleteSpeciesFromContainer(containerId, speciesId)
            .enqueue(object : retrofit2.Callback<MessageResponse> {
                override fun onResponse(
                    call: retrofit2.Call<MessageResponse>,
                    response: retrofit2.Response<MessageResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true, null)
                    } else {
                        onResult(false, "Failed to delete species: ${response.message()}")
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<MessageResponse>,
                    t: Throwable
                ) {
                    onResult(false, t.message)
                }
            })
    }

    fun updateContainerSpecies(
        containerId: Int,
        speciesId: Int,
        request: UpdateSpeciesCountRequest,
        onResult: (
            success: Boolean,
            failure: Boolean,
            errorMessage: String?
        ) -> Unit
    ) {
        // Indicate loading started
        onResult(false, false,null)
        apiService.updateContainerSpecies(containerId, speciesId, request)
            .enqueue(object : retrofit2.Callback<ContainerSpeciesUpdateResponse> {
                override fun onResponse(
                    call: retrofit2.Call<ContainerSpeciesUpdateResponse>,
                    response: retrofit2.Response<ContainerSpeciesUpdateResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true, false,null)
                    } else {
                        onResult(false, false,"Failed to update species: ${response.message()}")
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<ContainerSpeciesUpdateResponse>,
                    t: Throwable
                ) {
                    onResult(false, true,"Network error: ${t.message}")
                }
            })
    }

    fun addSpeciesToContainer(
        containerId: Int,
        request: AddSpeciesRequest,
        onResult: (success: Boolean, errorMessage: String?) -> Unit
    ) {
        apiService.addSpeciesToContainer(containerId, request)
            .enqueue(object : retrofit2.Callback<ContainerSpeciesItemResponse> {
                override fun onResponse(
                    call: retrofit2.Call<ContainerSpeciesItemResponse>,
                    response: retrofit2.Response<ContainerSpeciesItemResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true, null)
                    } else {
                        onResult(false, "Failed to add species: ${response.message()}")
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<ContainerSpeciesItemResponse>,
                    t: Throwable
                ) {
                    onResult(false, "Network error: ${t.message}")
                }
            })
    }

    fun getSpecies(
        category: String?,
        onResult: (
            success: Boolean,
            species: List<Species>?,
            errorMessage: String?) -> Unit
    ) {
        apiService.getSpecies(category)
            .enqueue(object : retrofit2.Callback<SpeciesListResponse> {
                override fun onResponse(
                    call: retrofit2.Call<SpeciesListResponse>,
                    response: retrofit2.Response<SpeciesListResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true,response.body()?.species ?: emptyList(), null)
                    } else {
                        onResult(false,null, "Failed to load species: ${response.message()}")
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<SpeciesListResponse>,
                    t: Throwable
                ) {
                    onResult(false,null, "Network error: ${t.message}")
                }
            })
    }
}