package put.inf154030.frog.repository

import put.inf154030.frog.models.Location
import put.inf154030.frog.models.requests.LocationCreateRequest
import put.inf154030.frog.models.requests.LocationUpdateRequest
import put.inf154030.frog.models.responses.LocationDetailResponse
import put.inf154030.frog.models.responses.LocationResponse
import put.inf154030.frog.models.responses.LocationUpdateResponse
import put.inf154030.frog.models.responses.LocationsResponse
import put.inf154030.frog.models.responses.MessageResponse
import put.inf154030.frog.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationsRepository {
    fun getLocations(
        onResult: (
            success: Boolean,
            locations: List<Location>?,
            errorMessage: String?
        ) -> Unit
    ) {
        // Indicate loading started
        onResult(false, null, null)
        ApiClient.apiService.getLocations()
            .enqueue(object : Callback<LocationsResponse> {
            override fun onResponse(
                call: Call<LocationsResponse>,
                response: Response<LocationsResponse>
            ) {
                if (response.isSuccessful) {
                    onResult(true, response.body()?.locations ?: emptyList(), null)
                } else {
                    onResult(false, null, "Failed to load locations: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LocationsResponse>, t: Throwable) {
                onResult(false, null, "Network error: ${t.message}")
            }
        })
    }

    fun getLocation(
        locationId: Int,
        onResult: (
            success: Boolean,
            location: LocationDetailResponse?,
            errorMessage: String?
        ) -> Unit
    ) {
        // Indicate loading started
        onResult(false, null, null)
        ApiClient.apiService.getLocation(locationId)
            .enqueue(object : Callback<LocationDetailResponse> {
                override fun onResponse(
                    call: Call<LocationDetailResponse>,
                    response: Response<LocationDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true, response.body(), null)
                    } else {
                        onResult(false, null, "Failed to load location: ${response.message()}")
                    }
                }

                override fun onFailure(
                    call: Call<LocationDetailResponse>,
                    t: Throwable
                ) {
                    onResult(false, null, "Network error: ${t.message}")
                }
            })
    }

    fun createLocation(
        locationCreateRequest: LocationCreateRequest,
        onResult: (
            success: Boolean,
            errorMessage: String?
        ) -> Unit
    ) {
        // Indicate loading started
        onResult(false, null)
        ApiClient.apiService.createLocation(locationCreateRequest)
            .enqueue(object : Callback<LocationResponse> {
                override fun onResponse(
                    call: Call<LocationResponse>,
                    response: Response<LocationResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true, null)
                    } else {
                        onResult(false, "Failed to create location: ${response.message()}")
                    }
                }

                override fun onFailure(
                    call: Call<LocationResponse>,
                    t: Throwable
                ) {
                    onResult(false, "Network error: ${t.message}")
                }
            })
    }

    fun deleteLocation(
        locationId: Int,
        onResult: (
            success: Boolean,
            errorMessage: String?
        ) -> Unit
    ) {
        // Indicate loading started
        onResult(false, null)
        ApiClient.apiService.deleteLocation(locationId)
            .enqueue(object : Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true, null)
                    } else {
                        onResult(false, "Failed to delete location: ${response.message()}")
                    }
                }

                override fun onFailure(
                    call: Call<MessageResponse>,
                    t: Throwable
                ) {
                    onResult(false,  "Network error: ${t.message}")
                }
            })
    }

    fun updateLocation(
        locationId: Int,
        locationUpdateRequest: LocationUpdateRequest,
        onResult: (
            success: Boolean,
            errorMessage: String?
        ) -> Unit
    ) {
        // Indicate loading started
        onResult(false, null)
        ApiClient.apiService.updateLocation(locationId, locationUpdateRequest)
            .enqueue(object : Callback<LocationUpdateResponse> {
                override fun onResponse(
                    call: Call<LocationUpdateResponse>,
                    response: Response<LocationUpdateResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true, null)
                    } else {
                        onResult(false, "Failed to update location: ${response.message()}")
                    }
                }

                override fun onFailure(
                    call: Call<LocationUpdateResponse>,
                    t: Throwable
                ) {
                    onResult(false, "Network error: ${t.message}")
                }
            })
    }
}