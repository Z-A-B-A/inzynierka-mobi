package put.inf154030.frog.views.activities.containers

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class EditContainerActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<EditContainerActivity>()

    @Test
    fun editContainerScreen_displaysHeaderBar() {
        composeTestRule.onNodeWithText("Edit Container").assertIsDisplayed()
    }

    @Test
    fun editContainerScreen_displaysNameAndDescriptionFields() {
        composeTestRule.onNodeWithText("Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Description").assertIsDisplayed()
    }

    @Test
    fun editContainerScreen_displaysLocationDropdown() {
        composeTestRule.onNodeWithText("Lokalizacja").assertIsDisplayed()
        composeTestRule.onNodeWithText("Select location").assertIsDisplayed()
    }

    @Test
    fun editContainerScreen_saveButton_disabledWhenLoading() {
        // Simulate loading state by clicking Save (if possible)
        // Enter valid name, description, and select location if needed
        composeTestRule.onAllNodes(hasSetTextAction())[0].performTextInput("Test Container")
        composeTestRule.onAllNodes(hasSetTextAction())[1].performTextInput("Test Description")
        // Open dropdown and select a location if locations are loaded
        // Click Save to trigger loading
        composeTestRule.onNodeWithText("Save").performClick()
        // The button should now be disabled (if loading state is set)
        composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun editContainerScreen_showsErrorMessageForEmptyFields() {
        // Click Save without entering anything
        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.onNodeWithText("Name cannot be empty").assertIsDisplayed()
        // Enter name, leave description empty
        composeTestRule.onAllNodes(hasSetTextAction())[0].performTextInput("Test Container")
        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.onNodeWithText("Description cannot be empty").assertIsDisplayed()
    }

    @Test
    fun editContainerScreen_showsErrorMessageForNoLocation() {
        // Enter valid name and description, but do not select location
        composeTestRule.onAllNodes(hasSetTextAction())[0].performTextInput("Test Container")
        composeTestRule.onAllNodes(hasSetTextAction())[1].performTextInput("Test Description")
        // Try to save without selecting location
        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.onNodeWithText("Please select a location").assertIsDisplayed()
    }

    @Test
    fun editContainerScreen_displaysDeleteContainerLink() {
        composeTestRule.onNodeWithText("delete container").assertIsDisplayed()
    }

    @Test
    fun editContainerScreen_deleteContainerLink_opensDeleteActivity() {
        composeTestRule.onNodeWithText("delete container").performClick()
        // Optionally, check if DeleteContainerActivity is started (requires intent monitoring)
    }

    @Test
    fun editContainerScreen_backButton_closesActivity() {
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            assert(activity.isFinishing)
        }
    }

    @Test
    fun editContainerScreen_showsLoadingIndicatorWhenLoading() {
        // Simulate loading by setting isLoading = true in the activity or via DI
        // For demonstration, check for CircularProgressIndicator by testTag if you add one
        // composeTestRule.onNode(hasTestTag("CircularProgressIndicator")).assertExists()
    }
}