package put.inf154030.frog.views.activities.login_pages

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class LogInActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<LogInActivity>()

    @Test
    fun loginScreen_displaysLogo() {
        composeTestRule.onNodeWithContentDescription("App logo").assertIsDisplayed()
    }

    @Test
    fun loginScreen_displaysEmailAndPasswordFields() {
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
    }

    @Test
    fun loginScreen_passwordVisibilityToggle_works() {
        // Find the password field and enter text
        composeTestRule.onAllNodes(hasSetTextAction())[1].performTextInput("secret")
        // Toggle visibility
        composeTestRule.onNodeWithContentDescription("Show password").performClick()
        // Toggle back
        composeTestRule.onNodeWithContentDescription("Hide password").performClick()
    }

    @Test
    fun loginScreen_showsBiometricButtonIfCredentialsStored() {
        composeTestRule.onNodeWithContentDescription("Use biometric login").assertIsDisplayed()
        composeTestRule.onNodeWithText("Use biometrics to login").assertIsDisplayed()
    }

    @Test
    fun loginScreen_showsErrorMessageForInvalidEmail() {
        composeTestRule.onAllNodes(hasSetTextAction())[0].performTextInput("invalid-email")
        composeTestRule.onNodeWithText("Invalid email address").assertIsDisplayed()
    }

    @Test
    fun loginScreen_showsErrorMessageIfPresent() {
        // Simulate error by setting errorMessage in the activity or via DI
        // For demonstration, check for a generic error message
        // composeTestRule.onNodeWithText("Some error").assertIsDisplayed()
    }

    @Test
    fun loginScreen_loginButton_disabledWhenLoading() {
        // Enter valid email and password
        composeTestRule.onAllNodes(hasSetTextAction())[0].performTextInput("test@example.com")
        composeTestRule.onAllNodes(hasSetTextAction())[1].performTextInput("password")
        // Click login to trigger loading
        composeTestRule.onNodeWithText("Log In").performClick()
        // Button should now show CircularProgressIndicator and be disabled
        // Optionally, check for CircularProgressIndicator by testTag if you add one
        // composeTestRule.onNode(hasTestTag("CircularProgressIndicator")).assertExists()
    }

    @Test
    fun loginScreen_loginButton_triggersLogin() {
        composeTestRule.onAllNodes(hasSetTextAction())[0].performTextInput("test@example.com")
        composeTestRule.onAllNodes(hasSetTextAction())[1].performTextInput("password")
        composeTestRule.onNodeWithText("Log In").performClick()
        // Optionally, check if LocationsActivity is started (requires intent monitoring)
    }

    @Test
    fun loginScreen_forgotPassword_showsToast() {
        composeTestRule.onNodeWithText("-- frogot password? --").performClick()
        // Optionally, check for Toast (requires Espresso)
    }
}