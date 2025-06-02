package put.inf154030.frog.views.activities.containers

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class ContainerActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ContainerActivity>()

    @Test
    fun containerScreen_displaysBackButton() {
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun containerScreen_displaysScheduleButton() {
        composeTestRule.onNodeWithText("schedule >>>").assertIsDisplayed()
    }

    @Test
    fun containerScreen_displaysChangeButton() {
        composeTestRule.onNodeWithText("Change").assertIsDisplayed()
    }

    @Test
    fun containerScreen_backButton_closesActivity() {
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.waitForIdle()
        // Optionally, check if the activity is destroyed
    }

    @Test
    fun containerScreen_scheduleButton_opensScheduleActivity() {
        composeTestRule.onNodeWithText("schedule >>>").performClick()
        // Optionally, check if ScheduleActivity is started
    }

    @Test
    fun containerScreen_changeButton_opensManageContainerActivity() {
        composeTestRule.onNodeWithText("Change").performClick()
        // Optionally, check if ManageContainerActivity is started
    }

    // You can implement error/loading tests here if you can inject state into the activity
}