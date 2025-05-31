package put.inf154030.frog.repository

import put.inf154030.frog.models.Location
import put.inf154030.frog.models.responses.LocationsResponse
import put.inf154030.frog.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationsRepository {
    fun getLocations(
        onResult: (
            success: Boolean,
            isLoading: Boolean,
            locations: List<Location>?,
            errorMessage: String?
        ) -> Unit
    ) {
        // Indicate loading started
        onResult(false, true, null, null)
        ApiClient.apiService.getLocations()
            .enqueue(object : Callback<LocationsResponse> {
            override fun onResponse(
                call: Call<LocationsResponse>,
                response: Response<LocationsResponse>
            ) {
                if (response.isSuccessful) {
                    onResult(true, false, response.body()?.locations ?: emptyList(), null)
                } else {
                    onResult(false, false, null, "Failed to load locations: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LocationsResponse>, t: Throwable) {
                onResult(false, false, null, "Network error: ${t.message}")
            }
        })
    }
}