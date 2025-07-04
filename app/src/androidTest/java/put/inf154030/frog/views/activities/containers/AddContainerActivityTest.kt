package put.inf154030.frog.views.activities.containers

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class AddContainerActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<AddContainerActivity>()

    @Test
    fun addContainerScreen_displaysHeaderBar() {
        composeTestRule.onNodeWithText("New Container").assertIsDisplayed()
    }

    @Test
    fun addContainerScreen_displaysBackButton() {
        // Assuming BackButton has a contentDescription "Back"
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun addContainerScreen_displaysScanQrButton() {
        composeTestRule.onNodeWithText("Scan QR Code").assertIsDisplayed()
    }

    @Test
    fun addContainerScreen_displaysManualEntrySection() {
        composeTestRule.onNodeWithText("Enter code manually").assertIsDisplayed()
        // The text field for code entry should be present
        composeTestRule.onAllNodes(hasSetTextAction()).onFirst().assertExists()
    }

    @Test
    fun addContainerScreen_showsErrorMessageForEmptyCode() {
        // Click Next without entering a code
        composeTestRule.onNodeWithText("Next").performClick()
        composeTestRule.onNodeWithText("Container code cannot be empty").assertIsDisplayed()
    }

    @Test
    fun addContainerScreen_backButton_closesActivity() {
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            assert(activity.isFinishing)
        }
    }
}