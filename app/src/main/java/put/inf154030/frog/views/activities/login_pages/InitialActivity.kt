package put.inf154030.frog.views.activities.login_pages

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import put.inf154030.frog.R
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily

// Entry activity for the app, shows login/signup options
class InitialActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FrogTheme {
                // Main screen composable for initial options
                InitialScreen(
                    onLoginClick = {
                        // Navigate to Log In screen
                        val intent = Intent(this, LogInActivity::class.java)
                        startActivity(intent)
                    },
                    onSignUpClick = {
                        // Navigate to Sign Up screen
                        val intent = Intent(this, SignUpActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

// Composable for the initial login/signup screen
@Composable
fun InitialScreen (
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Centered logo at the top
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App logo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp)
                )
            }
            // Log In button
            Button(
                onClick = { onLoginClick() },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(0.65f),
            ) {
                Text(
                    text = "Log In",
                    fontFamily = PoppinsFamily
                )
            }
            // Sign Up button
            Button(
                onClick = { onSignUpClick() },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(0.65f),
            ) {
                Text(
                    text = "Sign Up",
                    fontFamily = PoppinsFamily
                )
            }
            Spacer(modifier = Modifier.size(64.dp))
        }
    }
}

// Preview for Compose UI
@Preview
@Composable
fun InitialActivityPreview () {
    FrogTheme {
        InitialScreen(
            onLoginClick = {},
            onSignUpClick = {}
        )
    }
}