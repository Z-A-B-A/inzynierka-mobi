package put.inf154030.frog.repository

import put.inf154030.frog.models.requests.ContainerCreateRequest
import put.inf154030.frog.models.responses.ContainerDetailResponse
import put.inf154030.frog.models.responses.ContainerResponse
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
            isLoading: Boolean,
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
                            false,
                            null
                        )
                    } else {
                        onResult(
                            false,
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
                        false,
                        "Network error: ${t.message}"
                    )
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
}