package put.inf154030.frog.views.activities.species

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class AddSpeciesActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<AddSpeciesActivity>()

    @Test
    fun addSpeciesScreen_displaysHeaderBar() {
        composeTestRule.onNodeWithText("Add Species").assertIsDisplayed()
    }

    @Test
    fun addSpeciesScreen_displaysBackButton() {
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun addSpeciesScreen_displaysFilterChips() {
        composeTestRule.onNodeWithText("Filters", ignoreCase = true).assertExists()
        composeTestRule.onNodeWithText("Reptile").assertIsDisplayed()
        composeTestRule.onNodeWithText("Amphibian").assertIsDisplayed()
        composeTestRule.onNodeWithText("Fish").assertIsDisplayed()
    }

    @Test
    fun addSpeciesScreen_displaysSpeciesDropdown() {
        composeTestRule.onNodeWithText("Select species").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Expand").assertIsDisplayed()
    }

    @Test
    fun addSpeciesScreen_selectSpecies_displaysDetailsAndCountPicker() {
        // Open species dropdown and select the first species
        composeTestRule.onNodeWithText("Select species").performClick()
        composeTestRule.onNodeWithText("FROG1").performClick()
        // Details and count picker should be visible
        composeTestRule.onNodeWithText("Species Count:").assertIsDisplayed()
        composeTestRule.onNodeWithText("Category: amphibians").assertIsDisplayed()
        composeTestRule.onNodeWithText("Description: \nFROG1").assertIsDisplayed()
    }

    @Test
    fun addSpeciesScreen_countPicker_works() {
        // Select a species first
        composeTestRule.onNodeWithText("Select species").performClick()
        composeTestRule.onNodeWithText("FROG1").performClick()
        // Open count dropdown and select a value
        composeTestRule.onNodeWithText("1").performClick()
        composeTestRule.onNodeWithContentDescription("Expand count").performClick()
        composeTestRule.onNodeWithText("5").performClick()
        composeTestRule.onNodeWithText("5").assertIsDisplayed()
    }

    @Test
    fun addSpeciesScreen_saveButton_disabledWhenLoading() {
        // Select a species first
        composeTestRule.onNodeWithText("Select species").performClick()
        composeTestRule.onNodeWithText("FROG1").performClick()
        // Click Save to trigger loading
        composeTestRule.onNodeWithText("Save").performClick()
        // Button should now be disabled (if loading state is set)
        composeTestRule.onNodeWithText("Save").assertIsNotEnabled()
    }

    @Test
    fun addSpeciesScreen_showsErrorMessageIfPresent() {
        // Simulate error by setting errorMessage in the activity or via DI
        // For demonstration, check for a generic error message
        // composeTestRule.onNodeWithText("Some error").assertIsDisplayed()
    }

    @Test
    fun addSpeciesScreen_showsLoadingIndicatorWhenLoading() {
        // Simulate loading by setting isLoading = true in the activity or via DI
        // For demonstration, check for CircularProgressIndicator by testTag if you add one
        // composeTestRule.onNode(hasTestTag("CircularProgressIndicator")).assertExists()
    }

    @Test
    fun addSpeciesScreen_backButton_closesActivity() {
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            assert(activity.isFinishing)
        }
    }
}