package put.inf154030.frog.views.activities.login_pages

import androidx.compose.material3.CircularProgressIndicator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import put.inf154030.frog.R
import put.inf154030.frog.views.activities.locations.LocationsActivity
import put.inf154030.frog.models.requests.LoginRequest
import put.inf154030.frog.models.responses.AuthResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.network.SessionManager
import put.inf154030.frog.services.FrogFirebaseMessagingService
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.utils.dataStore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.flow.first
import put.inf154030.frog.models.requests.DeviceTokenRequest
import put.inf154030.frog.models.responses.MessageResponse

// Activity for user login
class LogInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrogTheme {
                // Main login screen composable
                LogInScreen(
                    onLoginSuccess = {
                        // Send FCM token after successful login
                        sendFcmTokenToServer()
                        // Navigate to main locations screen
                        val intent = Intent(this, LocationsActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }

    // Handles login logic and API call
    fun handleLogin(
        email: String,
        password: String,
        onResult: (success: Boolean, error: String?) -> Unit
    ) {
        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            onResult(false, "Please enter email and password")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onResult(false, "Please enter a valid email address")
            return
        }

        // Make login API request
        val loginRequest = LoginRequest(email = email, password = password)
        val call = ApiClient.apiService.loginUser(loginRequest)
        call.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { authResponse ->
                        SessionManager.saveAuthToken(authResponse.token)
                        authResponse.user.let { user ->
                            SessionManager.saveUserInfo(
                                user.id.toString(),
                                user.name,
                                user.email
                            )
                        }
                        onResult(true, null)
                    } ?: onResult(false, "Empty response received")
                } else {
                    onResult(false, response.errorBody()?.string() ?: "Login failed")
                }
            }
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                onResult(false, "Network error: Cannot connect to server")
            }
        })
        // Set a timeout to prevent indefinite waiting
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            if (isLoading) {
                call.cancel()
                isLoading = false
                errorMessage = "Connection timeout. Please try again."
            }
        }, 10000)
    }

    // Sends the FCM token to the server after login
    private fun sendFcmTokenToServer() {
        lifecycleScope.launch {
            val token = applicationContext.dataStore.data.first()[FrogFirebaseMessagingService.FCM_TOKEN_KEY]
            token?.let {
                try {
                    val tokenRequest = DeviceTokenRequest(deviceToken = it)

                    ApiClient.apiService.updateDeviceToken(tokenRequest).enqueue(object : Callback<MessageResponse> {
                        override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                            if (response.isSuccessful) {
                                Log.d("FCM", "Token sent to server successfully")
                            } else {
                                Log.e("FCM", "Failed to send token: ${response.errorBody()?.string()}")
                            }
                        }

                        override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                            Log.e("FCM", "Failed to send token to server", t)
                        }
                    })
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
    onLoginSuccess: () -> Unit,
) {
    val activity = LocalContext.current as LogInActivity
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    val emailValid = remember {
        derivedStateOf { email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches() }
    }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
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
                painter = painterResource(id = R.drawable.logo),
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
                // Email label and input
                Text(
                    text = "Email",
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
                Spacer(modifier = Modifier.size(16.dp))
                // Password label and input
                Text(
                    text = "Password",
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
                            isLoading = true
                            errorMessage = null
                            activity.handleLogin(email, password) { success, error ->
                                isLoading = false
                                if (success) {
                                    onLoginSuccess()
                                } else {
                                    errorMessage = error
                                }
                            }
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
                    text = "-- frogot password? --",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 16.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable { 
                            Toast.makeText(context, "Not implemented yet", Toast.LENGTH_SHORT).show()
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
                    onClick = {
                        isLoading = true
                        errorMessage = null
                        activity.handleLogin(email, password) { success, error ->
                            isLoading = false
                            if (success) {
                                onLoginSuccess()
                            } else {
                                errorMessage = error
                            }
                        }
                    },
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
                            text = "Log In",
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
            onLoginSuccess = {}
        )
    }
}