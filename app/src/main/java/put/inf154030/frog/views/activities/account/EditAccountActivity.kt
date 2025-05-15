package put.inf154030.frog.views.activities.account

import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.models.requests.UserUpdateRequest
import put.inf154030.frog.models.responses.UserResponse
import put.inf154030.frog.network.ApiClient
import put.inf154030.frog.network.SessionManager
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.TopHeaderBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Activity for editing user account information
class EditAccountActivity : ComponentActivity() {
    private val userName = SessionManager.getUserName()
    private val userEmail = SessionManager.getUserEmail()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrogTheme {
                EditAccountScreen(
                    onBackClick = { finish() }, // Close activity on back
                    onSaveClick = { name, email, onResult ->
                        // Make API call to update user info
                        val userUpdateRequest = UserUpdateRequest(name = name, email = email)

                        ApiClient.apiService.updateUser(userUpdateRequest).enqueue(object : Callback<UserResponse> {
                            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                                if (response.isSuccessful) {
                                    // Update session information
                                    SessionManager.saveUpdatedUserInfo(name, email)
                                    onResult(null)
                                    finish()
                                } else {
                                    onResult("Failed to update user information.")
                                }
                            }

                            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                                onResult("Network error: ${t.message}")
                            }
                        })
                    },
                    userName = userName,
                    userEmail = userEmail
                )
            }
        }
    }
}

// Composable for editing account info
@Composable
fun EditAccountScreen (
    onBackClick: () -> Unit,
    onSaveClick: (String, String, (String?) -> Unit) -> Unit, // Save callback
    userName: String?,
    userEmail: String?
) {
    var name by remember { mutableStateOf(userName ?: "") }
    var email by remember { mutableStateOf(userEmail ?: "") }
    // Validate email format
    val emailValid = remember { derivedStateOf { Patterns.EMAIL_ADDRESS.matcher(email).matches() } }
    val canSave = name.isNotBlank() && email.isNotBlank() && emailValid.value

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar( title = "Account" ) // Header bar
            BackButton { onBackClick() } // Back button
            Spacer(modifier = Modifier.size(64.dp))
            
            // Input fields and error messages
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Name label and input
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
                Spacer(modifier = Modifier.size(32.dp))

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
                Spacer(modifier = Modifier.size(8.dp))

                // Show email validation error
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

                // Show general error message
                errorMessage?.let {
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = it,
                        color = Color.Red,
                        fontSize = 14.sp,
                        fontFamily = PoppinsFamily,
                        modifier = Modifier.fillMaxWidth(0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Save button and loading indicator at the bottom
            Column (
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
                Button(
                    onClick = {
                        isLoading = true
                        errorMessage = null
                        onSaveClick(name, email) { error ->
                            isLoading = false
                            errorMessage = error
                        }
                    },
                    enabled = canSave && !isLoading,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(0.65f),
                ) {
                    Text(
                        text = "Save",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.size(64.dp))
            }
        }
    }
}

// Preview for EditAccountScreen
@Preview
@Composable
fun EditAccountActivityPreview () {
    FrogTheme {
        EditAccountScreen(
            onBackClick = {},
            onSaveClick = { _, _, _ -> },
            userName = "Bartosz",
            userEmail = "bartoszkorszun@gmail.com"
        )
    }
}