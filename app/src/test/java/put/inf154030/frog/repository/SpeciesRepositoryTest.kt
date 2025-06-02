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
import put.inf154030.frog.models.Species
import put.inf154030.frog.models.SpeciesReference
import put.inf154030.frog.models.requests.AddSpeciesRequest
import put.inf154030.frog.models.requests.UpdateSpeciesCountRequest
import put.inf154030.frog.models.responses.ContainerSpeciesItemResponse
import put.inf154030.frog.models.responses.ContainerSpeciesUpdateResponse
import put.inf154030.frog.models.responses.MessageResponse
import put.inf154030.frog.models.responses.SpeciesListResponse
import put.inf154030.frog.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SpeciesRepositoryTest {

    @Mock
    lateinit var mockApiService: ApiService

    @Mock
    lateinit var mockDeleteCall: Call<MessageResponse>
    @Mock
    lateinit var mockUpdateCall: Call<ContainerSpeciesUpdateResponse>
    @Mock
    lateinit var mockAddCall: Call<ContainerSpeciesItemResponse>
    @Mock
    lateinit var mockGetCall: Call<SpeciesListResponse>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `deleteSpeciesFromContainer success calls onResult with true`() {
        val repo = SpeciesRepository(mockApiService)
        val response = Response.success(MessageResponse("deleted"))
        `when`(mockApiService.deleteSpeciesFromContainer(1, 2)).thenReturn(mockDeleteCall)

        var called = false
        repo.deleteSpeciesFromContainer(1, 2) { success, error ->
            if (success) called = true
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<MessageResponse>>
        verify(mockDeleteCall).enqueue(captor.capture())
        captor.value.onResponse(mockDeleteCall, response)
        assert(called)
    }

    @Test
    fun `deleteSpeciesFromContainer failure calls onResult with false and error`() {
        val repo = SpeciesRepository(mockApiService)
        val response = Response.error<MessageResponse>(
            400, "error".toResponseBody("application/json".toMediaTypeOrNull())
        )
        `when`(mockApiService.deleteSpeciesFromContainer(1, 2)).thenReturn(mockDeleteCall)

        var errorMsg: String? = null
        repo.deleteSpeciesFromContainer(1, 2) { success, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<MessageResponse>>
        verify(mockDeleteCall).enqueue(captor.capture())
        captor.value.onResponse(mockDeleteCall, response)
        assert(errorMsg?.contains("Failed to delete species") == true)
    }

    @Test
    fun `deleteSpeciesFromContainer network failure calls onResult with false and error`() {
        val repo = SpeciesRepository(mockApiService)
        `when`(mockApiService.deleteSpeciesFromContainer(1, 2)).thenReturn(mockDeleteCall)

        var errorMsg: String? = null
        repo.deleteSpeciesFromContainer(1, 2) { success, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<MessageResponse>>
        verify(mockDeleteCall).enqueue(captor.capture())
        captor.value.onFailure(mockDeleteCall, Throwable("timeout"))
        assert(errorMsg?.contains("timeout") == true)
    }

    @Test
    fun `updateContainerSpecies success calls onResult with true`() {
        val repo = SpeciesRepository(mockApiService)
        val request = UpdateSpeciesCountRequest(5)
        val response = Response.success(ContainerSpeciesUpdateResponse(
            1,
            1,
            2,
            "02-06-2025 19:00",
        ))
        `when`(mockApiService.updateContainerSpecies(1, 2, request)).thenReturn(mockUpdateCall)

        var called = false
        repo.updateContainerSpecies(1, 2, request) { success, failure, error ->
            if (success) called = true
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ContainerSpeciesUpdateResponse>>
        verify(mockUpdateCall).enqueue(captor.capture())
        captor.value.onResponse(mockUpdateCall, response)
        assert(called)
    }

    @Test
    fun `updateContainerSpecies failure calls onResult with false and error`() {
        val repo = SpeciesRepository(mockApiService)
        val request = UpdateSpeciesCountRequest(5)
        val response = Response.error<ContainerSpeciesUpdateResponse>(
            400, "error".toResponseBody("application/json".toMediaTypeOrNull())
        )
        `when`(mockApiService.updateContainerSpecies(1, 2, request)).thenReturn(mockUpdateCall)

        var errorMsg: String? = null
        repo.updateContainerSpecies(1, 2, request) { success, failure, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ContainerSpeciesUpdateResponse>>
        verify(mockUpdateCall).enqueue(captor.capture())
        captor.value.onResponse(mockUpdateCall, response)
        assert(errorMsg?.contains("Failed to update species") == true)
    }

    @Test
    fun `updateContainerSpecies network failure calls onResult with false and network error`() {
        val repo = SpeciesRepository(mockApiService)
        val request = UpdateSpeciesCountRequest(5)
        `when`(mockApiService.updateContainerSpecies(1, 2, request)).thenReturn(mockUpdateCall)

        var errorMsg: String? = null
        repo.updateContainerSpecies(1, 2, request) { success, failure, error ->
            if (!success && failure) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ContainerSpeciesUpdateResponse>>
        verify(mockUpdateCall).enqueue(captor.capture())
        captor.value.onFailure(mockUpdateCall, Throwable("timeout"))
        assert(errorMsg?.contains("Network error") == true)
    }

    @Test
    fun `addSpeciesToContainer success calls onResult with true`() {
        val repo = SpeciesRepository(mockApiService)
        val request = AddSpeciesRequest(1, 5)
        val response = Response.success(ContainerSpeciesItemResponse(
            1, SpeciesReference(1, "Frog"), 5,
            "02-06-2025 19:00",
            emptyList()
        ))
        `when`(mockApiService.addSpeciesToContainer(1, request)).thenReturn(mockAddCall)

        var called = false
        repo.addSpeciesToContainer(1, request) { success, error ->
            if (success) called = true
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ContainerSpeciesItemResponse>>
        verify(mockAddCall).enqueue(captor.capture())
        captor.value.onResponse(mockAddCall, response)
        assert(called)
    }

    @Test
    fun `addSpeciesToContainer failure calls onResult with false and error`() {
        val repo = SpeciesRepository(mockApiService)
        val request = AddSpeciesRequest(1, 5)
        val response = Response.error<ContainerSpeciesItemResponse>(
            400, "error".toResponseBody("application/json".toMediaTypeOrNull())
        )
        `when`(mockApiService.addSpeciesToContainer(1, request)).thenReturn(mockAddCall)

        var errorMsg: String? = null
        repo.addSpeciesToContainer(1, request) { success, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ContainerSpeciesItemResponse>>
        verify(mockAddCall).enqueue(captor.capture())
        captor.value.onResponse(mockAddCall, response)
        assert(errorMsg?.contains("Failed to add species") == true)
    }

    @Test
    fun `addSpeciesToContainer network failure calls onResult with false and network error`() {
        val repo = SpeciesRepository(mockApiService)
        val request = AddSpeciesRequest(1, 5)
        `when`(mockApiService.addSpeciesToContainer(1, request)).thenReturn(mockAddCall)

        var errorMsg: String? = null
        repo.addSpeciesToContainer(1, request) { success, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<ContainerSpeciesItemResponse>>
        verify(mockAddCall).enqueue(captor.capture())
        captor.value.onFailure(mockAddCall, Throwable("timeout"))
        assert(errorMsg?.contains("Network error") == true)
    }

    @Test
    fun `getSpecies success calls onResult with true and species`() {
        val repo = SpeciesRepository(mockApiService)
        val speciesList = listOf(Species(
            1, "Frog", "Frog",
            "desc",
            "dinosaur",
            true
        ))
        val response = Response.success(SpeciesListResponse(speciesList))
        `when`(mockApiService.getSpecies("amphibian")).thenReturn(mockGetCall)

        var result: List<Species>? = null
        repo.getSpecies("amphibian") { success, species, error ->
            if (success) result = species
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<SpeciesListResponse>>
        verify(mockGetCall).enqueue(captor.capture())
        captor.value.onResponse(mockGetCall, response)
        assert(result == speciesList)
    }

    @Test
    fun `getSpecies failure calls onResult with false and error`() {
        val repo = SpeciesRepository(mockApiService)
        val response = Response.error<SpeciesListResponse>(
            400, "error".toResponseBody("application/json".toMediaTypeOrNull())
        )
        `when`(mockApiService.getSpecies("amphibian")).thenReturn(mockGetCall)

        var errorMsg: String? = null
        repo.getSpecies("amphibian") { success, species, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<SpeciesListResponse>>
        verify(mockGetCall).enqueue(captor.capture())
        captor.value.onResponse(mockGetCall, response)
        assert(errorMsg?.contains("Failed to load species") == true)
    }

    @Test
    fun `getSpecies network failure calls onResult with false and network error`() {
        val repo = SpeciesRepository(mockApiService)
        `when`(mockApiService.getSpecies("amphibian")).thenReturn(mockGetCall)

        var errorMsg: String? = null
        repo.getSpecies("amphibian") { success, species, error ->
            if (!success) errorMsg = error
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<SpeciesListResponse>>
        verify(mockGetCall).enqueue(captor.capture())
        captor.value.onFailure(mockGetCall, Throwable("timeout"))
        assert(errorMsg?.contains("Network error") == true)
    }
}