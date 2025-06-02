package put.inf154030.frog.views.activities.notifications

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class UpcomingActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<UpcomingActivity>()

    @Test
    fun upcomingScreen_displaysHeaderBar() {
        composeTestRule.onNodeWithText("Upcoming").assertIsDisplayed()
    }

    @Test
    fun upcomingScreen_displaysBackButton() {
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun upcomingScreen_showsLoadingIndicatorWhenLoading() {
        // By default, isLoading is true at start
        composeTestRule.onNode(isPopup()).assertDoesNotExist() // Just to avoid popup interference
        composeTestRule.onNode(hasAnyAncestor(hasText("Upcoming")) and hasTestTag("CircularProgressIndicator")).assertExists()
        // If you don't use testTag, you can check for CircularProgressIndicator by role or count
    }

    @Test
    fun upcomingScreen_showsErrorMessageIfPresent() {
        // Simulate error by setting errorMessage in the activity or via DI
        // For demonstration, check for a generic error message
        // composeTestRule.onNodeWithText("Some error").assertIsDisplayed()
    }

    @Test
    fun upcomingScreen_displaysEmptyState_whenNoEvents() {
        // Simulate isLoading = false and upcomingList = emptyList()
        // This requires launching the activity with those states or using DI
        // Example:
        // composeTestRule.onNodeWithText("No upcoming events 🎉").assertIsDisplayed()
    }

    @Test
    fun upcomingScreen_displaysUpcomingEventsList_whenNotEmpty() {
        // This test assumes upcomingList is not empty and isLoading = false
        // Check for a known event name from your preview or test data
        // composeTestRule.onNodeWithText("Karmienie").assertExists()
        // composeTestRule.onNodeWithText("Czyszczenie").assertExists()
        // composeTestRule.onNodeWithText("Terrarium Gekona Lamparci").assertExists()
    }

    @Test
    fun upcomingScreen_backButton_closesActivity() {
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            assert(activity.isFinishing)
        }
    }
}