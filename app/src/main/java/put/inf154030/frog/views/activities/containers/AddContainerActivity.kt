package put.inf154030.frog.views.activities.containers

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily
import put.inf154030.frog.utils.QrCodeAnalyzer
import put.inf154030.frog.views.fragments.BackButton
import put.inf154030.frog.views.fragments.TopHeaderBar

class AddContainerActivity : AppCompatActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, proceed with camera
        } else {
            Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_LONG).show()
        }
    }

    private var locationId: Int = -1
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationId = intent.getIntExtra("LOCATION_ID", -1)

        setContent {
            FrogTheme {
                AddContainerScreen(
                    onBackClick = { finish() },
                    onCodeScanned = { code, type ->
                        // Navigate to next activity with container data
                        val intent = Intent(this, AddContainerNextStepActivity::class.java)
                        intent.putExtra("CONTAINER_CODE", code)
                        intent.putExtra("CONTAINER_TYPE", type)
                        intent.putExtra("LOCATION_ID", locationId)
                        startActivity(intent)
                        finish()
                    },
                    requestCameraPermission = {
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                )
            }
        }
    }
}

@Composable
fun AddContainerScreen (
    onBackClick: () -> Unit,
    onCodeScanned: (String, String) -> Unit,
    requestCameraPermission: () -> Unit
) {
    val context = LocalContext.current
    var code by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showCamera by remember { mutableStateOf(false) }
    // Add a flag to prevent multiple scans
    var isScanning by remember { mutableStateOf(false) }

    // Check for camera permission
    val hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    LaunchedEffect(key1 = true) {
        if (!hasCameraPermission) {
            requestCameraPermission()
        }
    }

    Surface (
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(
                title = "New Container"
            )
            BackButton { onBackClick() }
            if (showCamera) {
                val cameraProviderFuture = remember {
                    ProcessCameraProvider.getInstance(context)
                }
                val lifecycleOwner = LocalLifecycleOwner.current
                Column (
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(64.dp)
                            .weight(1f),
                        factory = { context ->
                            val previewView = PreviewView(context)
                            val preview = androidx.camera.core.Preview.Builder().build()
                            val selector = CameraSelector.Builder()
                                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                                .build()
                            preview.surfaceProvider = previewView.surfaceProvider
                            val imageAnalysis = ImageAnalysis.Builder()
                                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                                .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                             imageAnalysis.setAnalyzer(
                                 ContextCompat.getMainExecutor(context),
                                 QrCodeAnalyzer { result ->
                                     // Add a guard to prevent multiple scans
                                     if (isScanning) return@QrCodeAnalyzer

                                     code = result
                                     val type = if (code.endsWith("a")) {
                                         "aquarium"
                                     } else if (code.endsWith("t")) {
                                         "terrarium"
                                     } else {
                                         "invalid_code"
                                     }
                                     println("$code, $type")
                                     if (type == "invalid_code") {
                                         errorMessage = "Invalid code"
                                     } else {
                                         // Set scanning flag to prevent multiple triggers
                                         isScanning = true
                                        onCodeScanned(code, type)
                                     }
                                 }
                             )
                            try {
                                cameraProviderFuture.get().bindToLifecycle(
                                    lifecycleOwner,
                                    selector,
                                    preview,
                                    imageAnalysis
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            previewView
                        })
                }
            } else {
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // QR Scanner button
                        Button(
                            onClick = {
                                if (hasCameraPermission) {
                                    showCamera = true
                                } else {
                                    requestCameraPermission()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.65f)
                                .padding(vertical = 16.dp)
                        ) {
                            Text(
                                text = "Scan QR Code",
                                fontFamily = PoppinsFamily
                            )
                        }

                        Spacer(modifier = Modifier.size(32.dp))

                        Text(
                            text = "-- or --",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 24.sp,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.size(32.dp))

                        // Manual code entry section
                        BasicTextField(
                            value = code,
                            onValueChange = { newValue ->
                                code = newValue
                                errorMessage = null
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.secondary,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = PoppinsFamily
                            ),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (code.isEmpty()) {
                                        Text(
                                            "Enter code manually"
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )

                        Spacer(modifier = Modifier.size(8.dp))

                        // Error message
                        errorMessage?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp,
                                fontFamily = PoppinsFamily
                            )
                        }
                    }
                    Column {
                        Button(
                            onClick = {
                                if (code.trim().isEmpty()) {
                                    errorMessage = "Container code cannot be empty"
                                    return@Button
                                }

                                isLoading = true
                                errorMessage = null

                                val type = if (code.endsWith("a")) {
                                    "aquarium"
                                } else if (code.endsWith("t")) {
                                    "terrarium"
                                } else {
                                    "invalid_code"
                                }

                                if (type == "invalid_code") {
                                    errorMessage = "Invalid code"
                                } else {
                                    // Process manual code
                                    onCodeScanned(code, type)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(0.65f),
                            enabled = !isLoading
                        ) {
                            Text(
                                text = if (isLoading) "Wait..." else "Next",
                                fontFamily = PoppinsFamily
                            )
                        }
                        Spacer(modifier = Modifier.size(64.dp))
                    }
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun AddContainerActivityPreview () {
    FrogTheme {
        AddContainerScreen(
            onBackClick = {},
            onCodeScanned = { _, _ -> },
            requestCameraPermission = {}
        )
    }
}