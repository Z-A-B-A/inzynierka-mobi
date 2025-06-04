package put.inf154030.frog.views.activities.login_pages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import put.inf154030.frog.R
import put.inf154030.frog.models.requests.DeviceTokenRequest
import put.inf154030.frog.models.requests.LoginRequest
import put.inf154030.frog.repository.AccountRepository
import put.inf154030.frog.repository.NotificationsRepository
import put.inf154030.frog.services.FrogFirebaseMessagingService
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.utils.dataStore
import put.inf154030.frog.views.activities.locations.LocationsActivity

// Activity for user login
class LogInActivity : FragmentActivity() {
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)
    private val accountRepository = AccountRepository()
    private val notificationsRepository = NotificationsRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If opened from notification create a chain to get to notifications activity
        val fromNotification = intent.getBooleanExtra("FROM_NOTIFICATION", false)
        if (fromNotification) {
            val intent = Intent(this, LocationsActivity::class.java)
            intent.putExtra("FROM_NOTIFICATION", true)
            startActivity(intent)
        }

        // Check if user has stored credentials
        val storedCredentials = getCredentials(this)

        setContent {
            FrogTheme {
                // Main login screen composable
                LogInScreen(
                    onLoginClick = { email, password ->
                        handleLogin(
                            email,
                            password,
                            onSuccess = { success ->
                                // Avoid starting multiple activities
                                if (success && !isLoading) {
                                    // Send FCM token after successful login
                                    sendFcmTokenToServer()
                                    // Navigate to main locations screen
                                    val intent = Intent(this, LocationsActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        )
                    },
                    onBiometricClick = { showBiometricPrompt() },
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    hasStoredCredentials = storedCredentials != null
                )
            }
        }
    }

    // Store credentials after first login
    private fun saveCredentials(context: Context, email: String, password: String) {
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = EncryptedSharedPreferences.create(
            "secret_shared_prefs",
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        sharedPreferences.edit()
            .putString("email", email)
            .putString("password", password)
            .apply()
    }

    // Retrieve credentials for biometric login
    fun getCredentials(context: Context): Pair<String, String>? {
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPreferences = EncryptedSharedPreferences.create(
            "secret_shared_prefs",
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val email = sharedPreferences.getString("email", null)
        val password = sharedPreferences.getString("password", null)
        return if (email != null && password != null) Pair(email, password) else null
    }

    // Biometric
    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    // Get stored credentials
                    val credentials = getCredentials(this@LogInActivity)
                    if (credentials != null) {
                        val (storedEmail, storedPassword) = credentials
                        // Login with stored credentials
                        handleLogin(
                            storedEmail,
                            storedPassword,
                            onSuccess = { success ->
                                if (success && !isLoading) {
                                    // Send FCM token after successful login
                                    sendFcmTokenToServer()
                                    // Navigate to main locations screen
                                    val intent =
                                        Intent(this@LogInActivity, LocationsActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        )
                    }  else {
                        errorMessage = "Brak danych do logowania"
                    }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    errorMessage = "Błąd uwierzytelniania: $errString"
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    errorMessage = "Uwierzytelnienie nie powiodło się"
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Logowanie Biometryczne")
            .setSubtitle("Zaloguj za pomocą biometrii")
            .setNegativeButtonText("Anuluj")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    // Handles login logic and API call
    private fun handleLogin(
        email: String,
        password: String,
        onSuccess: (Boolean) -> Unit
    ) {
        isLoading = true
        errorMessage = null

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            isLoading = false
            errorMessage = "Wprowadź dane logowania"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isLoading = false
            errorMessage = "Wprowadź prawidlowy adres e-mail"
            return
        }

        // Make login API request
        val loginRequest = LoginRequest(email = email, password = password)
        accountRepository.loginUser(
            loginRequest,
            onResult = { success, error ->
                isLoading = false
                errorMessage = error
                if (success) saveCredentials(this, email, password)
                onSuccess(success)
            }
        )
    }

    // Sends the FCM token to the server after login
    private fun sendFcmTokenToServer() {
        lifecycleScope.launch {
            val token = applicationContext.dataStore.data.first()[FrogFirebaseMessagingService.FCM_TOKEN_KEY]
            token?.let {
                try {
                    val tokenRequest = DeviceTokenRequest(deviceToken = it)
                    notificationsRepository.updateDeviceToken(
                        tokenRequest,
                        onResult = { error ->
                            isLoading = false
                            errorMessage = error
                        }
                    )
                } catch (e: Exception) {
                    Log.e("FCM", "Error setting up token request", e)
                }
            }
        }
    }
}

// Composable for the login screen UI
@Composable
fun LogInScreen(
    onLoginClick: (String, String) -> Unit,
    onBiometricClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    hasStoredCredentials: Boolean
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    val emailValid = remember {
        derivedStateOf { email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches() }
    }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(128.dp))
            // App logo
            Image(
                painter = painterResource(id = R.drawable.frog_logo),
                contentDescription = "App logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(128.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                // Show biometric button if credentials are stored
                if (hasStoredCredentials) {
                    IconButton(
                        onClick = { onBiometricClick() },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_fingerprint), // Make sure you have this icon
                            contentDescription = "Use biometric login",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "Zaloguj za pomocą biometrii",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                // Email label and input
                Text(
                    text = "Adres e-mail",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, bottom = 4.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
                BasicTextField(
                    value = email,
                    onValueChange = { newValue -> email = newValue },
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 16.sp
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            innerTextField()
                        }
                    }
                )
                // Error message if email is not valid
                if (!emailValid.value && email.isNotEmpty()) {
                    Text(
                        text = "Nieprawidłowy adres email",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        fontFamily = PoppinsFamily,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.size(16.dp))
                // Password label and input
                Text(
                    text = "Hasło",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, bottom = 4.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
                BasicTextField(
                    value = password,
                    onValueChange = { newValue -> password = newValue },
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { 
                            // Trigger login when "Done" is pressed on keyboard
                            onLoginClick(email, password)
                        }
                    ),
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 16.sp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(Modifier.weight(1f)) { innerTextField() }
                                // Toggle password visibility
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        painter = painterResource(
                                            if (passwordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                                        ),
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                    )
                                }
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.size(32.dp))
                // "Frogot password" clickable text
                Text(
                    text = "-- żabomniałeś/aś hasła? --",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 16.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable { 
                            Toast.makeText(context, "Jeszcze nie zaimplementowane", Toast.LENGTH_SHORT).show()
                         }
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.size(32.dp))

                // Error message display
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        fontFamily = PoppinsFamily,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Login button with loading indicator
                Button(
                    onClick = { onLoginClick(email, password) },
                    modifier = Modifier.fillMaxWidth(0.65f),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Zaloguj",
                            fontFamily = PoppinsFamily
                        )
                    }
                }
                Spacer(modifier = Modifier.size(64.dp))
            }
        }
    }
}

// Preview for Compose UI
@Preview
@Composable
fun LogInActivityPreview () {
    FrogTheme {
        LogInScreen(
            onLoginClick = { _ ,_ -> },
            onBiometricClick = {},
            isLoading = false,
            errorMessage = null,
            hasStoredCredentials = true
        )
    }
}