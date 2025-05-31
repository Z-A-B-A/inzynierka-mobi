package put.inf154030.frog.repository

import put.inf154030.frog.models.requests.ParameterUpdateRequest
import put.inf154030.frog.models.responses.ParameterHistoryResponse
import put.inf154030.frog.models.responses.ParameterResponse
import put.inf154030.frog.network.ApiClient

class ParametersRepository {
    fun getParameterHistory(
        containerId: Int,
        parameterType: String,
        fromDate: String?,
        toDate: String?,
        onResult: (
            success: Boolean,
            response: ParameterHistoryResponse?,
            errorMessage: String?
        ) -> Unit
    ) {
        ApiClient.apiService.getParameterHistory(containerId, parameterType, fromDate, toDate)
            .enqueue(object : retrofit2.Callback<ParameterHistoryResponse> {
                override fun onResponse(
                    call: retrofit2.Call<ParameterHistoryResponse>,
                    response: retrofit2.Response<ParameterHistoryResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true, response.body(), null)
                    } else {
                        onResult(false, null, "Failed to load parameter history: ${response.message()}")
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<ParameterHistoryResponse>,
                    t: Throwable
                ) {
                    onResult(false, null, "Network error: ${t.message}")
                }
            })
    }

    fun updateParameter(
        containerId: Int,
        parameterType: String,
        updateRequest: ParameterUpdateRequest,
        onResult: (
            success: Boolean,
            failure: Boolean,
            isLoading: Boolean,
            errorMessage: String?
        ) -> Unit
    ) {
        // Indicate loading started
        onResult(false, false, true, null)
        ApiClient.apiService.updateParameter(containerId, updateRequest, parameterType)
            .enqueue(object : retrofit2.Callback<ParameterResponse> {
                override fun onResponse(
                    call: retrofit2.Call<ParameterResponse>,
                    response: retrofit2.Response<ParameterResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true,  false, false, null)
                    } else {
                        onResult(false, false, false, "Failed to update parameter: ${response.message()}")
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<ParameterResponse>,
                    t: Throwable
                ) {
                    onResult(false, true, false, "Network error: ${t.message}")
                }
            })
    }
}