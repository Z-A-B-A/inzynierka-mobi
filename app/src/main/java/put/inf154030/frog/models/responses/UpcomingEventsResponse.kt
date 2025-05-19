package put.inf154030.frog.models.responses

import com.google.gson.annotations.SerializedName
import put.inf154030.frog.models.UpcomingEvent

data class UpcomingEventsResponse(
    @SerializedName("upcoming_events") val upcomingEvents: List<UpcomingEvent>
)
