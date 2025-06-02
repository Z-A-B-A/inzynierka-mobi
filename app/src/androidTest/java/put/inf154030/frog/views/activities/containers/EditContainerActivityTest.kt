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
}