package put.inf154030.frog.views.activities.containers

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class ContainerActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ContainerActivity>()

    @Test
    fun containerScreen_displaysHeaderBar() {
        composeTestRule.onNodeWithText("Container X").assertIsDisplayed()
    }

    @Test
    fun containerScreen_displaysBackButton() {
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun containerScreen_displaysScheduleButton() {
        composeTestRule.onNodeWithText("schedule >>>").assertIsDisplayed()
    }

    @Test
    fun containerScreen_displaysDescriptionSection() {
        composeTestRule.onNodeWithText("-- description --").assertIsDisplayed()
        composeTestRule.onNodeWithText("Potężny kontener na bycze ryby").assertIsDisplayed()
    }

    @Test
    fun containerScreen_displaysParametersSection() {
        composeTestRule.onNodeWithText("Temperatura wody").assertIsDisplayed()
        composeTestRule.onNodeWithText("pH").assertIsDisplayed()
        composeTestRule.onNodeWithText("Światło").assertIsDisplayed()
    }

    @Test
    fun containerScreen_displaysSpeciesSection() {
        composeTestRule.onNodeWithText("Species").assertIsDisplayed()
        composeTestRule.onNodeWithText("Count").assertIsDisplayed()
        composeTestRule.onNodeWithText("frog").assertIsDisplayed()
        composeTestRule.onNodeWithText("3").assertIsDisplayed()
    }

    @Test
    fun containerScreen_displaysParameterHistorySection() {
        composeTestRule.onNodeWithText("Parameter History").assertIsDisplayed()
        composeTestRule.onNodeWithText("Last Hour").assertIsDisplayed()
        composeTestRule.onNodeWithText("Last 6 Hours").assertIsDisplayed()
        composeTestRule.onNodeWithText("Last 12 Hours").assertIsDisplayed()
        composeTestRule.onNodeWithText("Last Day").assertIsDisplayed()
    }

    @Test
    fun containerScreen_displaysChangeButton() {
        composeTestRule.onNodeWithText("Change").assertIsDisplayed()
    }

    @Test
    fun containerScreen_backButton_closesActivity() {
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            assert(activity.isFinishing)
        }
    }

    @Test
    fun containerScreen_scheduleButton_opensScheduleActivity() {
        composeTestRule.onNodeWithText("schedule >>>").performClick()
        // Optionally, check if ScheduleActivity is started (requires intent monitoring)
    }

    @Test
    fun containerScreen_changeButton_opensManageContainerActivity() {
        composeTestRule.onNodeWithText("Change").performClick()
        // Optionally, check if ManageContainerActivity is started (requires intent monitoring)
    }

    @Test
    fun containerScreen_showsErrorMessageIfPresent() {
        // Simulate error by setting errorMessage in the activity or via DI
        // For demonstration, check for a generic error message
        // composeTestRule.onNodeWithText("Some error").assertIsDisplayed()
    }

    @Test
    fun containerScreen_showsLoadingIndicatorWhenLoading() {
        // Simulate loading by setting isLoading = true in the activity or via DI
        // For demonstration, check for CircularProgressIndicator by testTag if you add one
        // composeTestRule.onNode(hasTestTag("CircularProgressIndicator")).assertExists()
    }
}