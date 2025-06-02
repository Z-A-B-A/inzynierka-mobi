package put.inf154030.frog.views.activities.containers

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class AddContainerNextStepActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<AddContainerNextStepActivity>()

    @Test
    fun addContainerNextStepScreen_displaysHeaderBar() {
        composeTestRule.onNodeWithText("New Container").assertIsDisplayed()
    }

    @Test
    fun addContainerNextStepScreen_displaysNameAndDescriptionFields() {
        composeTestRule.onNodeWithText("Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Description").assertIsDisplayed()
    }

    @Test
    fun addContainerNextStepScreen_finishButton_disabledWhenLoading() {
        // Enter valid name and description
        composeTestRule.onAllNodes(hasSetTextAction())[0].performTextInput("Test Container")
        composeTestRule.onAllNodes(hasSetTextAction())[1].performTextInput("Test Description")
        // Click Finish to trigger loading
        composeTestRule.onNodeWithText("Finish").performClick()
        // Button should now show "Loading..." and be disabled
        composeTestRule.onNodeWithText("Loading...").assertIsDisplayed()
        composeTestRule.onNodeWithText("Loading...").assertIsNotEnabled()
    }

    @Test
    fun addContainerNextStepScreen_showsErrorMessageForEmptyFields() {
        // Click Finish without entering anything
        composeTestRule.onNodeWithText("Finish").performClick()
        composeTestRule.onNodeWithText("Container name cannot be empty").assertIsDisplayed()
        composeTestRule.onNodeWithText("Description cannot be empty").assertIsDisplayed()
    }

    @Test
    fun addContainerNextStepScreen_displaysCharacterCounters() {
        // Enter some text and check counters
        composeTestRule.onAllNodes(hasSetTextAction())[0].performTextInput("Test")
        composeTestRule.onNodeWithText("4/32 characters").assertIsDisplayed()
        composeTestRule.onAllNodes(hasSetTextAction())[1].performTextInput("Desc")
        composeTestRule.onNodeWithText("4/300 characters").assertIsDisplayed()
    }

    @Test
    fun addContainerNextStepScreen_backButton_closesActivity() {
        // Assuming BackButton has a contentDescription "Back"
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            assert(activity.isFinishing)
        }
    }
}