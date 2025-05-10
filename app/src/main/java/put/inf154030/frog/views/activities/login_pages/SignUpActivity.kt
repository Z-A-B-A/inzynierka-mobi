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

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrogTheme {
                SignUpScreen(
                    onSignUpSuccess = {
                        val intent = Intent(this, LogInActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    context = this
                )
            }
        }
    }
}

@Composable
fun SignUpScreen (
    onSignUpSuccess: () -> Unit,
    context: Context
) {
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(64.dp))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App logo",
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(128.dp)
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                var name by remember { mutableStateOf("") }
                BasicTextField(
                    value = name,
                    onValueChange = { newValue -> name = newValue },
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
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
                            if (name.isEmpty()) {
                                Text("Name", fontFamily = PoppinsFamily)
                            }
                            innerTextField()
                        }
                    }
                )
                Spacer(modifier = Modifier.size(16.dp))
                var email by remember { mutableStateOf("") }
                val emailValid = remember {
                    derivedStateOf {
                        Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    }
                }
                BasicTextField(
                    value = email,
                    onValueChange = { newValue -> email = newValue },
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
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
                            if (email.isEmpty()) {
                                Text("E-mail", fontFamily = PoppinsFamily)
                            }
                            innerTextField()
                        }
                    }
                )
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
                var password by remember { mutableStateOf("") }
                val passwordValid = remember {
                    derivedStateOf {
                        password.length >= 8
                                && password.any { it.isUpperCase() }
                                && password.any { it.isLowerCase() }
                                && password.any { it.isDigit() }
                    }
                }
                BasicTextField(
                    value = password,
                    onValueChange = { newValue -> password = newValue },
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
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
                    visualTransformation = PasswordVisualTransformation(),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (password.isEmpty()) {
                                Text("Password", fontFamily = PoppinsFamily)
                            }
                            innerTextField()
                        }
                    }
                )
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
                var passwordConfirmation by remember { mutableStateOf("") }
                val passwordsMatch = remember {
                    derivedStateOf { password == passwordConfirmation }
                }
                BasicTextField(
                    value = passwordConfirmation,
                    onValueChange = { newValue -> passwordConfirmation = newValue },
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
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
                    visualTransformation = PasswordVisualTransformation(),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (passwordConfirmation.isEmpty()) {
                                Text("Confirm password", fontFamily = PoppinsFamily)
                            }
                            innerTextField()
                        }
                    }
                )
                if (passwordConfirmation.isNotEmpty() && !passwordsMatch.value) {
                    Text(
                        text = "Password do not match.",
                        color = Color.Red,
                        fontSize = 14.sp,
                        fontFamily = PoppinsFamily,
                        modifier = Modifier
                            .fillMaxWidth(0.65f),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.size(64.dp))
                Button(
                    onClick = {
                        if (
                            name.isNotEmpty()
                            && email.isNotEmpty()
                            && emailValid.value
                            && password.isNotEmpty()
                            && passwordValid.value
                            && passwordsMatch.value
                        ){
                            isLoading = true
                            errorMessage = null

                            val registerRequest = RegisterRequest(
                                name = name,
                                email = email,
                                password = password
                            )

                            ApiClient.apiService.registerUser(registerRequest).enqueue(object : Callback<RegisterResponse> {
                                override fun onResponse(
                                    call: Call<RegisterResponse>,
                                    response: Response<RegisterResponse>
                                ) {
                                    isLoading = false
                                    if (response.isSuccessful) {
                                        Toast.makeText(
                                            context,
                                            "Registration successful! Please login.",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        onSignUpSuccess()

                                    } else {
                                        errorMessage = try {
                                            response.errorBody()?.string() ?: "Registration failed"
                                        } catch (e: Exception) {
                                            "Registration failed: ${e.message}"
                                        }
                                    }
                                }

                                override fun onFailure(
                                    call: Call<RegisterResponse>,
                                    t: Throwable
                                ) {
                                    isLoading = false
                                    errorMessage = "Network error: ${t.message}"
                                }

                            })
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.65f),
                    enabled = !isLoading
                ) {
                    Text(
                        text = if (isLoading) "Registering..." else "Sign Up",
                        fontFamily = PoppinsFamily
                    )
                }
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

@Preview
@Composable
fun SignUpActivityPreview () {
    val context = androidx.compose.ui.platform.LocalContext.current
    FrogTheme {
        SignUpScreen(
            onSignUpSuccess = {},
            context = context
        )
    }
}