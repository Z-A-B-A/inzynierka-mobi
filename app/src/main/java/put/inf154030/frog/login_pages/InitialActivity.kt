package put.inf154030.frog.login_pages

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import put.inf154030.frog.R
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily

class InitialActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrogTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "App logo",
                            modifier = Modifier
                                .fillMaxSize(0.75f)
                        )
                        Button(
                            onClick = {
                                val intent = Intent(this@InitialActivity, LogInActivity::class.java)
                                startActivity(intent)
                            },
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth(0.65f),
                        ) {
                            Text(
                                text = "Log In",
                                fontFamily = PoppinsFamily
                            )
                        }
                        Button(
                            onClick = {
                                val intent = Intent(this@InitialActivity, SignUpActivity::class.java)
                                startActivity(intent)
                            },
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth(0.65f),
                        ) {
                            Text(
                                text = "Sign Up",
                                fontFamily = PoppinsFamily
                            )
                        }
                    }
                }
            }
        }
    }
}