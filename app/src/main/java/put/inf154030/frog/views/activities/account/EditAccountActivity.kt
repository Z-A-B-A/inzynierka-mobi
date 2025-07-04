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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.models.requests.UserUpdateRequest
import put.inf154030.frog.network.SessionManager
import put.inf154030.frog.repository.AccountRepository
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.TopHeaderBar

// Activity for editing user account information
class EditAccountActivity : ComponentActivity() {
    private var isLoading by mutableStateOf(false)
    private var errorMessage by mutableStateOf<String?>(null)
    private val userName = SessionManager.getUserName()
    private val userEmail = SessionManager.getUserEmail()
    private val accountRepository = AccountRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrogTheme {
                EditAccountScreen(
                    onBackClick = { finish() }, // Close activity on back
                    onSaveClick = { name, email ->
                        isLoading = true
                        errorMessage = null

                        val userUpdateRequest = UserUpdateRequest(name = name, email = email)
                        accountRepository.updateUser(
                            userUpdateRequest,
                            onResult = { success, error ->
                                isLoading = false
                                errorMessage = error
                                if (success) finish()
                            }
                        )
                    },
                    userName = userName,
                    userEmail = userEmail,
                    isLoading = isLoading,
                    errorMessage = errorMessage
                )
            }
        }
    }
}

// Composable for editing account info
@Composable
fun EditAccountScreen (
    onBackClick: () -> Unit,
    onSaveClick: (String, String) -> Unit, // Save callback
    userName: String?,
    userEmail: String?,
    isLoading: Boolean,
    errorMessage: String?
) {
    var name by remember { mutableStateOf(userName ?: "") }
    var email by remember { mutableStateOf(userEmail ?: "") }
    // Validate email format
    val emailValid = remember { derivedStateOf { Patterns.EMAIL_ADDRESS.matcher(email).matches() } }
    val canSave = name.isNotBlank() && email.isNotBlank() && emailValid.value

    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar( title = "Edytuj Profil" ) // Header bar
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
                    text = "Imię",
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
                Spacer(modifier = Modifier.size(8.dp))

                // Show email validation error
                if (email.isNotEmpty() && !emailValid.value) {
                    Text(
                        text = "Wprowadź prawidłowy adres email",
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .testTag("CircularProgressIndicator")
                    )
                }
                Button(
                    onClick = { onSaveClick(name, email) },
                    enabled = canSave && !isLoading,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(0.65f),
                ) {
                    Text(
                        text = "Zapisz",
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
            onSaveClick = { _, _ -> },
            userName = "Bartosz",
            userEmail = "bartoszkorszun@gmail.com",
            isLoading = false,
            errorMessage = null
        )
    }
}