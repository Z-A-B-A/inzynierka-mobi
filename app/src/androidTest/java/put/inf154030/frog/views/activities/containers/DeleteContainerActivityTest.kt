package put.inf154030.frog.views.activities.containers

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import org.junit.Rule
import org.junit.Test

class DeleteContainerActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<DeleteContainerActivity>()

    @Test
    fun deleteContainerScreen_displaysHeaderBar() {
        composeTestRule.onNodeWithText("Delete Container").assertIsDisplayed()
    }

    @Test
    fun deleteContainerScreen_displaysConfirmationText() {
        composeTestRule.onNodeWithText("Are you sure?").assertIsDisplayed()
        composeTestRule.onNodeWithText("This operation can not be undone").assertIsDisplayed()
    }

    @Test
    fun deleteContainerScreen_displaysYesAndNoButtons() {
        composeTestRule.onNodeWithText("Yes").assertIsDisplayed()
        composeTestRule.onNodeWithText("No").assertIsDisplayed()
    }

    @Test
    fun deleteContainerScreen_noButton_closesActivity() {
        composeTestRule.onNodeWithText("No").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED
        }
        assert(composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED)
    }
}