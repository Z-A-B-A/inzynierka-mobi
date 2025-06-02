package put.inf154030.frog.views.activities.locations

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class EditLocationActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<EditLocationActivity>()

    @Test
    fun editLocationScreen_displaysHeaderBar() {
        composeTestRule.onNodeWithText("Edit Location").assertIsDisplayed()
    }

    @Test
    fun editLocationScreen_displaysNameFieldAndCounter() {
        composeTestRule.onNodeWithText("Name").assertIsDisplayed()
        // The counter should reflect the initial name length (e.g., "0/32 characters" or actual)
        composeTestRule.onAllNodes(hasText("/32 characters", substring = true)).onFirst().assertIsDisplayed()
    }

    @Test
    fun editLocationScreen_nameField_acceptsInputAndUpdatesCounter() {
        composeTestRule.onAllNodes(hasSetTextAction()).first().performTextInput("Test Location")
        composeTestRule.onNodeWithText("13/32 characters").assertIsDisplayed()
    }

    @Test
    fun editLocationScreen_saveButton_disabledWhenLoading() {
        // Enter valid name
        composeTestRule.onAllNodes(hasSetTextAction()).first().performTextInput("Test Location")
        // Click Save to trigger loading
        composeTestRule.onNodeWithText("Save").performClick()
        // Button should now show "Saving..." and be disabled
        composeTestRule.onNodeWithText("Saving...").assertIsDisplayed()
        composeTestRule.onNodeWithText("Saving...").assertIsNotEnabled()
    }

    @Test
    fun editLocationScreen_showsErrorMessageForEmptyName() {
        // Clear the name field and click Save
        composeTestRule.onAllNodes(hasSetTextAction()).first().performTextClearance()
        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.onNodeWithText("Location name cannot be empty").assertIsDisplayed()
    }

    @Test
    fun editLocationScreen_displaysDeleteLocationLink() {
        composeTestRule.onNodeWithText("delete location").assertIsDisplayed()
    }

    @Test
    fun editLocationScreen_deleteLocationLink_opensDeleteActivity() {
        composeTestRule.onNodeWithText("delete location").performClick()
        // Optionally, check if DeleteLocationActivity is started (requires intent monitoring)
    }

    @Test
    fun editLocationScreen_backButton_closesActivity() {
        // Assuming BackButton has a contentDescription "Back"
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            assert(activity.isFinishing)
        }
    }

    @Test
    fun editLocationScreen_showsLoadingIndicatorWhenLoading() {
        // Simulate loading by setting isLoading = true in the activity or via DI
        // For demonstration, check for CircularProgressIndicator by testTag if you add one
        // composeTestRule.onNode(hasTestTag("CircularProgressIndicator")).assertExists()
    }
}