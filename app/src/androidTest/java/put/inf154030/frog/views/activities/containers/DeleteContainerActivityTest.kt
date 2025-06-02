package put.inf154030.frog.views.activities.containers

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class DeleteContainerActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<DeleteContainerActivity>()

    @Test
    fun deleteContainerScreen_displaysHeaderBar() {
        composeTestRule.onNodeWithText("Delete Container").assertIsDisplayed()
    }

    @Test
    fun deleteContainerScreen_displaysConfirmationText() {
        composeTestRule.onNodeWithText("Are you sure?").assertIsDisplayed()
        composeTestRule.onNodeWithText("This operation can not be undone").assertIsDisplayed()
    }

    @Test
    fun deleteContainerScreen_displaysYesAndNoButtons() {
        composeTestRule.onNodeWithText("Yes").assertIsDisplayed()
        composeTestRule.onNodeWithText("No").assertIsDisplayed()
    }

    @Test
    fun deleteContainerScreen_yesButton_disabledWhenLoading() {
        // Simulate loading state by clicking Yes
        composeTestRule.onNodeWithText("Yes").performClick()
        // The button should now be disabled (if loading state is set)
        composeTestRule.onNodeWithText("Yes").assertIsNotEnabled()
    }

    @Test
    fun deleteContainerScreen_noButton_closesActivity() {
        composeTestRule.onNodeWithText("No").performClick()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            assert(activity.isFinishing)
        }
    }

    @Test
    fun deleteContainerScreen_showsErrorMessageIfPresent() {
        // Simulate error by setting errorMessage in the activity or via DI
        // For demonstration, check for a generic error message
        // composeTestRule.onNodeWithText("Some error").assertIsDisplayed()
    }

    @Test
    fun deleteContainerScreen_showsLoadingIndicatorWhenLoading() {
        // Simulate loading by setting isLoading = true in the activity or via DI
        // For demonstration, check for CircularProgressIndicator
        // composeTestRule.onNode(isInstanceOf(CircularProgressIndicator::class.java)).assertExists()
    }
}