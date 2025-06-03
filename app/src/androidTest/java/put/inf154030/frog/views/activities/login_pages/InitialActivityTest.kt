package put.inf154030.frog.views.activities.login_pages

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class InitialActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<InitialActivity>()

    @Test
    fun initialScreen_displaysLogo() {
        composeTestRule.onNodeWithContentDescription("App logo").assertIsDisplayed()
    }

    @Test
    fun initialScreen_displaysLoginAndSignUpButtons() {
        composeTestRule.onNodeWithText("Log In").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign Up").assertIsDisplayed()
    }

    @Test
    fun initialScreen_loginButton_triggersNavigation() {
        composeTestRule.onNodeWithText("Log In").performClick()
        // Optionally, check if LogInActivity is started (requires intent monitoring)
    }

    @Test
    fun initialScreen_signUpButton_triggersNavigation() {
        composeTestRule.onNodeWithText("Sign Up").performClick()
        // Optionally, check if SignUpActivity is started (requires intent monitoring)
    }
}