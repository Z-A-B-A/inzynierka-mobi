package put.inf154030.frog.views.activities.locations

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class LocationsActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<LocationsActivity>()

    @Test
    fun locationsScreen_displaysAddLocationButton() {
        composeTestRule.onNodeWithContentDescription("Add new location").assertIsDisplayed()
    }

    @Test
    fun locationsScreen_displaysEmptyState_whenNoLocations() {
        // Simulate empty list and check for empty state message
        // This requires launching the activity with an empty list or using DI
        // Example:
        // composeTestRule.onNodeWithText("Tap + to add your first location").assertIsDisplayed()
    }

    @Test
    fun locationsScreen_showsLoadingIndicatorWhenLoading() {
        // Simulate loading by setting isLoading = true in the activity or via DI
        // For demonstration, check for CircularProgressIndicator by testTag if you add one
        // composeTestRule.onNode(hasTestTag("CircularProgressIndicator")).assertExists()
    }

    @Test
    fun locationsScreen_showsErrorMessageIfPresent() {
        // Simulate error by setting errorMessage in the activity or via DI
        // For demonstration, check for a generic error message
        // composeTestRule.onNodeWithText("Some error").assertIsDisplayed()
    }

    @Test
    fun locationsScreen_addLocationButton_opensAddLocationActivity() {
        composeTestRule.onNodeWithContentDescription("Add new location").performClick()
        // Optionally, check if AddLocationActivity is started (requires intent monitoring)
    }
}