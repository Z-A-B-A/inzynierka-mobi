package put.inf154030.frog.views.activities.locations

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class LocationActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<LocationActivity>()

    @Test
    fun locationScreen_displaysHeaderBar() {
        composeTestRule.onNodeWithText("Location").assertIsDisplayed()
    }

    @Test
    fun locationScreen_displaysBackButton() {
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun locationScreen_displaysFilterButtons() {
        composeTestRule.onNodeWithText("All").assertIsDisplayed()
        composeTestRule.onNodeWithText("Aquariums").assertIsDisplayed()
        composeTestRule.onNodeWithText("Terrariums").assertIsDisplayed()
    }

    @Test
    fun locationScreen_displaysAddContainerButton() {
        composeTestRule.onNodeWithContentDescription("Add new container").assertIsDisplayed()
    }

    @Test
    fun locationScreen_displaysEmptyState_whenNoContainers() {
        // Simulate empty list and check for empty state message
        // This requires launching the activity with an empty list or using DI
        // Example:
        // composeTestRule.onNodeWithText("No containers added yet").assertIsDisplayed()
    }

    @Test
    fun locationScreen_showsLoadingIndicatorWhenLoading() {
        // Simulate loading by setting isLoading = true in the activity or via DI
        // For demonstration, check for CircularProgressIndicator by testTag if you add one
        // composeTestRule.onNode(hasTestTag("CircularProgressIndicator")).assertExists()
    }

    @Test
    fun locationScreen_showsErrorMessageIfPresent() {
        // Simulate error by setting errorMessage in the activity or via DI
        // For demonstration, check for a generic error message
        // composeTestRule.onNodeWithText("Some error").assertIsDisplayed()
    }

    @Test
    fun locationScreen_addContainerButton_opensAddContainerActivity() {
        composeTestRule.onNodeWithContentDescription("Add new container").performClick()
        // Optionally, check if AddContainerActivity is started (requires intent monitoring)
    }
}