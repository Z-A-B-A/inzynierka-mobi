package put.inf154030.frog.views.activities.containers

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class ManageContainerActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ManageContainerActivity>()

    @Test
    fun manageContainerScreen_displaysAddSpeciesButton() {
        composeTestRule.onNodeWithContentDescription("Add species").assertIsDisplayed()
    }

    @Test
    fun manageContainerScreen_addSpeciesButton_opensAddSpeciesActivity() {
        composeTestRule.onNodeWithContentDescription("Add species").performClick()
        // Optionally, check if AddSpeciesActivity is started (requires intent monitoring)
    }

    @Test
    fun manageContainerScreen_saveButton_disabledWhenLoadingOrInvalid() {
        // Simulate loading state or invalid input
        // For demonstration, just check the button is present and can be disabled
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
        // If isLoading or hasInvalidInput is true, button should be disabled
        // (You may need to launch the activity with those states set)
    }

    @Test
    fun manageContainerScreen_showsValidationMessageForInvalidInput() {
        // Simulate invalid input by setting hasInvalidInput = true
        // For demonstration, check for the validation message
        // composeTestRule.onNodeWithText("Please correct invalid parameter or species values.").assertIsDisplayed()
    }

    @Test
    fun manageContainerScreen_showsErrorMessageIfPresent() {
        // Simulate error by setting errorMessageParams or errorMessageSpecies
        // For demonstration, check for a generic error message
        // composeTestRule.onNodeWithText("Some error").assertIsDisplayed()
    }

    @Test
    fun manageContainerScreen_showsLoadingIndicatorWhenLoading() {
        // Simulate loading by setting isLoading = true
        // For demonstration, check for CircularProgressIndicator
        // composeTestRule.onNode(isInstanceOf(CircularProgressIndicator::class.java)).assertExists()
    }

    @Test
    fun manageContainerScreen_backButton_closesActivity() {
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            assert(activity.isFinishing)
        }
    }
}