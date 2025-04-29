package put.inf154030.frog.account

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import put.inf154030.frog.fragments.BackButton
import put.inf154030.frog.fragments.TopHeaderBar
import put.inf154030.frog.network.SessionManager
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily

class EditAccountActivity : ComponentActivity() {
    private val userName = SessionManager.getUserName()
    private val userEmail = SessionManager.getUserEmail()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrogTheme {
                EditAccountScreen(
                    onBackClick = { finish() },
                    onSave = { name, email ->
                        TODO("Nie ma requesta na to")
                    },
                    userName = userName,
                    userEmail = userEmail
                )
            }
        }
    }
}

@Composable
fun EditAccountScreen (
    onBackClick: () -> Unit,
    onSave: (String, String) -> Unit,
    userName: String?,
    userEmail: String?
) {
    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(
                title = "Account"
            )
            BackButton { onBackClick() }
            Spacer(modifier = Modifier.size(64.dp))
            var name by remember { mutableStateOf(userName) }
            var email by remember { mutableStateOf(userEmail) }
            val emailValid = remember {
                derivedStateOf {
                    Patterns.EMAIL_ADDRESS.matcher(email!!).matches()
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BasicTextField(
                    value = name!!,
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
                            if (name!!.isEmpty()) {
                                Text(userName!!, fontFamily = PoppinsFamily)
                            }
                            innerTextField()
                        }
                    }
                )
                Spacer(modifier = Modifier.size(32.dp))
                BasicTextField(
                    value = email!!,
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
                            if (email!!.isEmpty()) {
                                Text(userEmail!!, fontFamily = PoppinsFamily)
                            }
                            innerTextField()
                        }
                    }
                )
                if (email!!.isNotEmpty() && !emailValid.value) {
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
            }
            Column (
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        onSave(name!!, email!!)
                    },
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