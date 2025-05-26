package put.inf154030.frog.views.activities.containers

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
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

// Activity for adding a new container (aquarium/terrarium)
class AddContainerActivity : ComponentActivity() {
    // Launcher for requesting camera permission
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_LONG).show()
        }
    }

    private var locationId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationId = intent.getIntExtra("LOCATION_ID", -1)

        setContent {
            FrogTheme {
                // Main screen composable
                AddContainerScreen(
                    onBackClick = { finish() },
                    onCodeScanned = { code ->
                        // Navigate to next step with scanned/entered code and type
                        val intent = Intent(this, AddContainerNextStepActivity::class.java)
                        intent.putExtra("CONTAINER_CODE", code)
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

// Main screen composable for adding a container
@Composable
fun AddContainerScreen(
    onBackClick: () -> Unit,
    onCodeScanned: (String) -> Unit,
    requestCameraPermission: () -> Unit
) {
    val context = LocalContext.current
    var code by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showCamera by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(false) } // To prevent multiple scans

    // Check if camera permission is granted
    val hasCameraPermission = remember {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Request camera permission on first launch if not granted
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            requestCameraPermission()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            TopHeaderBar(title = "New Container")
            BackButton { onBackClick() }
            if (showCamera) {
                // Show camera preview for QR scanning
                CameraPreviewSection(
                    onCodeDetected = { scannedCode ->
                        if (isScanning) return@CameraPreviewSection
                        code = scannedCode
                        isScanning = true
                        onCodeScanned(code)
                    }
                )
            } else {
                // Show manual entry section
                ManualEntrySection(
                    code = code,
                    onCodeChange = {
                        code = it
                        errorMessage = null
                    },
                    errorMessage = errorMessage,
                    isLoading = isLoading,
                    onScanClick = {
                        // Handle scan button click and permission check
                        val permissionGranted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                        if (permissionGranted) {
                            showCamera = true
                            isScanning = false // Reset scanning state
                            errorMessage = null // Clear error
                        } else {
                            requestCameraPermission()
                        }
                    },
                    onNextClick = {
                        // Handle next button click for manual code entry
                        if (code.trim().isEmpty()) {
                            errorMessage = "Container code cannot be empty"
                            return@ManualEntrySection
                        }

                        isLoading = true
                        errorMessage = null
                        onCodeScanned(code)
                    }
                )
            }
        }
    }
}

// Camera preview composable for QR code scanning
@Composable
private fun CameraPreviewSection(
    onCodeDetected: (String) -> Unit
) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(64.dp)
                .weight(1f),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
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
                    ContextCompat.getMainExecutor(ctx),
                    QrCodeAnalyzer { result ->
                        onCodeDetected(result)
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
            }
        )
    }
}

// Manual entry section for entering container code
@Composable
private fun ManualEntrySection(
    code: String,
    onCodeChange: (String) -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    onScanClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Column(
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
            // Button to open camera for QR scanning
            Button(
                onClick = onScanClick,
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

            // Manual code entry field
            Text(
                text = "Enter code manually",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, bottom = 4.dp),
                color = MaterialTheme.colorScheme.secondary
            )
            BasicTextField(
                value = code,
                onValueChange = onCodeChange,
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
                        innerTextField()
                    }
                }
            )

            Spacer(modifier = Modifier.size(8.dp))

            // Show error message if present
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    fontFamily = PoppinsFamily
                )
            }
        }
        // Action buttons (Next, loading indicator)
        ActionButtonsSection(
            isLoading = isLoading,
            onNextClick = onNextClick
        )
    }
}

// Section for Next button and loading indicator
@Composable
private fun ActionButtonsSection(
    isLoading: Boolean,
    onNextClick: () -> Unit
) {
    Column {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        Button(
            onClick = onNextClick,
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

// Preview for Compose UI
@androidx.compose.ui.tooling.preview.Preview
@Composable
fun AddContainerActivityPreview() {
    FrogTheme {
        AddContainerScreen(
            onBackClick = {},
            onCodeScanned = { _ -> },
            requestCameraPermission = {}
        )
    }
}