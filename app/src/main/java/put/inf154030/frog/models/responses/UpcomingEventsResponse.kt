package put.inf154030.frog.models.responses

import put.inf154030.frog.models.UpcomingEvent

data class UpcomingEventsResponse(
    val upcoming_events: List<UpcomingEvent>
)
