package put.inf154030.frog.repository

import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import put.inf154030.frog.models.requests.ParameterUpdateRequest
import put.inf154030.frog.models.responses.ParameterHistoryResponse
import put.inf154030.frog.models.responses.ParameterResponse
import put.inf154030.frog.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody

class ParametersRepositoryTest {

    @Mock
    lateinit var mockApiService: ApiService

    @Mock
    lateinit var mockHistoryCall: Call<ParameterHistoryResponse>
    @Mock
    lateinit var mockUpdateCall: Call<ParameterResponse>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        ApiClient::class.java.getDeclaredField("apiService").apply {
            isAccessible = true
            set(ApiClient, mockApiService)
        }
    }

    @Test
    fun `getParameterHistory success calls onResult with true and response`() {
        val repo = ParametersRepository()
        val responseObj = ParameterHistoryResponse(emptyList())
        val response = Response.success(responseObj)
        `when`(mockApiService.getParameterHistory(1, "temp", null, null)).thenReturn(mockHistoryCall)

        var result: ParameterHistoryResponse? = null
        repo.getParameterHistory(1, "temp", null, null) { success, resp, error ->
            if (success) result = resp
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ParameterHistoryResponse>>
        verify(mockHistoryCall).enqueue(captor.capture())
        captor.value.onResponse(mockHistoryCall, response)
        assert(result == responseObj)
    }

    @Test
    fun `getParameterHistory failure calls onResult with false and error`() {
        val repo = ParametersRepository()
        val response = Response.error<ParameterHistoryResponse>(
            400, ResponseBody.create("application/json".toMediaTypeOrNull(), "error")
        )
        `when`(mockApiService.getParameterHistory(1, "temp", null, null)).thenReturn(mockHistoryCall)

        var errorMsg: String? = null
        repo.getParameterHistory(1, "temp", null, null) { success, resp, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ParameterHistoryResponse>>
        verify(mockHistoryCall).enqueue(captor.capture())
        captor.value.onResponse(mockHistoryCall, response)
        assert(errorMsg?.contains("Failed to load parameter history") == true)
    }

    @Test
    fun `getParameterHistory network failure calls onResult with false and network error`() {
        val repo = ParametersRepository()
        `when`(mockApiService.getParameterHistory(1, "temp", null, null)).thenReturn(mockHistoryCall)

        var errorMsg: String? = null
        repo.getParameterHistory(1, "temp", null, null) { success, resp, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ParameterHistoryResponse>>
        verify(mockHistoryCall).enqueue(captor.capture())
        captor.value.onFailure(mockHistoryCall, Throwable("timeout"))
        assert(errorMsg?.contains("Network error") == true)
    }

    @Test
    fun `updateParameter success calls onResult with true`() {
        val repo = ParametersRepository()
        val request = ParameterUpdateRequest(1.0)
        val response = Response.success(ParameterResponse("ok"))
        `when`(mockApiService.updateParameter(1, request, "temp")).thenReturn(mockUpdateCall)

        var called = false
        repo.updateParameter(1, "temp", request) { success, failure, error ->
            if (success) called = true
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ParameterResponse>>
        verify(mockUpdateCall).enqueue(captor.capture())
        captor.value.onResponse(mockUpdateCall, response)
        assert(called)
    }

    @Test
    fun `updateParameter failure calls onResult with false and error`() {
        val repo = ParametersRepository()
        val request = ParameterUpdateRequest(1.0)
        val response = Response.error<ParameterResponse>(
            400, ResponseBody.create("application/json".toMediaTypeOrNull(), "error")
        )
        `when`(mockApiService.updateParameter(1, request, "temp")).thenReturn(mockUpdateCall)

        var errorMsg: String? = null
        repo.updateParameter(1, "temp", request) { success, failure, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ParameterResponse>>
        verify(mockUpdateCall).enqueue(captor.capture())
        captor.value.onResponse(mockUpdateCall, response)
        assert(errorMsg?.contains("Failed to update parameter") == true)
    }

    @Test
    fun `updateParameter network failure calls onResult with false and network error`() {
        val repo = ParametersRepository()
        val request = ParameterUpdateRequest(1.0)
        `when`(mockApiService.updateParameter(1, request, "temp")).thenReturn(mockUpdateCall)

        var errorMsg: String? = null
        repo.updateParameter(1, "temp", request) { success, failure, error ->
            if (!success && failure) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ParameterResponse>>
        verify(mockUpdateCall).enqueue(captor.capture())
        captor.value.onFailure(mockUpdateCall, Throwable("timeout"))
        assert(errorMsg?.contains("Network error") == true)
    }
}