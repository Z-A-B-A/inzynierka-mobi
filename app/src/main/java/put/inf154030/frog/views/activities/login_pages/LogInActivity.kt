package put.inf154030.frog.views.activities.login_pages

import android.content.Intent
import android.os.Bundle
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
import put.inf154030.frog.R
import put.inf154030.frog.views.activities.locations.LocationsActivity
import put.inf154030.frog.models.requests.LoginRequest
import put.inf154030.frog.models.responses.AuthResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.network.SessionManager
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrogTheme {
                LogInScreen(
                    onLoginSuccess = {
                        val intent = Intent(this, LocationsActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun LogInScreen(
    onLoginSuccess: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(128.dp))
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
                var email by remember { mutableStateOf("") }
                val emailValid = remember {
                    derivedStateOf { email.isEmpty() || Patterns.EMAIL_ADDRESS.matcher(email).matches() }
                }
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
                var password by remember { mutableStateOf("") }
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
                        fontSize = 16.sp
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            innerTextField()
                        }
                    }
                )
                Spacer(modifier = Modifier.size(32.dp))
                Text(
                    text = "-- frogot password? --",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 16.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable { TODO("Usługa jeszcze nie zaimplementowana emoji żaby") }
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.size(32.dp))
                var isLoading by remember { mutableStateOf(false) }
                var errorMessage by remember { mutableStateOf<String?>(null) }
                Button(
                    onClick = {
                        if (email.isNotEmpty() && password.isNotEmpty() && emailValid.value) {
                            isLoading = true
                            errorMessage = null

                            val loginRequest = LoginRequest(
                                email = email,
                                password = password
                            )

                            ApiClient.apiService.loginUser(loginRequest).enqueue(object :
                                Callback<AuthResponse> {
                                override fun onResponse(
                                    call: Call<AuthResponse>,
                                    response: Response<AuthResponse>
                                ) {
                                    isLoading = false
                                    if (response.isSuccessful) {
                                        response.body()?.let { authResponse ->
                                            // Save token in SessionManager - updated to use the singleton object
                                            SessionManager.saveAuthToken(authResponse.token)

                                            // Optionally, save user info if needed
                                            authResponse.user.let { user ->
                                                SessionManager.saveUserInfo(
                                                    user.id.toString(),
                                                    user.name,
                                                    user.email
                                                )
                                            }

                                            onLoginSuccess()

                                        } ?: run {
                                            errorMessage = "Empty response received"
                                        }
                                    } else {
                                        errorMessage = try {
                                            response.errorBody()?.string() ?: "Login failed"
                                        } catch (e: Exception) {
                                            "Login failed: ${e.message}"
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                                    isLoading = false
                                    errorMessage = "Network error: ${t.message}"
                                }
                            })
                        } else if (!emailValid.value) {
                            errorMessage = "Please enter a valid email address"
                        } else {
                            errorMessage = "Please enter email and password"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.65f),
                    enabled = !isLoading
                ) {
                    Text(
                        text = if (isLoading) "Logging in..." else "Log In",
                        fontFamily = PoppinsFamily
                    )
                }
                Spacer(modifier = Modifier.size(64.dp))
            }
        }
    }
}

@Preview
@Composable
fun LogInActivityPreview () {
    FrogTheme {
        LogInScreen(
            onLoginSuccess = {}
        )
    }
}