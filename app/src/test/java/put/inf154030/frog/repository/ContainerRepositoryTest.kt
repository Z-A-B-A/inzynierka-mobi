package put.inf154030.frog.repository

import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import put.inf154030.frog.models.Container
import put.inf154030.frog.models.requests.ContainerCreateRequest
import put.inf154030.frog.models.requests.ContainerUpdateRequest
import put.inf154030.frog.models.responses.*
import put.inf154030.frog.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody

class ContainersRepositoryTest {

    @Mock
    lateinit var mockApiService: ApiService

    @Mock
    lateinit var mockCreateCall: Call<ContainerResponse>
    @Mock
    lateinit var mockGetCall: Call<ContainersResponse>
    @Mock
    lateinit var mockDetailCall: Call<ContainerDetailResponse>
    @Mock
    lateinit var mockDeleteCall: Call<MessageResponse>
    @Mock
    lateinit var mockUpdateCall: Call<ContainerUpdateResponse>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        ApiClient::class.java.getDeclaredField("apiService").apply {
            isAccessible = true
            set(ApiClient, mockApiService)
        }
    }

    @Test
    fun `createContainer success calls onResult with true`() {
        val repo = ContainersRepository()
        val request = ContainerCreateRequest("Test", "desc")
        val response = Response.success(ContainerResponse(1, "Test", "desc"))
        `when`(mockApiService.createContainer(1, request)).thenReturn(mockCreateCall)

        var called = false
        repo.createContainer(request, 1) { success, error ->
            if (success) called = true
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ContainerResponse>>
        verify(mockCreateCall).enqueue(captor.capture())
        captor.value.onResponse(mockCreateCall, response)
        assert(called)
    }

    @Test
    fun `createContainer failure calls onResult with false and error`() {
        val repo = ContainersRepository()
        val request = ContainerCreateRequest("Test", "desc")
        val response = Response.error<ContainerResponse>(
            400, ResponseBody.create("application/json".toMediaTypeOrNull(), "error")
        )
        `when`(mockApiService.createContainer(1, request)).thenReturn(mockCreateCall)

        var errorMsg: String? = null
        repo.createContainer(request, 1) { success, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ContainerResponse>>
        verify(mockCreateCall).enqueue(captor.capture())
        captor.value.onResponse(mockCreateCall, response)
        assert(errorMsg?.contains("Failed to create container") == true)
    }

    @Test
    fun `createContainer network failure calls onResult with false and network error`() {
        val repo = ContainersRepository()
        val request = ContainerCreateRequest("Test", "desc")
        `when`(mockApiService.createContainer(1, request)).thenReturn(mockCreateCall)

        var errorMsg: String? = null
        repo.createContainer(request, 1) { success, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ContainerResponse>>
        verify(mockCreateCall).enqueue(captor.capture())
        captor.value.onFailure(mockCreateCall, Throwable("timeout"))
        assert(errorMsg?.contains("Network error") == true)
    }

    @Test
    fun `getContainers success calls onResult with true and containers`() {
        val repo = ContainersRepository()
        val containers = listOf(Container(1, "Test", "desc", null, null, null, null, null, null))
        val response = Response.success(ContainersResponse(containers))
        `when`(mockApiService.getContainers(1)).thenReturn(mockGetCall)

        var resultList: List<Container>? = null
        repo.getContainers(1) { success, containers, error ->
            if (success) resultList = containers
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ContainersResponse>>
        verify(mockGetCall).enqueue(captor.capture())
        captor.value.onResponse(mockGetCall, response)
        assert(resultList == containers)
    }

    @Test
    fun `getContainers failure calls onResult with false and error`() {
        val repo = ContainersRepository()
        val response = Response.error<ContainersResponse>(
            400, ResponseBody.create("application/json".toMediaTypeOrNull(), "error")
        )
        `when`(mockApiService.getContainers(1)).thenReturn(mockGetCall)

        var errorMsg: String? = null
        repo.getContainers(1) { success, containers, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ContainersResponse>>
        verify(mockGetCall).enqueue(captor.capture())
        captor.value.onResponse(mockGetCall, response)
        assert(errorMsg?.contains("Failed to load containers") == true)
    }

    @Test
    fun `getContainers network failure calls onResult with false and network error`() {
        val repo = ContainersRepository()
        `when`(mockApiService.getContainers(1)).thenReturn(mockGetCall)

        var errorMsg: String? = null
        repo.getContainers(1) { success, containers, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ContainersResponse>>
        verify(mockGetCall).enqueue(captor.capture())
        captor.value.onFailure(mockGetCall, Throwable("timeout"))
        assert(errorMsg?.contains("Network error") == true)
    }

    @Test
    fun `getContainerDetails success calls onResult with true and response`() {
        val repo = ContainersRepository()
        val detail = ContainerDetailResponse(1, "Test", "desc", null, null, null, null, null, null, null)
        val response = Response.success(detail)
        `when`(mockApiService.getContainerDetails(1)).thenReturn(mockDetailCall)

        var result: ContainerDetailResponse? = null
        repo.getContainerDetails(1) { success, resp, error ->
            if (success) result = resp
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ContainerDetailResponse>>
        verify(mockDetailCall).enqueue(captor.capture())
        captor.value.onResponse(mockDetailCall, response)
        assert(result == detail)
    }

    @Test
    fun `getContainerDetails failure calls onResult with false and error`() {
        val repo = ContainersRepository()
        val response = Response.error<ContainerDetailResponse>(
            400, ResponseBody.create("application/json".toMediaTypeOrNull(), "error")
        )
        `when`(mockApiService.getContainerDetails(1)).thenReturn(mockDetailCall)

        var errorMsg: String? = null
        repo.getContainerDetails(1) { success, resp, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ContainerDetailResponse>>
        verify(mockDetailCall).enqueue(captor.capture())
        captor.value.onResponse(mockDetailCall, response)
        assert(errorMsg?.contains("Failed to load container details") == true)
    }

    @Test
    fun `getContainerDetails network failure calls onResult with false and network error`() {
        val repo = ContainersRepository()
        `when`(mockApiService.getContainerDetails(1)).thenReturn(mockDetailCall)

        var errorMsg: String? = null
        repo.getContainerDetails(1) { success, resp, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ContainerDetailResponse>>
        verify(mockDetailCall).enqueue(captor.capture())
        captor.value.onFailure(mockDetailCall, Throwable("timeout"))
        assert(errorMsg?.contains("Network error") == true)
    }

    @Test
    fun `deleteContainer success calls onResult with true`() {
        val repo = ContainersRepository()
        val response = Response.success(MessageResponse("deleted"))
        `when`(mockApiService.deleteContainer(1)).thenReturn(mockDeleteCall)

        var called = false
        repo.deleteContainer(1) { success, error ->
            if (success) called = true
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<MessageResponse>>
        verify(mockDeleteCall).enqueue(captor.capture())
        captor.value.onResponse(mockDeleteCall, response)
        assert(called)
    }

    @Test
    fun `deleteContainer failure calls onResult with false and error`() {
        val repo = ContainersRepository()
        val response = Response.error<MessageResponse>(
            400, ResponseBody.create("application/json".toMediaTypeOrNull(), "error")
        )
        `when`(mockApiService.deleteContainer(1)).thenReturn(mockDeleteCall)

        var errorMsg: String? = null
        repo.deleteContainer(1) { success, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<MessageResponse>>
        verify(mockDeleteCall).enqueue(captor.capture())
        captor.value.onResponse(mockDeleteCall, response)
        assert(errorMsg != null)
    }

    @Test
    fun `deleteContainer network failure calls onResult with false and error`() {
        val repo = ContainersRepository()
        `when`(mockApiService.deleteContainer(1)).thenReturn(mockDeleteCall)

        var errorMsg: String? = null
        repo.deleteContainer(1) { success, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<MessageResponse>>
        verify(mockDeleteCall).enqueue(captor.capture())
        captor.value.onFailure(mockDeleteCall, Throwable("timeout"))
        assert(errorMsg?.contains("timeout") == true)
    }

    @Test
    fun `updateContainer success calls onResult with true`() {
        val repo = ContainersRepository()
        val request = ContainerUpdateRequest("Test", "desc")
        val response = Response.success(ContainerUpdateResponse("updated"))
        `when`(mockApiService.updateContainer(1, request)).thenReturn(mockUpdateCall)

        var called = false
        repo.updateContainer(1, request) { success, error ->
            if (success) called = true
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ContainerUpdateResponse>>
        verify(mockUpdateCall).enqueue(captor.capture())
        captor.value.onResponse(mockUpdateCall, response)
        assert(called)
    }

    @Test
    fun `updateContainer failure calls onResult with false and error`() {
        val repo = ContainersRepository()
        val request = ContainerUpdateRequest("Test", "desc")
        val response = Response.error<ContainerUpdateResponse>(
            400, ResponseBody.create("application/json".toMediaTypeOrNull(), "error")
        )
        `when`(mockApiService.updateContainer(1, request)).thenReturn(mockUpdateCall)

        var errorMsg: String? = null
        repo.updateContainer(1, request) { success, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ContainerUpdateResponse>>
        verify(mockUpdateCall).enqueue(captor.capture())
        captor.value.onResponse(mockUpdateCall, response)
        assert(errorMsg?.contains("Failed to update container") == true)
    }

    @Test
    fun `updateContainer network failure calls onResult with false and network error`() {
        val repo = ContainersRepository()
        val request = ContainerUpdateRequest("Test", "desc")
        `when`(mockApiService.updateContainer(1, request)).thenReturn(mockUpdateCall)

        var errorMsg: String? = null
        repo.updateContainer(1, request) { success, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ContainerUpdateResponse>>
        verify(mockUpdateCall).enqueue(captor.capture())
        captor.value.onFailure(mockUpdateCall, Throwable("timeout"))
        assert(errorMsg?.contains("Network error") == true)
    }
}