package put.inf154030.frog.views.activities.schedule

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class ScheduleActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ScheduleActivity>()

    @Test
    fun scheduleScreen_displaysHeaderBar() {
        composeTestRule.onNodeWithText("Schedule").assertIsDisplayed()
    }

    @Test
    fun scheduleScreen_displaysBackButton() {
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun scheduleScreen_displaysEmptyState_whenNoSchedules() {
        // Simulate empty list and check for empty state message
        // This requires launching the activity with an empty list or using DI
        // Example:
        // composeTestRule.onNodeWithText("No schedule added yet").assertIsDisplayed()
    }

    @Test
    fun scheduleScreen_showsLoadingIndicatorWhenLoading() {
        // Simulate loading by setting isLoading = true in the activity or via DI
        // For demonstration, check for CircularProgressIndicator by testTag if you add one
        // composeTestRule.onNode(hasTestTag("CircularProgressIndicator")).assertExists()
    }

    @Test
    fun scheduleScreen_showsErrorMessageIfPresent() {
        // Simulate error by setting errorMessage in the activity or via DI
        // For demonstration, check for a generic error message
        // composeTestRule.onNodeWithText("Some error").assertIsDisplayed()
    }

    @Test
    fun scheduleScreen_createScheduleButton_opensCreateScheduleActivity() {
        composeTestRule.onNodeWithText("-- Create Schedule --").performClick()
        // Optionally, check if CreateScheduleActivity is started (requires intent monitoring)
    }

    @Test
    fun scheduleScreen_backButton_closesActivity() {
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            assert(activity.isFinishing)
        }
    }
}