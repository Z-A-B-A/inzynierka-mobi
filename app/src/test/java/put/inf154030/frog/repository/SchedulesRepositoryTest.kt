package put.inf154030.frog.repository

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import put.inf154030.frog.models.Schedule
import put.inf154030.frog.models.requests.ScheduleCreateRequest
import put.inf154030.frog.models.requests.ScheduleUpdateRequest
import put.inf154030.frog.models.responses.MessageResponse
import put.inf154030.frog.models.responses.ScheduleResponse
import put.inf154030.frog.models.responses.ScheduleUpdateResponse
import put.inf154030.frog.models.responses.SchedulesResponse
import put.inf154030.frog.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SchedulesRepositoryTest {

    @Mock
    lateinit var mockApiService: ApiService

    @Mock
    lateinit var mockCreateCall: Call<ScheduleResponse>
    @Mock
    lateinit var mockDeleteCall: Call<MessageResponse>
    @Mock
    lateinit var mockUpdateCall: Call<ScheduleUpdateResponse>
    @Mock
    lateinit var mockGetCall: Call<SchedulesResponse>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `createSchedule success calls onResult with true`() {
        val repo = SchedulesRepository(mockApiService)
        val request = ScheduleCreateRequest(
            "name", "2024-01-01", "daily", "0", "12:00"
        )
        val response = Response.success(ScheduleResponse(1, "name", "2024-01-01", "daily", "0","12:00", "2024-01-01 19:00"))
        `when`(mockApiService.createSchedule(1, request)).thenReturn(mockCreateCall)

        var called = false
        repo.createSchedule(1, request) { success, error ->
            if (success) called = true
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ScheduleResponse>>
        verify(mockCreateCall).enqueue(captor.capture())
        captor.value.onResponse(mockCreateCall, response)
        assert(called)
    }

    @Test
    fun `createSchedule failure calls onResult with false and error`() {
        val repo = SchedulesRepository(mockApiService)
        val request = ScheduleCreateRequest(
            "name", "2024-01-01", "daily", "0", "12:00"
        )
        val response = Response.error<ScheduleResponse>(
            400, "error".toResponseBody("application/json".toMediaTypeOrNull())
        )
        `when`(mockApiService.createSchedule(1, request)).thenReturn(mockCreateCall)

        var errorMsg: String? = null
        repo.createSchedule(1, request) { success, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ScheduleResponse>>
        verify(mockCreateCall).enqueue(captor.capture())
        captor.value.onResponse(mockCreateCall, response)
        assert(errorMsg != null)
    }

    @Test
    fun `createSchedule network failure calls onResult with false and network error`() {
        val repo = SchedulesRepository(mockApiService)
        val request = ScheduleCreateRequest(
            "name", "2024-01-01", "daily", "0", "12:00"
        )
        `when`(mockApiService.createSchedule(1, request)).thenReturn(mockCreateCall)

        var errorMsg: String? = null
        repo.createSchedule(1, request) { success, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ScheduleResponse>>
        verify(mockCreateCall).enqueue(captor.capture())
        captor.value.onFailure(mockCreateCall, Throwable("timeout"))
        assert(errorMsg?.contains("timeout") == true || errorMsg?.contains("Network error") == true)
    }

    @Test
    fun `deleteSchedule success calls onResult with true`() {
        val repo = SchedulesRepository(mockApiService)
        val response = Response.success(MessageResponse("deleted"))
        `when`(mockApiService.deleteSchedule(1)).thenReturn(mockDeleteCall)

        var called = false
        repo.deleteSchedule(1) { success, error ->
            if (success) called = true
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<MessageResponse>>
        verify(mockDeleteCall).enqueue(captor.capture())
        captor.value.onResponse(mockDeleteCall, response)
        assert(called)
    }

    @Test
    fun `deleteSchedule failure calls onResult with false and error`() {
        val repo = SchedulesRepository(mockApiService)
        val response = Response.error<MessageResponse>(
            400, "error".toResponseBody("application/json".toMediaTypeOrNull())
        )
        `when`(mockApiService.deleteSchedule(1)).thenReturn(mockDeleteCall)

        var errorMsg: String? = null
        repo.deleteSchedule(1) { success, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<MessageResponse>>
        verify(mockDeleteCall).enqueue(captor.capture())
        captor.value.onResponse(mockDeleteCall, response)
        assert(errorMsg != null)
    }

    @Test
    fun `deleteSchedule network failure calls onResult with false and error`() {
        val repo = SchedulesRepository(mockApiService)
        `when`(mockApiService.deleteSchedule(1)).thenReturn(mockDeleteCall)

        var errorMsg: String? = null
        repo.deleteSchedule(1) { success, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<MessageResponse>>
        verify(mockDeleteCall).enqueue(captor.capture())
        captor.value.onFailure(mockDeleteCall, Throwable("timeout"))
        assert(errorMsg != null)
    }

    @Test
    fun `updateSchedule success calls onResult with true`() {
        val repo = SchedulesRepository(mockApiService)
        val request = ScheduleUpdateRequest("12:00")
        val response = Response.success(ScheduleUpdateResponse(
            1,
            "Karmienie",
            "12:00",
            "2024-01-01 19:00"
        ))
        `when`(mockApiService.updateSchedule(1, request)).thenReturn(mockUpdateCall)

        var called = false
        repo.updateSchedule(1, request) { success, error ->
            if (success) called = true
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ScheduleUpdateResponse>>
        verify(mockUpdateCall).enqueue(captor.capture())
        captor.value.onResponse(mockUpdateCall, response)
        assert(called)
    }

    @Test
    fun `updateSchedule failure calls onResult with false and error`() {
        val repo = SchedulesRepository(mockApiService)
        val request = ScheduleUpdateRequest("12:00")
        val response = Response.error<ScheduleUpdateResponse>(
            400, "error".toResponseBody("application/json".toMediaTypeOrNull())
        )
        `when`(mockApiService.updateSchedule(1, request)).thenReturn(mockUpdateCall)

        var errorMsg: String? = null
        repo.updateSchedule(1, request) { success, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ScheduleUpdateResponse>>
        verify(mockUpdateCall).enqueue(captor.capture())
        captor.value.onResponse(mockUpdateCall, response)
        assert(errorMsg != null)
    }

    @Test
    fun `updateSchedule network failure calls onResult with false and network error`() {
        val repo = SchedulesRepository(mockApiService)
        val request = ScheduleUpdateRequest("12:00")
        `when`(mockApiService.updateSchedule(1, request)).thenReturn(mockUpdateCall)

        var errorMsg: String? = null
        repo.updateSchedule(1, request) { success, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ScheduleUpdateResponse>>
        verify(mockUpdateCall).enqueue(captor.capture())
        captor.value.onFailure(mockUpdateCall, Throwable("timeout"))
        assert(errorMsg != null)
    }

    @Test
    fun `getSchedules success calls onResult with true and schedules`() {
        val repo = SchedulesRepository(mockApiService)
        val schedules = listOf(Schedule(
            1, "name", "2024-01-01", "daily", "0", "12:00", "2024-01-01 19:00",
            "2024-01-01 19:00"
        ))
        val response = Response.success(SchedulesResponse(schedules))
        `when`(mockApiService.getSchedules(1)).thenReturn(mockGetCall)

        var result: List<Schedule>? = null
        repo.getSchedules(1) { success, scheds, error ->
            if (success) result = scheds
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<SchedulesResponse>>
        verify(mockGetCall).enqueue(captor.capture())
        captor.value.onResponse(mockGetCall, response)
        assert(result == schedules)
    }

    @Test
    fun `getSchedules failure calls onResult with false and error`() {
        val repo = SchedulesRepository(mockApiService)
        val response = Response.error<SchedulesResponse>(
            400, "error".toResponseBody("application/json".toMediaTypeOrNull())
        )
        `when`(mockApiService.getSchedules(1)).thenReturn(mockGetCall)

        var errorMsg: String? = null
        repo.getSchedules(1) { success, scheds, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<SchedulesResponse>>
        verify(mockGetCall).enqueue(captor.capture())
        captor.value.onResponse(mockGetCall, response)
        assert(errorMsg?.contains("Failed to load schedules") == true)
    }

    @Test
    fun `getSchedules network failure calls onResult with false and network error`() {
        val repo = SchedulesRepository(mockApiService)
        `when`(mockApiService.getSchedules(1)).thenReturn(mockGetCall)

        var errorMsg: String? = null
        repo.getSchedules(1) { success, scheds, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<SchedulesResponse>>
        verify(mockGetCall).enqueue(captor.capture())
        captor.value.onFailure(mockGetCall, Throwable("timeout"))
        assert(errorMsg?.contains("Network error") == true)
    }
}