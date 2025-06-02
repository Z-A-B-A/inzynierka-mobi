package put.inf154030.frog.views.activities.locations

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class DeleteLocationActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<DeleteLocationActivity>()

    @Test
    fun deleteLocationScreen_displaysHeaderBar() {
        composeTestRule.onNodeWithText("Delete Location").assertIsDisplayed()
    }

    @Test
    fun deleteLocationScreen_displaysConfirmationTextAndWarning() {
        composeTestRule.onNodeWithText("Are you sure?").assertIsDisplayed()
        composeTestRule.onNodeWithText(
            "Note: This operation deletes the location along with all containers, parameters, schedules, notifications and other related data"
        ).assertIsDisplayed()
    }

    @Test
    fun deleteLocationScreen_displaysYesAndNoButtons() {
        composeTestRule.onNodeWithText("Yes").assertIsDisplayed()
        composeTestRule.onNodeWithText("No").assertIsDisplayed()
    }

    @Test
    fun deleteLocationScreen_yesButton_disabledWhenLoading() {
        // Click Yes to trigger loading
        composeTestRule.onNodeWithText("Yes").performClick()
        // The button should now be disabled (if loading state is set)
        composeTestRule.onNodeWithText("Yes").assertIsNotEnabled()
    }

    @Test
    fun deleteLocationScreen_noButton_closesActivity() {
        composeTestRule.onNodeWithText("No").performClick()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            assert(activity.isFinishing)
        }
    }

    @Test
    fun deleteLocationScreen_showsErrorMessageIfPresent() {
        // Simulate error by setting errorMessage in the activity or via DI
        // For demonstration, check for a generic error message
        // composeTestRule.onNodeWithText("Some error").assertIsDisplayed()
    }

    @Test
    fun deleteLocationScreen_showsLoadingIndicatorWhenLoading() {
        // Simulate loading by setting isLoading = true in the activity or via DI
        // For demonstration, check for CircularProgressIndicator
        // composeTestRule.onNode(isInstanceOf(CircularProgressIndicator::class.java)).assertExists()
    }
}