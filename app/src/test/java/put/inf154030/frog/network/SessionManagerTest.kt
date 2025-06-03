package put.inf154030.frog.network

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SessionManagerTest {

    private lateinit var mockSharedPreferences: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        mockSharedPreferences = mock(SharedPreferences::class.java)
        mockEditor = mock(SharedPreferences.Editor::class.java)
        mockContext = mock(Context::class.java)

        `when`(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPreferences)
        `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor)

        SessionManager.init(mockContext)
    }

    @Test
    fun `saveAuthToken stores token in SharedPreferences`() {
        // Given
        val token = "test_token_123"

        // When
        SessionManager.saveAuthToken(token)

        // Then
        verify(mockEditor).putString("user_token", token)
        verify(mockEditor).apply()
    }

    @Test
    fun `getToken returns null when token not present`() {
        // Given
        `when`(mockSharedPreferences.getString("user_token", null)).thenReturn(null)

        // When
        val result = SessionManager.getToken()

        // Then
        assertNull(result)
    }

    @Test
    fun `getToken returns token when present`() {
        // Given
        val token = "test_token_123"
        `when`(mockSharedPreferences.getString("user_token", null)).thenReturn(token)

        // When
        val result = SessionManager.getToken()

        // Then
        assertEquals(token, result)
    }

    @Test
    fun `saveUserInfo stores all user data correctly`() {
        // Given
        val userId = "123"
        val name = "John Doe"
        val email = "john@example.com"

        // When
        SessionManager.saveUserInfo(userId, name, email)

        // Then
        verify(mockEditor).putString("user_id", userId)
        verify(mockEditor).putString("user_name", name)
        verify(mockEditor).putString("user_email", email)
        verify(mockEditor).apply()
    }

    @Test
    fun `saveUpdatedUserInfo updates only name and email`() {
        // Given
        val name = "Jane Doe"
        val email = "jane@example.com"

        // When
        SessionManager.saveUpdatedUserInfo(name, email)

        // Then
        verify(mockEditor).putString("user_name", name)
        verify(mockEditor).putString("user_email", email)
        verify(mockEditor).apply()
    }

    @Test
    fun `getUserName and getUserEmail return correct values`() {
        // Given
        val name = "John Doe"
        val email = "john@example.com"
        `when`(mockSharedPreferences.getString("user_name", null)).thenReturn(name)
        `when`(mockSharedPreferences.getString("user_email", null)).thenReturn(email)

        // When & Then
        assertEquals(name, SessionManager.getUserName())
        assertEquals(email, SessionManager.getUserEmail())
    }

    @Test
    fun `init sets up correct SharedPreferences`() {
        // When
        val context = ApplicationProvider.getApplicationContext<Context>()
        SessionManager.init(context)

        // Then
        // This is more of an integration test - verifying init works with real context
        // No explicit assertion but this would fail if init doesn't handle the context properly
    }
}