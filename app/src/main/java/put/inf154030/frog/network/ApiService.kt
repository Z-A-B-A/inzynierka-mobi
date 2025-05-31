package put.inf154030.frog.network

import put.inf154030.frog.models.requests.AddSpeciesRequest
import put.inf154030.frog.models.requests.ContainerCreateRequest
import put.inf154030.frog.models.requests.ContainerUpdateRequest
import put.inf154030.frog.models.requests.DeviceTokenRequest
import put.inf154030.frog.models.requests.LocationCreateRequest
import put.inf154030.frog.models.requests.LocationUpdateRequest
import put.inf154030.frog.models.requests.LoginRequest
import put.inf154030.frog.models.requests.ParameterUpdateRequest
import put.inf154030.frog.models.requests.RegisterRequest
import put.inf154030.frog.models.requests.ScheduleCreateRequest
import put.inf154030.frog.models.requests.ScheduleUpdateRequest
import put.inf154030.frog.models.requests.UpdateSpeciesCountRequest
import put.inf154030.frog.models.requests.UserUpdateRequest
import put.inf154030.frog.models.responses.AuthResponse
import put.inf154030.frog.models.responses.ContainerDetailResponse
import put.inf154030.frog.models.responses.ContainerResponse
import put.inf154030.frog.models.responses.ContainerSpeciesItemResponse
import put.inf154030.frog.models.responses.ContainerSpeciesResponse
import put.inf154030.frog.models.responses.ContainerSpeciesUpdateResponse
import put.inf154030.frog.models.responses.ContainerUpdateResponse
import put.inf154030.frog.models.responses.ContainersResponse
import put.inf154030.frog.models.responses.LocationDetailResponse
import put.inf154030.frog.models.responses.LocationResponse
import put.inf154030.frog.models.responses.LocationUpdateResponse
import put.inf154030.frog.models.responses.LocationsResponse
import put.inf154030.frog.models.responses.MessageResponse
import put.inf154030.frog.models.responses.NotificationMarkAllReadResponse
import put.inf154030.frog.models.responses.NotificationUpdateResponse
import put.inf154030.frog.models.responses.NotificationsResponse
import put.inf154030.frog.models.responses.ParameterHistoryResponse
import put.inf154030.frog.models.responses.ParameterResponse
import put.inf154030.frog.models.responses.RegisterResponse
import put.inf154030.frog.models.responses.ScheduleResponse
import put.inf154030.frog.models.responses.ScheduleUpdateResponse
import put.inf154030.frog.models.responses.SchedulesResponse
import put.inf154030.frog.models.responses.SpeciesDetailResponse
import put.inf154030.frog.models.responses.SpeciesListResponse
import put.inf154030.frog.models.responses.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Auth Endpoints
    @POST("auth/login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<AuthResponse>

    @POST("auth/register")
    fun registerUser(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @POST("auth/device-token")
    fun updateDeviceToken(@Body request: DeviceTokenRequest): Call<MessageResponse>

    // Location Endpoints
    @GET("locations")
    fun getLocations(): Call<LocationsResponse>

    @POST("locations")
    fun createLocation(@Body location: LocationCreateRequest): Call<LocationResponse>

    @GET("locations/{id}")
    fun getLocation(@Path("id") locationId: Int): Call<LocationDetailResponse>

    @PUT("locations/{id}")
    fun updateLocation(@Path("id") locationId: Int, @Body locationUpdateRequest: LocationUpdateRequest): Call<LocationUpdateResponse>

    @DELETE("locations/{id}")
    fun deleteLocation(@Path("id") locationId: Int): Call<MessageResponse>

    // Container Endpoints
    @GET("locations/{location_id}/containers")
    fun getContainers(@Path("location_id") locationId: Int): Call<ContainersResponse>

    @POST("locations/{location_id}/containers")
    fun createContainer(@Path("location_id") locationId: Int, @Body container: ContainerCreateRequest): Call<ContainerResponse>

    @GET("containers/{id}")
    fun getContainerDetails(@Path("id") containerId: Int): Call<ContainerDetailResponse>

    @PUT("containers/{id}")
    fun updateContainer(@Path("id") containerId: Int, @Body container: ContainerUpdateRequest): Call<ContainerUpdateResponse>

    @DELETE("containers/{id}")
    fun deleteContainer(@Path("id") containerId: Int): Call<MessageResponse>

    // Parameter Endpoints
    @PUT("containers/{id}/parameters/{parameter_type}")
    fun updateParameter(@Path("id") containerId: Int, @Body parameter: ParameterUpdateRequest, @Path("parameter_type") parameterType: String): Call<ParameterResponse>

    @GET("containers/{container_id}/parameters/{parameter_type}/history")
    fun getParameterHistory(@Path("container_id") containerId: Int, @Path("parameter_type") parameterType: String, @Query("from_datetime") fromDate: String?, @Query("to_datetime") toDate: String?): Call<ParameterHistoryResponse>

    // Schedule Endpoints
    @GET("containers/{container_id}/schedules")
    fun getSchedules(@Path("container_id") containerId: Int): Call<SchedulesResponse>

    @POST("containers/{container_id}/schedules")
    fun createSchedule(@Path("container_id") containerId: Int, @Body schedule: ScheduleCreateRequest): Call<ScheduleResponse>

    @PUT("schedules/{id}")
    fun updateSchedule(@Path("id") scheduleId: Int, @Body schedule: ScheduleUpdateRequest): Call<ScheduleUpdateResponse>

    @DELETE("schedules/{id}")
    fun deleteSchedule(@Path("id") scheduleId: Int): Call<MessageResponse>

    // Species Endpoints
    @GET("species")
    fun getSpecies(@Query("category") category: String?): Call<SpeciesListResponse>

    @GET("species/{id}")
    fun getSpeciesDetails(@Path("id") speciesId: Int): Call<SpeciesDetailResponse>

    @GET("containers/{container_id}/species")
    fun getContainerSpecies(@Path("container_id") containerId: Int): Call<ContainerSpeciesResponse>

    @POST("containers/{container_id}/species")
    fun addSpeciesToContainer(@Path("container_id") containerId: Int, @Body request: AddSpeciesRequest): Call<ContainerSpeciesItemResponse>

    @PUT("containers/{container_id}/species/{species_id}")
    fun updateContainerSpecies(@Path("container_id") containerId: Int, @Path("species_id") speciesId: Int, @Body request: UpdateSpeciesCountRequest): Call<ContainerSpeciesUpdateResponse>

    @DELETE("containers/{container_id}/species/{species_id}")
    fun deleteSpeciesFromContainer(@Path("container_id") containerId: Int, @Path("species_id") speciesId: Int): Call<MessageResponse>

    // Notification Endpoints
    @GET("notifications")
    fun getNotifications(@Query("unread_only") unreadOnly: Boolean?): Call<NotificationsResponse>

    @POST("notifications/{id}/mark-read")
    fun markNotificationAsRead(@Path("id") notificationId: Int): Call<NotificationUpdateResponse>

    @POST("notifications/mark-all-read")
    fun markAllNotificationsAsRead(): Call<NotificationMarkAllReadResponse>

    // Profile endpoints
    @PUT("me")
    fun updateUser(@Body request: UserUpdateRequest): Call<UserResponse>
}