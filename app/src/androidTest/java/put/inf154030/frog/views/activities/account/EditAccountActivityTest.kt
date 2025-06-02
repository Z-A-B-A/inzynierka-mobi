package put.inf154030.frog.views.activities.account

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class EditAccountActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<EditAccountActivity>()

    @Test
    fun editAccountScreen_displaysHeaderBar() {
        composeTestRule.onNodeWithText("Account").assertIsDisplayed()
    }

    @Test
    fun editAccountScreen_displaysNameAndEmailFields() {
        composeTestRule.onNodeWithText("Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
    }

    @Test
    fun editAccountScreen_saveButton_disabledForInvalidEmail() {
        // Enter invalid email
        composeTestRule.onAllNodes(hasSetTextAction())[1].performTextInput("invalid-email")
        // Save button should be disabled
        composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
        // Error message should be shown
        composeTestRule.onNodeWithText("Enter valid email address.").assertIsDisplayed()
    }

    @Test
    fun editAccountScreen_saveButton_enabledForValidInput() {
        // Enter valid name and email
        composeTestRule.onAllNodes(hasSetTextAction())[0].performTextInput("Test User")
        composeTestRule.onAllNodes(hasSetTextAction())[1].performTextInput("test@example.com")
        // Save button should be enabled
        composeTestRule.onNodeWithText("Save").assertIsEnabled()
    }

    @Test
    fun editAccountScreen_loadingIndicatorIsShownWhenLoading() {
        // Simulate loading state by clicking Save (if possible)
        // Or, if you can inject isLoading, launch the screen with isLoading = true
        // Here, we just check if the CircularProgressIndicator exists after clicking Save
        composeTestRule.onAllNodes(hasSetTextAction())[0].performTextInput("Test User")
        composeTestRule.onAllNodes(hasSetTextAction())[1].performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Save").performClick()
        // CircularProgressIndicator should be shown (may need a testTag for reliability)
        composeTestRule.onNode(hasTestTag("CircularProgressIndicator")).assertExists()
    }

    @Test
    fun editAccountScreen_backButton_closesActivity() {
        // Assuming BackButton has a contentDescription "Back"
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            assert(activity.isFinishing)
        }
    }
}