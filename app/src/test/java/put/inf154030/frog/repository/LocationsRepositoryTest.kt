package put.inf154030.frog.repository

import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import put.inf154030.frog.models.Location
import put.inf154030.frog.models.requests.LocationCreateRequest
import put.inf154030.frog.models.requests.LocationUpdateRequest
import put.inf154030.frog.models.responses.*
import put.inf154030.frog.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody

class LocationsRepositoryTest {

    @Mock
    lateinit var mockApiService: ApiService

    @Mock
    lateinit var mockGetCall: Call<LocationsResponse>
    @Mock
    lateinit var mockDetailCall: Call<LocationDetailResponse>
    @Mock
    lateinit var mockCreateCall: Call<LocationResponse>
    @Mock
    lateinit var mockDeleteCall: Call<MessageResponse>
    @Mock
    lateinit var mockUpdateCall: Call<LocationUpdateResponse>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        ApiClient::class.java.getDeclaredField("apiService").apply {
            isAccessible = true
            set(ApiClient, mockApiService)
        }
    }

    @Test
    fun `getLocations success calls onResult with true and locations`() {
        val repo = LocationsRepository()
        val locations = listOf(Location(1, "Test", "desc", null, null, null, null, null, null))
        val response = Response.success(LocationsResponse(locations))
        `when`(mockApiService.getLocations()).thenReturn(mockGetCall)

        var resultList: List<Location>? = null
        repo.getLocations { success, locs, error ->
            if (success) resultList = locs
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<LocationsResponse>>
        verify(mockGetCall).enqueue(captor.capture())
        captor.value.onResponse(mockGetCall, response)
        assert(resultList == locations)
    }

    @Test
    fun `getLocations failure calls onResult with false and error`() {
        val repo = LocationsRepository()
        val response = Response.error<LocationsResponse>(
            400, ResponseBody.create("application/json".toMediaTypeOrNull(), "error")
        )
        `when`(mockApiService.getLocations()).thenReturn(mockGetCall)

        var errorMsg: String? = null
        repo.getLocations { success, locs, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<LocationsResponse>>
        verify(mockGetCall).enqueue(captor.capture())
        captor.value.onResponse(mockGetCall, response)
        assert(errorMsg?.contains("Failed to load locations") == true)
    }

    @Test
    fun `getLocations network failure calls onResult with false and network error`() {
        val repo = LocationsRepository()
        `when`(mockApiService.getLocations()).thenReturn(mockGetCall)

        var errorMsg: String? = null
        repo.getLocations { success, locs, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<LocationsResponse>>
        verify(mockGetCall).enqueue(captor.capture())
        captor.value.onFailure(mockGetCall, Throwable("timeout"))
        assert(errorMsg?.contains("Network error") == true)
    }

    @Test
    fun `getLocation success calls onResult with true and detail`() {
        val repo = LocationsRepository()
        val detail = LocationDetailResponse(1, "Test", "desc", null, null, null, null, null, null, null)
        val response = Response.success(detail)
        `when`(mockApiService.getLocation(1)).thenReturn(mockDetailCall)

        var result: LocationDetailResponse? = null
        repo.getLocation(1) { success, resp, error ->
            if (success) result = resp
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<LocationDetailResponse>>
        verify(mockDetailCall).enqueue(captor.capture())
        captor.value.onResponse(mockDetailCall, response)
        assert(result == detail)
    }

    @Test
    fun `getLocation failure calls onResult with false and error`() {
        val repo = LocationsRepository()
        val response = Response.error<LocationDetailResponse>(
            400, ResponseBody.create("application/json".toMediaTypeOrNull(), "error")
        )
        `when`(mockApiService.getLocation(1)).thenReturn(mockDetailCall)

        var errorMsg: String? = null
        repo.getLocation(1) { success, resp, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<LocationDetailResponse>>
        verify(mockDetailCall).enqueue(captor.capture())
        captor.value.onResponse(mockDetailCall, response)
        assert(errorMsg?.contains("Failed to load location") == true)
    }

    @Test
    fun `getLocation network failure calls onResult with false and network error`() {
        val repo = LocationsRepository()
        `when`(mockApiService.getLocation(1)).thenReturn(mockDetailCall)

        var errorMsg: String? = null
        repo.getLocation(1) { success, resp, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<LocationDetailResponse>>
        verify(mockDetailCall).enqueue(captor.capture())
        captor.value.onFailure(mockDetailCall, Throwable("timeout"))
        assert(errorMsg?.contains("Network error") == true)
    }

    @Test
    fun `createLocation success calls onResult with true`() {
        val repo = LocationsRepository()
        val request = LocationCreateRequest("Test", "desc")
        val response = Response.success(LocationResponse(1, "Test", "desc"))
        `when`(mockApiService.createLocation(request)).thenReturn(mockCreateCall)

        var called = false
        repo.createLocation(request) { success, error ->
            if (success) called = true
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<LocationResponse>>
        verify(mockCreateCall).enqueue(captor.capture())
        captor.value.onResponse(mockCreateCall, response)
        assert(called)
    }

    @Test
    fun `createLocation failure calls onResult with false and error`() {
        val repo = LocationsRepository()
        val request = LocationCreateRequest("Test", "desc")
        val response = Response.error<LocationResponse>(
            400, ResponseBody.create("application/json".toMediaTypeOrNull(), "error")
        )
        `when`(mockApiService.createLocation(request)).thenReturn(mockCreateCall)

        var errorMsg: String? = null
        repo.createLocation(request) { success, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<LocationResponse>>
        verify(mockCreateCall).enqueue(captor.capture())
        captor.value.onResponse(mockCreateCall, response)
        assert(errorMsg?.contains("Failed to create location") == true)
    }

    @Test
    fun `createLocation network failure calls onResult with false and network error`() {
        val repo = LocationsRepository()
        val request = LocationCreateRequest("Test", "desc")
        `when`(mockApiService.createLocation(request)).thenReturn(mockCreateCall)

        var errorMsg: String? = null
        repo.createLocation(request) { success, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<LocationResponse>>
        verify(mockCreateCall).enqueue(captor.capture())
        captor.value.onFailure(mockCreateCall, Throwable("timeout"))
        assert(errorMsg?.contains("Network error") == true)
    }

    @Test
    fun `deleteLocation success calls onResult with true`() {
        val repo = LocationsRepository()
        val response = Response.success(MessageResponse("deleted"))
        `when`(mockApiService.deleteLocation(1)).thenReturn(mockDeleteCall)

        var called = false
        repo.deleteLocation(1) { success, error ->
            if (success) called = true
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<MessageResponse>>
        verify(mockDeleteCall).enqueue(captor.capture())
        captor.value.onResponse(mockDeleteCall, response)
        assert(called)
    }

    @Test
    fun `deleteLocation failure calls onResult with false and error`() {
        val repo = LocationsRepository()
        val response = Response.error<MessageResponse>(
            400, ResponseBody.create("application/json".toMediaTypeOrNull(), "error")
        )
        `when`(mockApiService.deleteLocation(1)).thenReturn(mockDeleteCall)

        var errorMsg: String? = null
        repo.deleteLocation(1) { success, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<MessageResponse>>
        verify(mockDeleteCall).enqueue(captor.capture())
        captor.value.onResponse(mockDeleteCall, response)
        assert(errorMsg?.contains("Failed to delete location") == true)
    }

    @Test
    fun `deleteLocation network failure calls onResult with false and network error`() {
        val repo = LocationsRepository()
        `when`(mockApiService.deleteLocation(1)).thenReturn(mockDeleteCall)

        var errorMsg: String? = null
        repo.deleteLocation(1) { success, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<MessageResponse>>
        verify(mockDeleteCall).enqueue(captor.capture())
        captor.value.onFailure(mockDeleteCall, Throwable("timeout"))
        assert(errorMsg?.contains("Network error") == true)
    }

    @Test
    fun `updateLocation success calls onResult with true`() {
        val repo = LocationsRepository()
        val request = LocationUpdateRequest("Test", "desc")
        val response = Response.success(LocationUpdateResponse("updated"))
        `when`(mockApiService.updateLocation(1, request)).thenReturn(mockUpdateCall)

        var called = false
        repo.updateLocation(1, request) { success, error ->
            if (success) called = true
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<LocationUpdateResponse>>
        verify(mockUpdateCall).enqueue(captor.capture())
        captor.value.onResponse(mockUpdateCall, response)
        assert(called)
    }

    @Test
    fun `updateLocation failure calls onResult with false and error`() {
        val repo = LocationsRepository()
        val request = LocationUpdateRequest("Test", "desc")
        val response = Response.error<LocationUpdateResponse>(
            400, ResponseBody.create("application/json".toMediaTypeOrNull(), "error")
        )
        `when`(mockApiService.updateLocation(1, request)).thenReturn(mockUpdateCall)

        var errorMsg: String? = null
        repo.updateLocation(1, request) { success, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<LocationUpdateResponse>>
        verify(mockUpdateCall).enqueue(captor.capture())
        captor.value.onResponse(mockUpdateCall, response)
        assert(errorMsg?.contains("Failed to update location") == true)
    }

    @Test
    fun `updateLocation network failure calls onResult with false and network error`() {
        val repo = LocationsRepository()
        val request = LocationUpdateRequest("Test", "desc")
        `when`(mockApiService.updateLocation(1, request)).thenReturn(mockUpdateCall)

        var errorMsg: String? = null
        repo.updateLocation(1, request) { success, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<LocationUpdateResponse>>
        verify(mockUpdateCall).enqueue(captor.capture())
        captor.value.onFailure(mockUpdateCall, Throwable("timeout"))
        assert(errorMsg?.contains("Network error") == true)
    }
}