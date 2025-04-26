package put.inf154030.frog.containers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import put.inf154030.frog.fragments.BackButton
import put.inf154030.frog.fragments.TopHeaderBar
import put.inf154030.frog.theme.FrogTheme
import put.inf154030.frog.theme.PoppinsFamily

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
    onBackClick: () -> Unit = {},
    onCodeScanned: (String, String) -> Unit,
    requestCameraPermission: () -> Unit = {}
) {
    val context = LocalContext.current
    var code by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showCamera by remember { mutableStateOf(false) }

    // Check for camera permission
    val cameraPermission = remember {
        context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    LaunchedEffect(key1 = true) {
        if (!cameraPermission) {
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
            Column (
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                if (showCamera && cameraPermission) {
                    // Camera preview for QR scanning
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                    ) {
                        CameraPreview(
                            onBarcodeDetected = { barcode ->
                                showCamera = false
                                // Process the barcode and navigate
                                barcode?.let {
                                    val type = if (it.endsWith("1")) "aquarium" else "terrarium"
                                    onCodeScanned(it, type)
                                }
                            }
                        )

                        // Scanning overlay and close button
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            // QR scanning frame
                            Box(
                                modifier = Modifier
                                    .size(250.dp)
                                    .align(Alignment.Center)
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            )

                            // Close button
                            IconButton(
                                onClick = { showCamera = false },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close camera",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            Text(
                                text = "Align QR code within frame",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontFamily = PoppinsFamily,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 32.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // QR Scanner button
                        Button(
                            onClick = {
                                if (cameraPermission) {
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
                                .fillMaxWidth(0.65f)
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

                        Spacer(modifier = Modifier.size(24.dp))

                        Button(
                            onClick = {
                                if (code.trim().isEmpty()) {
                                    errorMessage = "Container code cannot be empty"
                                    return@Button
                                }

                                isLoading = true
                                errorMessage = null

                                val type = if (code.endsWith("1")) "aquarium" else "terrarium"

                                // Process manual code
                                onCodeScanned(code, type)
                            },
                            modifier = Modifier.fillMaxWidth(0.65f),
                            enabled = !isLoading
                        ) {
                            Text(
                                text = if (isLoading) "Wait..." else "Next",
                                fontFamily = PoppinsFamily
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
fun CameraPreview(
    onBarcodeDetected: (String?) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }

            val executor = ContextCompat.getMainExecutor(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                // Setup image analysis for barcode scanning
                val imageAnalysis = ImageAnalysis.Builder()
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                    .setDefaultResolution(Size(1280, 720))  // Set a reasonable default resolution
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(executor, BarcodeAnalyzer { barcode ->
                            onBarcodeDetected(barcode)
                        })
                    }

                // Preview use case
                val preview = Preview.Builder().build()

                // Select back camera
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    // Unbind any bound use cases before rebinding
                    cameraProvider.unbindAll()

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )

                    preview.setSurfaceProvider(previewView.surfaceProvider)
                } catch (e: Exception) {
                    Log.e("CameraX", "Use case binding failed", e)
                }
            }, executor)

            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

class BarcodeAnalyzer(private val onBarcodeDetected: (String?) -> Unit) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient()
    private var isScanning = true

    override fun analyze(imageProxy: ImageProxy) {
        if (!isScanning) {
            imageProxy.close()
            return
        }

        @androidx.camera.core.ExperimentalGetImage
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        // QR codes will be in the rawValue
                        val rawValue = barcode.rawValue
                        if (!rawValue.isNullOrEmpty()) {
                            isScanning = false
                            onBarcodeDetected(rawValue)
                            break
                        }
                    }
                }
                .addOnFailureListener {
                    // Handle any errors
                    Log.e("BarcodeAnalyzer", "Barcode scanning failed", it)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}