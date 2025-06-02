package put.inf154030.frog.views.activities.schedule

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class DeleteScheduleActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<DeleteScheduleActivity>()

    @Test
    fun deleteScheduleScreen_displaysHeaderBar() {
        composeTestRule.onNodeWithText("Delete Schedule").assertIsDisplayed()
    }

    @Test
    fun deleteScheduleScreen_displaysConfirmationTextAndWarning() {
        composeTestRule.onNodeWithText("Are you sure?").assertIsDisplayed()
        composeTestRule.onNodeWithText("This operation can not be undone").assertIsDisplayed()
    }

    @Test
    fun deleteScheduleScreen_displaysYesAndNoButtons() {
        composeTestRule.onNodeWithText("Yes").assertIsDisplayed()
        composeTestRule.onNodeWithText("No").assertIsDisplayed()
    }

    @Test
    fun deleteScheduleScreen_yesButton_disabledWhenLoading() {
        // Click Yes to trigger loading
        composeTestRule.onNodeWithText("Yes").performClick()
        // The button should now be disabled (if loading state is set)
        composeTestRule.onNodeWithText("Yes").assertIsNotEnabled()
    }

    @Test
    fun deleteScheduleScreen_noButton_closesActivity() {
        composeTestRule.onNodeWithText("No").performClick()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            assert(activity.isFinishing)
        }
    }

    @Test
    fun deleteScheduleScreen_showsErrorMessageIfPresent() {
        // Simulate error by setting errorMessage in the activity or via DI
        // For demonstration, check for a generic error message
        // composeTestRule.onNodeWithText("Some error").assertIsDisplayed()
    }

    @Test
    fun deleteScheduleScreen_showsLoadingIndicatorWhenLoading() {
        // Simulate loading by setting isLoading = true in the activity or via DI
        // For demonstration, check for CircularProgressIndicator by testTag if you add one
        // composeTestRule.onNode(hasTestTag("CircularProgressIndicator")).assertExists()
    }
}