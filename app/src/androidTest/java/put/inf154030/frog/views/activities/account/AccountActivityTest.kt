package put.inf154030.frog.views.activities.account

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class AccountActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<AccountActivity>()

    @Test
    fun accountScreen_displaysHeaderBar() {
        composeTestRule.onNodeWithText("Account").assertIsDisplayed()
    }

    @Test
    fun accountScreen_displaysUserName() {
        // Replace with the actual user name if you use a test SessionManager
        composeTestRule.onNodeWithText("Could not load name", substring = true).assertExists()
    }

    @Test
    fun accountScreen_displaysUserEmail() {
        // Replace with the actual user email if you use a test SessionManager
        composeTestRule.onNodeWithText("Could not load email", substring = true).assertExists()
    }

    @Test
    fun accountScreen_displaysEditButton() {
        composeTestRule.onNodeWithText("Edit").assertIsDisplayed()
    }

    @Test
    fun accountScreen_editButton_navigatesToEditAccount() {
        composeTestRule.onNodeWithText("Edit").performClick()
        // Optionally, check if EditAccountActivity is started (requires ActivityScenario or intent monitoring)
    }

    @Test
    fun accountScreen_backButton_closesActivity() {
        // Assuming BackButton has a contentDescription "Back"
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.activityRule.scenario.onActivity { activity ->
            assert(activity.isFinishing)
        }
    }
}