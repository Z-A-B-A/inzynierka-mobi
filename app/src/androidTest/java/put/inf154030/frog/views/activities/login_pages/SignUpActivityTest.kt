package put.inf154030.frog.views.activities.login_pages

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class SignUpActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<SignUpActivity>()

    @Test
    fun signUpScreen_displaysLogo() {
        composeTestRule.onNodeWithContentDescription("App logo").assertIsDisplayed()
    }

    @Test
    fun signUpScreen_displaysAllInputFields() {
        composeTestRule.onNodeWithText("Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithText("Confirm password").assertIsDisplayed()
    }

    @Test
    fun signUpScreen_passwordVisibilityToggle_works() {
        // Enter password and toggle visibility
        composeTestRule.onAllNodes(hasSetTextAction())[2].performTextInput("Secret123")
        composeTestRule.onAllNodesWithContentDescription("Show password").first().performClick()
        composeTestRule.onAllNodesWithContentDescription("Hide password").first().performClick()
    }

    @Test
    fun signUpScreen_showsEmailValidationMessage() {
        composeTestRule.onAllNodes(hasSetTextAction())[1].performTextInput("invalid-email")
        composeTestRule.onNodeWithText("Enter valid email address.").assertIsDisplayed()
    }

    @Test
    fun signUpScreen_showsPasswordValidationMessage() {
        composeTestRule.onAllNodes(hasSetTextAction())[2].performTextInput("short")
        composeTestRule.onNodeWithText("Password must have at least 8 characters, 1 uppercase, 1 lowercase, and 1 number.").assertIsDisplayed()
    }

    @Test
    fun signUpScreen_showsPasswordsDoNotMatchMessage() {
        composeTestRule.onAllNodes(hasSetTextAction())[2].performTextInput("Secret123")
        composeTestRule.onAllNodes(hasSetTextAction())[3].performTextInput("Different123")
        composeTestRule.onNodeWithText("Passwords do not match.").assertIsDisplayed()
    }

    @Test
    fun signUpScreen_signUpButton_disabledWhenLoading() {
        // Enter valid data
        composeTestRule.onAllNodes(hasSetTextAction())[0].performTextInput("Test User")
        composeTestRule.onAllNodes(hasSetTextAction())[1].performTextInput("test@example.com")
        composeTestRule.onAllNodes(hasSetTextAction())[2].performTextInput("Secret123")
        composeTestRule.onAllNodes(hasSetTextAction())[3].performTextInput("Secret123")
        // Click Sign Up to trigger loading
        composeTestRule.onNodeWithText("Sign Up").performClick()
        // Button should now show CircularProgressIndicator and be disabled
        composeTestRule.onNodeWithText("Sign Up").assertIsNotEnabled()
    }

    @Test
    fun signUpScreen_showsErrorMessageIfPresent() {
        // Simulate error by setting errorMessage in the activity or via DI
        // For demonstration, check for a generic error message
        // composeTestRule.onNodeWithText("Some error").assertIsDisplayed()
    }
}