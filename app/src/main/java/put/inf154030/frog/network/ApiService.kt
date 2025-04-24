package put.inf154030.frog.network

import put.inf154030.frog.models.*
import put.inf154030.frog.models.requests.*
import put.inf154030.frog.models.responses.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    // Auth Endpoints
    @POST("auth/login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<AuthResponse>

    @POST("auth/register")
    fun registerUser(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    // Location Endpoints
    @GET("locations")
    fun getLocations(): Call<LocationsResponse>

    @POST("locations")
    fun createLocation(@Body location: LocationCreateRequest): Call<LocationResponse>

    @GET("locations/{id}")
    fun getLocation(@Path("id") locationId: Int): Call<LocationDetailResponse>

    // Container Endpoints
    @GET("locations/{location_id}/containers")
    fun getContainers(@Path("location_id") locationId: Int): Call<ContainersResponse>

    @POST("locations/{location_id}/containers")
    fun createContainer(@Path("location_id") locationId: Int, @Body container: ContainerCreateRequest): Call<ContainerResponse>

    @GET("containers/{id}")
    fun getContainerDetails(@Path("id") containerId: Int): Call<ContainerDetailResponse>

    @PUT("containers/{id}")
    fun updateContainer(@Path("id") containerId: Int, @Body container: ContainerUpdateRequest): Call<ContainerResponse>

    @DELETE("containers/{id}")
    fun deleteContainer(@Path("id") containerId: Int): Call<MessageResponse>

    // Parameter Endpoints
    @GET("containers/{container_id}/parameters")
    fun getParameters(@Path("container_id") containerId: Int): Call<ParametersResponse>

    @POST("containers/{container_id}/parameters")
    fun createParameter(@Path("container_id") containerId: Int, @Body parameter: ParameterCreateRequest): Call<ParameterResponse>

    @POST("containers/{container_id}/preset-parameters")
    fun addPresetParameters(@Path("container_id") containerId: Int, @Body request: PresetParametersRequest): Call<PresetParametersResponse>

    @PUT("parameters/{id}")
    fun updateParameter(@Path("id") parameterId: Int, @Body parameter: ParameterUpdateRequest): Call<ParameterResponse>

    @POST("parameters/{id}/update-value")
    fun updateParameterValue(@Path("id") parameterId: Int, @Body request: ParameterValueUpdateRequest): Call<ParameterValueResponse>

    @GET("parameters/{id}/history")
    fun getParameterHistory(@Path("id") parameterId: Int, @Query("from_date") fromDate: String?, @Query("to_date") toDate: String?): Call<ParameterHistoryResponse>

    // Schedule Endpoints
    @GET("containers/{container_id}/schedules")
    fun getSchedules(@Path("container_id") containerId: Int): Call<SchedulesResponse>

    @POST("containers/{container_id}/schedules")
    fun createSchedule(@Path("container_id") containerId: Int, @Body schedule: ScheduleCreateRequest): Call<ScheduleResponse>

    @PUT("schedules/{id}")
    fun updateSchedule(@Path("id") scheduleId: Int, @Body schedule: ScheduleUpdateRequest): Call<ScheduleResponse>

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
    fun removeSpeciesFromContainer(@Path("container_id") containerId: Int, @Path("species_id") speciesId: Int): Call<MessageResponse>

    // Notification Endpoints
    @GET("notifications")
    fun getNotifications(@Query("unread_only") unreadOnly: Boolean?): Call<NotificationsResponse>

    @POST("notifications/{id}/mark-read")
    fun markNotificationAsRead(@Path("id") notificationId: Int): Call<NotificationUpdateResponse>

    @POST("notifications/mark-all-read")
    fun markAllNotificationsAsRead(): Call<NotificationMarkAllReadResponse>

    @GET("notifications/upcoming")
    fun getUpcomingNotifications(@Query("days") days: Int?): Call<UpcomingEventsResponse>
}