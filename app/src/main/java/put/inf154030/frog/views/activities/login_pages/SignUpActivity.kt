package put.inf154030.frog.views.activities.login_pages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.R
import put.inf154030.frog.models.requests.RegisterRequest
import put.inf154030.frog.models.responses.RegisterResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Activity for user registration
class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrogTheme {
                // Main sign-up screen composable
                SignUpScreen(
                    onSignUpSuccess = {
                        // Navigate to login screen after successful registration
                        val intent = Intent(this, LogInActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }

    // Handles registration API call
    fun handleSignUp(
        name: String,
        email: String,
        password: String,
        onResult: (success: Boolean, error: String?) -> Unit
    ) {
        val registerRequest = RegisterRequest(name, email, password)
        val call = ApiClient.apiService.registerUser(registerRequest)
        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, response.errorBody()?.string() ?: "Registration failed")
                }
            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                onResult(false, "Network error: Cannot connect to server")
            }
        })
        // Timeout logic
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            onResult(false, "Connection timeout. Please try again.")
            call.cancel()
        }, 10000)
    }
}

// Composable for the sign-up screen UI
@Composable
fun SignUpScreen (
    onSignUpSuccess: () -> Unit
) {
    val activity = LocalContext.current as SignUpActivity
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val emailValid = remember {
        derivedStateOf {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }
    var password by remember { mutableStateOf("") }
    val passwordValid = remember {
        derivedStateOf {
            password.length >= 8
                    && password.any { it.isUpperCase() }
                    && password.any { it.isLowerCase() }
                    && password.any { it.isDigit() }
        }
    }
    var passwordConfirmation by remember { mutableStateOf("") }
    val passwordsMatch = remember {
        derivedStateOf { password == passwordConfirmation }
    }

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
                    .fillMaxWidth(0.75f)
                    .height(128.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                // Name input
                Text(
                    text = "Name",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, bottom = 4.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
                BasicTextField(
                    value = name,
                    onValueChange = { newValue -> name = newValue },
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
                // Email input
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
                // Email validation message
                if (email.isNotEmpty() && !emailValid.value) {
                    Text(
                        text = "Enter valid email address.",
                        color = Color.Red,
                        fontSize = 14.sp,
                        fontFamily = PoppinsFamily,
                        modifier = Modifier
                            .fillMaxWidth(0.65f),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.size(16.dp))
                // Password input with show/hide toggle
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
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = PoppinsFamily
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
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
                // Password validation message
                if (password.isNotEmpty() && !passwordValid.value) {
                    Text(
                        text = "Password must have at least 8 characters, 1 uppercase, 1 lowercase, and 1 number.",
                        color = Color.Red,
                        fontSize = 14.sp,
                        fontFamily = PoppinsFamily,
                        modifier = Modifier
                            .fillMaxWidth(0.65f),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.size(16.dp))
                // Confirm password input with show/hide toggle
                Text(
                    text = "Confirm password",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, bottom = 4.dp),
                    color = MaterialTheme.colorScheme.secondary
                )
                BasicTextField(
                    value = passwordConfirmation,
                    onValueChange = { newValue -> passwordConfirmation = newValue },
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = PoppinsFamily
                    ),
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // Trigger sign up on keyboard "Done"
                            if (allFieldsValid) {
                                isLoading = true
                                errorMessage = null
                                activity.handleSignUp(name, email, password) { success, error ->
                                    isLoading = false
                                    if (success) {
                                        Toast.makeText(context, "Registration successful! Please login.", Toast.LENGTH_LONG).show()
                                        onSignUpSuccess()
                                    } else {
                                        errorMessage = error
                                    }
                                }
                            }
                        }
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.weight(1f)) { innerTextField() }
                                // Toggle confirm password visibility
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        painter = painterResource(
                                            if (confirmPasswordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                                        ),
                                        contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                                    )
                                }
                            }
                        }
                    }
                )
                // Passwords match validation message
                if (passwordConfirmation.isNotEmpty() && !passwordsMatch.value) {
                    Text(
                        text = "Passwords do not match.",
                        color = Color.Red,
                        fontSize = 14.sp,
                        fontFamily = PoppinsFamily,
                        modifier = Modifier
                            .fillMaxWidth(0.65f),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.size(64.dp))
                // Sign Up button
                Button(
                    onClick = {
                        isLoading = true
                        errorMessage = null
                        activity.handleSignUp(name, email, password) { success, error ->
                            isLoading = false
                            if (success) {
                                Toast.makeText(context, "Registration successful! Please login.", Toast.LENGTH_LONG).show()
                                onSignUpSuccess()
                            } else {
                                errorMessage = error
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.65f),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Sign Up",
                            fontFamily = PoppinsFamily
                        )
                    }
                }
                // Error message display
                errorMessage?.let { error ->
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 14.sp,
                        fontFamily = PoppinsFamily,
                        modifier = Modifier.fillMaxWidth(0.65f),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.size(64.dp))
            }
        }
    }
}

// Preview for Compose UI
@Preview
@Composable
fun SignUpActivityPreview () {
    FrogTheme {
        SignUpScreen(
            onSignUpSuccess = {}
        )
    }
}