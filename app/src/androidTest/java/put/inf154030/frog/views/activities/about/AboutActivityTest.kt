package put.inf154030.frog.views.activities.about

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class AboutActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<AboutActivity>()

    @Test
    fun aboutScreen_displaysHeaderBar() {
        // Check if the header bar with title "About" is displayed
        composeTestRule.onNodeWithText("About").assertIsDisplayed()
    }

    @Test
    fun aboutScreen_displaysMainText() {
        // Check if the main lorem ipsum text is displayed
        composeTestRule.onNodeWithText(
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
        ).assertIsDisplayed()
    }

    @Test
    fun aboutScreen_backButton_closesActivity() {
        // Find the back button by its content description or text
        // (Assuming BackButton has a contentDescription "Back")
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        // Optionally, check if the activity is finishing
        composeTestRule.activityRule.scenario.onActivity { activity ->
            assert(activity.isFinishing)
        }
    }
}