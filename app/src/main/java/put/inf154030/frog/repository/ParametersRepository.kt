package put.inf154030.frog.repository

import put.inf154030.frog.models.responses.ParameterHistoryResponse
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
}