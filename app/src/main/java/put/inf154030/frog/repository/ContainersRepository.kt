package put.inf154030.frog.repository

import put.inf154030.frog.models.Container
import put.inf154030.frog.models.requests.ContainerCreateRequest
import put.inf154030.frog.models.requests.ContainerUpdateRequest
import put.inf154030.frog.models.responses.ContainerDetailResponse
import put.inf154030.frog.models.responses.ContainerResponse
import put.inf154030.frog.models.responses.ContainerUpdateResponse
import put.inf154030.frog.models.responses.ContainersResponse
import put.inf154030.frog.models.responses.MessageResponse
import put.inf154030.frog.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContainersRepository {
    fun createContainer(
        request: ContainerCreateRequest,
        locationId: Int,
        onResult: (
            success: Boolean,
            errorMessage: String? ) -> Unit
    ) {
        // Make API call to create the container
        ApiClient.apiService.createContainer(locationId, request)
            .enqueue(object : Callback<ContainerResponse> {
                override fun onResponse(
                    call: Call<ContainerResponse>,
                    response: Response<ContainerResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(
                            true,
                            null
                        )
                    } else {
                        onResult(
                            false,
                            "Failed to create container: ${response.message()}"
                        )
                    }
                }

                override fun onFailure(
                    call: Call<ContainerResponse>,
                    t: Throwable
                ) {
                    onResult(
                        false,
                        "Network error: ${t.message}"
                    )
                }
            })
    }

    fun getContainers(
        locationId: Int,
        onResult: (
            success: Boolean,
            containers: List<Container>?,
            errorMessage: String?
        ) -> Unit
    ) {
        // Indicate loading started
        onResult(false, null, null)
        ApiClient.apiService.getContainers(locationId)
            .enqueue(object : Callback<ContainersResponse> {
                override fun onResponse(
                    call: Call<ContainersResponse>,
                    response: Response<ContainersResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true, response.body()?.containers ?: emptyList(), null)
                    } else {
                        onResult(false, null, "Failed to load containers: ${response.message()}")
                    }
                }

                override fun onFailure(
                    call: Call<ContainersResponse>,
                    t: Throwable
                ) {
                    onResult(false, null, "Network error: ${t.message}")
                }
            })
    }

    fun getContainerDetails(
        containerId: Int,
        onResult: (
            success: Boolean,
            response: ContainerDetailResponse?,
            errorMessage: String?
        ) -> Unit
    ) {
        ApiClient.apiService.getContainerDetails(containerId)
            .enqueue(object : Callback<ContainerDetailResponse> {
                override fun onResponse(
                    call: Call<ContainerDetailResponse>,
                    response: Response<ContainerDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true, response.body(), null)
                    } else {
                        onResult(false, null, "Failed to load container details: ${response.message()}")
                    }
                }

                override fun onFailure(
                    call: Call<ContainerDetailResponse>,
                    t: Throwable
                ) {
                    onResult(false, null, "Network error: ${t.message}")
                }
            })
    }

    fun deleteContainer(
        containerId: Int,
        onResult: (
            success: Boolean,
            errorMessage: String?
        ) -> Unit
    ) {
        // Indicate loading started
        onResult(false,  null)
        ApiClient.apiService.deleteContainer(containerId)
            .enqueue(object : Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true,  null)
                    } else {
                        val error = response.errorBody()?.string() ?: "Unknown error"
                        onResult(false, error)
                    }
                }

                override fun onFailure(
                    call: Call<MessageResponse>,
                    t: Throwable
                ) {
                    onResult(false, t.message)
                }
            })
    }

    fun updateContainer(
        containerId: Int,
        containerUpdateRequest: ContainerUpdateRequest,
        onResult: (
            success: Boolean,
            errorMessage: String?
        ) -> Unit
    ) {
        // Indicate loading started
        onResult(false, null)
        ApiClient.apiService.updateContainer(containerId, containerUpdateRequest)
            .enqueue(object : Callback<ContainerUpdateResponse> {
                override fun onResponse(
                    call: Call<ContainerUpdateResponse>,
                    response: Response<ContainerUpdateResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true, null)
                    } else {
                        onResult(false, "Failed to update container: ${response.message()}")
                    }
                }

                override fun onFailure(
                    call: Call<ContainerUpdateResponse>,
                    t: Throwable
                ) {
                    onResult(false, "Network error: ${t.message}")
                }
            })
    }
}