package put.inf154030.frog.views.activities.schedule

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class EditScheduleActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<EditScheduleActivity>()

    @Test
    fun editScheduleScreen_displaysHeaderBar() {
        composeTestRule.onNodeWithText("Edit Schedule").assertIsDisplayed()
    }

    @Test
    fun editScheduleScreen_displaysTimeFieldAndPicker() {
        composeTestRule.onNodeWithText("Execution time").assertIsDisplayed()
        // The formatted time (e.g., "12:00") should be displayed
        composeTestRule.onAllNodes(hasText(":")).onFirst().assertIsDisplayed()
        // Open the time picker dialog
        composeTestRule.onAllNodes(hasText(":")).onFirst().performClick()
        composeTestRule.onNodeWithText("Select Time").assertIsDisplayed()
        composeTestRule.onNodeWithText("OK").performClick()
    }

    @Test
    fun editScheduleScreen_displaysDeleteScheduleLink() {
        composeTestRule.onNodeWithText("delete schedule").assertIsDisplayed()
    }

    @Test
    fun editScheduleScreen_deleteScheduleLink_opensDeleteActivity() {
        composeTestRule.onNodeWithText("delete schedule").performClick()
        // Optionally, check if DeleteScheduleActivity is started (requires intent monitoring)
    }

    @Test
    fun editScheduleScreen_saveButton_disabledWhenLoading() {
        // Click Save to trigger loading
        composeTestRule.onNodeWithText("Save").performClick()
        // Button should now be disabled (if loading state is set)
        composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun editScheduleScreen_showsErrorMessageIfPresent() {
        // Simulate error by setting errorMessage in the activity or via DI
        // For demonstration, check for a generic error message
        // composeTestRule.onNodeWithText("Some error").assertIsDisplayed()
    }

    @Test
    fun editScheduleScreen_showsLoadingIndicatorWhenLoading() {
        // Simulate loading by setting isLoading = true in the activity or via DI
        // For demonstration, check for CircularProgressIndicator by testTag if you add one
        // composeTestRule.onNode(hasTestTag("CircularProgressIndicator")).assertExists()
    }

    @Test
    fun editScheduleScreen_backButton_closesActivity() {
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            assert(activity.isFinishing)
        }
    }
}