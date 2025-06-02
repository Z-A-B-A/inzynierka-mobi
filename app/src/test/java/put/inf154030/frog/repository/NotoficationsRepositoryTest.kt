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
import put.inf154030.frog.models.ContainerReference
import put.inf154030.frog.models.Notification
import put.inf154030.frog.models.ScheduleReference
import put.inf154030.frog.models.requests.DeviceTokenRequest
import put.inf154030.frog.models.responses.MessageResponse
import put.inf154030.frog.models.responses.NotificationMarkAllReadResponse
import put.inf154030.frog.models.responses.NotificationUpdateResponse
import put.inf154030.frog.models.responses.NotificationsResponse
import put.inf154030.frog.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsRepositoryTest {

    @Mock
    lateinit var mockApiService: ApiService

    @Mock
    lateinit var mockTokenCall: Call<MessageResponse>
    @Mock
    lateinit var mockGetCall: Call<NotificationsResponse>
    @Mock
    lateinit var mockMarkCall: Call<NotificationUpdateResponse>
    @Mock
    lateinit var mockMarkAllCall: Call<NotificationMarkAllReadResponse>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `updateDeviceToken success calls onResult with null`() {
        val repo = NotificationsRepository(mockApiService)
        val request = DeviceTokenRequest("token")
        val response = Response.success(MessageResponse("ok"))
        `when`(mockApiService.updateDeviceToken(request)).thenReturn(mockTokenCall)

        var error: String? = "not null"
        repo.updateDeviceToken(request) { err -> error = err }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<MessageResponse>>
        verify(mockTokenCall).enqueue(captor.capture())
        captor.value.onResponse(mockTokenCall, response)
        assert(error == null)
    }

    @Test
    fun `updateDeviceToken failure calls onResult with error message`() {
        val repo = NotificationsRepository(mockApiService)
        val request = DeviceTokenRequest("token")
        val response = Response.error<MessageResponse>(
            400, "error".toResponseBody("application/json".toMediaTypeOrNull())
        )
        `when`(mockApiService.updateDeviceToken(request)).thenReturn(mockTokenCall)

        var error: String? = null
        repo.updateDeviceToken(request) { err -> error = err }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<MessageResponse>>
        verify(mockTokenCall).enqueue(captor.capture())
        captor.value.onResponse(mockTokenCall, response)
        assert(error?.contains("Failed to send token") == true || error != null)
    }

    @Test
    fun `updateDeviceToken network failure calls onResult with network error`() {
        val repo = NotificationsRepository(mockApiService)
        val request = DeviceTokenRequest("token")
        `when`(mockApiService.updateDeviceToken(request)).thenReturn(mockTokenCall)

        var error: String? = null
        repo.updateDeviceToken(request) { err -> error = err }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<MessageResponse>>
        verify(mockTokenCall).enqueue(captor.capture())
        captor.value.onFailure(mockTokenCall, Throwable("timeout"))
        assert(error?.contains("Network error") == true)
    }

    @Test
    fun `getNotifications success calls onResult with notifications`() {
        val repo = NotificationsRepository(mockApiService)
        val notifications = listOf(Notification(
            1,
            "msg",
            false,
            "02-06-2025 19:00",
            "02-06-2025 19:00",
            1,
            ScheduleReference(1, "Karmienie"),
            ContainerReference(1, "Terrarium")
        ))
        val response = Response.success(NotificationsResponse(
            notifications,
            1
        ))
        `when`(mockApiService.getNotifications(true)).thenReturn(mockGetCall)

        var result: List<Notification>? = null
        repo.getNotifications(true) { notifs, error ->
            result = notifs
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<NotificationsResponse>>
        verify(mockGetCall).enqueue(captor.capture())
        captor.value.onResponse(mockGetCall, response)
        assert(result == notifications)
    }

    @Test
    fun `getNotifications failure calls onResult with error`() {
        val repo = NotificationsRepository(mockApiService)
        val response = Response.error<NotificationsResponse>(
            400, "error".toResponseBody("application/json".toMediaTypeOrNull())
        )
        `when`(mockApiService.getNotifications(true)).thenReturn(mockGetCall)

        var error: String? = null
        repo.getNotifications(true) { notifs, err -> error = err }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<NotificationsResponse>>
        verify(mockGetCall).enqueue(captor.capture())
        captor.value.onResponse(mockGetCall, response)
        assert(error?.contains("Failed to load notifications") == true)
    }

    @Test
    fun `getNotifications network failure calls onResult with network error`() {
        val repo = NotificationsRepository(mockApiService)
        `when`(mockApiService.getNotifications(true)).thenReturn(mockGetCall)

        var error: String? = null
        repo.getNotifications(true) { notifs, err -> error = err }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<NotificationsResponse>>
        verify(mockGetCall).enqueue(captor.capture())
        captor.value.onFailure(mockGetCall, Throwable("timeout"))
        assert(error == "timeout" || (error != null && error!!.contains("timeout")))
    }

    @Test
    fun `markNotificationAsRead success calls onResult with true`() {
        val repo = NotificationsRepository(mockApiService)
        val response = Response.success(NotificationUpdateResponse(
            1,
            true,
            "02-06-2025 19:00"
        ))
        `when`(mockApiService.markNotificationAsRead(1)).thenReturn(mockMarkCall)

        var called = false
        repo.markNotificationAsRead(1) { success, error ->
            if (success) called = true
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<NotificationUpdateResponse>>
        verify(mockMarkCall).enqueue(captor.capture())
        captor.value.onResponse(mockMarkCall, response)
        assert(called)
    }

    @Test
    fun `markNotificationAsRead failure calls onResult with false and error`() {
        val repo = NotificationsRepository(mockApiService)
        val response = Response.error<NotificationUpdateResponse>(
            400, "error".toResponseBody("application/json".toMediaTypeOrNull())
        )
        `when`(mockApiService.markNotificationAsRead(1)).thenReturn(mockMarkCall)

        var error: String? = null
        repo.markNotificationAsRead(1) { success, err ->
            if (!success) error = err
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<NotificationUpdateResponse>>
        verify(mockMarkCall).enqueue(captor.capture())
        captor.value.onResponse(mockMarkCall, response)
        assert(error?.contains("Failed to mark notification as read") == true)
    }

    @Test
    fun `markNotificationAsRead network failure calls onResult with false and network error`() {
        val repo = NotificationsRepository(mockApiService)
        `when`(mockApiService.markNotificationAsRead(1)).thenReturn(mockMarkCall)

        var called = false
        var error: String? = null
        repo.markNotificationAsRead(1) { success, err ->
            called = true
            if (!success) error = err
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<NotificationUpdateResponse>>
        verify(mockMarkCall).enqueue(captor.capture())
        captor.value.onFailure(mockMarkCall, Throwable("timeout"))
        assert(called)
        assert(error == "timeout" || (error != null && error!!.contains("timeout")))
    }

    @Test
    fun `markAllNotificationsAsRead success calls onResult with true`() {
        val repo = NotificationsRepository(mockApiService)
        val response = Response.success(NotificationMarkAllReadResponse(
            "msg",
            1
        ))
        `when`(mockApiService.markAllNotificationsAsRead()).thenReturn(mockMarkAllCall)

        var called = false
        repo.markAllNotificationsAsRead { success, error ->
            if (success) called = true
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<NotificationMarkAllReadResponse>>
        verify(mockMarkAllCall).enqueue(captor.capture())
        captor.value.onResponse(mockMarkAllCall, response)
        assert(called)
    }

    @Test
    fun `markAllNotificationsAsRead failure calls onResult with false and error`() {
        val repo = NotificationsRepository(mockApiService)
        val response = Response.error<NotificationMarkAllReadResponse>(
            400, "error".toResponseBody("application/json".toMediaTypeOrNull())
        )
        `when`(mockApiService.markAllNotificationsAsRead()).thenReturn(mockMarkAllCall)

        var error: String? = null
        repo.markAllNotificationsAsRead { success, err ->
            if (!success) error = err
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<NotificationMarkAllReadResponse>>
        verify(mockMarkAllCall).enqueue(captor.capture())
        captor.value.onResponse(mockMarkAllCall, response)
        assert(error?.contains("Failed to mark all notifications as read") == true)
    }

    @Test
    fun `markAllNotificationsAsRead network failure calls onResult with false and network error`() {
        val repo = NotificationsRepository(mockApiService)
        `when`(mockApiService.markAllNotificationsAsRead()).thenReturn(mockMarkAllCall)

        var error: String? = null
        repo.markAllNotificationsAsRead { success, err ->
            if (!success) error = err
        }

        val captor = ArgumentCaptor.forClass(Callback::class.java) as ArgumentCaptor<Callback<NotificationMarkAllReadResponse>>
        verify(mockMarkAllCall).enqueue(captor.capture())
        captor.value.onFailure(mockMarkAllCall, Throwable("timeout"))
        assert(error == "timeout" || (error != null && error!!.contains("timeout")))
    }
}