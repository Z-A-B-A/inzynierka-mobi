package put.inf154030.frog.repository

import put.inf154030.frog.models.Schedule
import put.inf154030.frog.models.requests.ScheduleCreateRequest
import put.inf154030.frog.models.requests.ScheduleUpdateRequest
import put.inf154030.frog.models.responses.MessageResponse
import put.inf154030.frog.models.responses.ScheduleResponse
import put.inf154030.frog.models.responses.ScheduleUpdateResponse
import put.inf154030.frog.models.responses.SchedulesResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SchedulesRepository (
    private val apiService: ApiService = ApiClient.apiService
) {
    fun createSchedule(
        containerId: Int,
        request: ScheduleCreateRequest,
        onResult: (success: Boolean, errorMessage: String?) -> Unit
    ) {
        apiService.createSchedule(containerId, request)
            .enqueue(object : Callback<ScheduleResponse> {
                override fun onResponse(
                    call: Call<ScheduleResponse>,
                    response: Response<ScheduleResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true, null)
                    } else {
                        val error = response.errorBody()?.string() ?: "Unknown error"
                        onResult(false, error)
                    }
                }

                override fun onFailure(call: Call<ScheduleResponse>, t: Throwable) {
                    onResult(false, t.message ?: "Network error")
                }
            })
    }

    fun deleteSchedule(
        scheduleId: Int,
        onResult: (success: Boolean, errorMessage: String?) -> Unit
    ) {
        apiService.deleteSchedule(scheduleId)
            .enqueue(object : Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true, null)
                    } else {
                        val error = response.errorBody()?.string() ?: "Unknown error"
                        onResult(false, error)
                    }
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    onResult(false, t.message ?: "Failed to delete schedule")
                }
            })
    }

    fun updateSchedule(
        scheduleId: Int,
        request: ScheduleUpdateRequest,
        onResult: (success: Boolean, errorMessage: String?) -> Unit
    ) {
        apiService.updateSchedule(scheduleId, request)
            .enqueue(object : Callback<ScheduleUpdateResponse> {
                override fun onResponse(
                    call: Call<ScheduleUpdateResponse>,
                    response: Response<ScheduleUpdateResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true, null)
                    } else {
                        val error = response.errorBody()?.string() ?: "Unknown error occurred"
                        onResult(false, error)
                    }
                }

                override fun onFailure(call: Call<ScheduleUpdateResponse>, t: Throwable) {
                    onResult(false, "Network error occurred")
                }
            })
    }

    fun getSchedules(
        containerId: Int,
        onResult: (
            success: Boolean,
            schedules: List<Schedule>?,
            errorMessage: String?) -> Unit
    ) {
        apiService.getSchedules(containerId)
            .enqueue(object : Callback<SchedulesResponse> {
                override fun onResponse(
                    call: Call<SchedulesResponse>,
                    response: Response<SchedulesResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(true, response.body()?.schedules ?: emptyList(), null)
                    } else {
                        onResult(false, null, "Failed to load schedules: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<SchedulesResponse>, t: Throwable) {
                    onResult(false, null, "Network error: ${t.message}")
                }
            })
    }
}