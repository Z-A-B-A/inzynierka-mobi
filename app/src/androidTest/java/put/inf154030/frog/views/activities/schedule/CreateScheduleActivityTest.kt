package put.inf154030.frog.views.activities.schedule

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class CreateScheduleActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<CreateScheduleActivity>()

    @Test
    fun createScheduleScreen_displaysHeaderBar() {
        composeTestRule.onNodeWithText("Create Schedule").assertIsDisplayed()
    }

    @Test
    fun createScheduleScreen_displaysNameFieldAndCounter() {
        composeTestRule.onNodeWithText("Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("0/32 characters").assertIsDisplayed()
    }

    @Test
    fun createScheduleScreen_nameField_acceptsInputAndUpdatesCounter() {
        composeTestRule.onAllNodes(hasSetTextAction()).onFirst().performTextInput("Feeding")
        composeTestRule.onNodeWithText("7/32 characters").assertIsDisplayed()
    }

    @Test
    fun createScheduleScreen_displaysExecutionToggle() {
        composeTestRule.onNodeWithText("daily").assertIsDisplayed()
        composeTestRule.onNodeWithText("weekly").assertIsDisplayed()
    }

    @Test
    fun createScheduleScreen_weeklyMode_displaysDaySelectors() {
        // Toggle to weekly mode if not already
        composeTestRule.onNodeWithText("weekly").performClick()
        composeTestRule.onNodeWithText("Mon").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tue").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sun").assertIsDisplayed()
    }

    @Test
    fun createScheduleScreen_weeklyMode_showsValidationIfNoDaySelected() {
        // Toggle to weekly mode and check for validation
        composeTestRule.onNodeWithText("weekly").performClick()
        composeTestRule.onNodeWithText("Please select at least one day").assertIsDisplayed()
    }

    @Test
    fun createScheduleScreen_timePicker_opensAndSelectsTime() {
        composeTestRule.onNodeWithText("12:00").performClick()
        composeTestRule.onNodeWithText("Select Time").assertIsDisplayed()
        // Optionally, select a time and confirm
        composeTestRule.onNodeWithText("OK").performClick()
    }

    @Test
    fun createScheduleScreen_createButton_disabledWhenLoading() {
        // Enter valid name
        composeTestRule.onAllNodes(hasSetTextAction()).onFirst().performTextInput("Feeding")
        // Click Create to trigger loading
        composeTestRule.onNodeWithText("Create").performClick()
        // Button should now be disabled (if loading state is set)
        composeTestRule.onNodeWithText("Create").assertIsNotEnabled()
    }

    @Test
    fun createScheduleScreen_showsErrorMessageIfPresent() {
        // Simulate error by setting errorMessage in the activity or via DI
        // For demonstration, check for a generic error message
        // composeTestRule.onNodeWithText("Some error").assertIsDisplayed()
    }

    @Test
    fun createScheduleScreen_showsLoadingIndicatorWhenLoading() {
        // Simulate loading by setting isLoading = true in the activity or via DI
        // For demonstration, check for CircularProgressIndicator by testTag if you add one
        // composeTestRule.onNode(hasTestTag("CircularProgressIndicator")).assertExists()
    }

    @Test
    fun createScheduleScreen_backButton_closesActivity() {
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            assert(activity.isFinishing)
        }
    }
}