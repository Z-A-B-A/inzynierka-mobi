package put.inf154030.frog.views.activities.notifications

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class NotificationsActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<NotificationsActivity>()

    @Test
    fun notificationsScreen_displaysHeaderBar() {
        composeTestRule.onNodeWithText("Notifications").assertIsDisplayed()
    }

    @Test
    fun notificationsScreen_displaysBackButton() {
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun notificationsScreen_displaysMarkAllAsRead() {
        composeTestRule.onNodeWithText("mark all as read").assertIsDisplayed()
    }

    @Test
    fun notificationsScreen_displaysEmptyState_whenNoNotifications() {
        // Simulate empty list and check for empty state message
        // This requires launching the activity with an empty list or using DI
        // Example:
        // composeTestRule.onNodeWithText("No new notifications").assertIsDisplayed()
    }

    @Test
    fun notificationsScreen_showsLoadingIndicatorWhenLoading() {
        // Simulate loading by setting isLoading = true in the activity or via DI
        // For demonstration, check for CircularProgressIndicator by testTag if you add one
        // composeTestRule.onNode(hasTestTag("CircularProgressIndicator")).assertExists()
    }

    @Test
    fun notificationsScreen_showsErrorMessageIfPresent() {
        // Simulate error by setting errorMessage in the activity or via DI
        // For demonstration, check for a generic error message
        // composeTestRule.onNodeWithText("Some error").assertIsDisplayed()
    }

    @Test
    fun notificationsScreen_backButton_closesActivity() {
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            assert(activity.isFinishing)
        }
    }

    @Test
    fun notificationsScreen_markAllAsRead_triggersAction() {
        composeTestRule.onNodeWithText("mark all as read").performClick()
        // Optionally, check if all notifications are marked as read (requires state observation)
    }
}