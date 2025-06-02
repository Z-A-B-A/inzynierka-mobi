package put.inf154030.frog.views.activities.locations

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class AddLocationActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<AddLocationActivity>()

    @Test
    fun addLocationScreen_displaysHeaderBar() {
        composeTestRule.onNodeWithText("New Location").assertIsDisplayed()
    }

    @Test
    fun addLocationScreen_displaysNameFieldAndCounter() {
        composeTestRule.onNodeWithText("Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("0/32 characters").assertIsDisplayed()
    }

    @Test
    fun addLocationScreen_nameField_acceptsInputAndUpdatesCounter() {
        composeTestRule.onAllNodes(hasSetTextAction()).onFirst().performTextInput("Test Location")
        composeTestRule.onNodeWithText("13/32 characters").assertIsDisplayed()
    }

    @Test
    fun addLocationScreen_showsErrorMessageForEmptyName() {
        // Click Add without entering a name
        composeTestRule.onNodeWithText("Add").performClick()
        composeTestRule.onNodeWithText("Location name cannot be empty").assertIsDisplayed()
    }

    @Test
    fun addLocationScreen_backButton_closesActivity() {
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        // Assert the activity is destroyed
        assert(composeTestRule.activityRule.scenario.state == androidx.lifecycle.Lifecycle.State.DESTROYED)
    }
}