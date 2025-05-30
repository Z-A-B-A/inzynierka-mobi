package put.inf154030.frog.utils

import android.graphics.ImageFormat
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer

// Analyzer for processing camera frames and detecting QR codes
class QrCodeAnalyzer(
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {
    // Supported image formats for analysis
    private val supportedImageFormats = listOf(
        ImageFormat.YUV_420_888,
        ImageFormat.YUV_422_888,
        ImageFormat.YUV_444_888
    )

    // Called for each camera frame
    override fun analyze(image: ImageProxy) {
        if (image.format in supportedImageFormats) {
            // Convert image buffer to byte array
            val bytes = image.planes.first().buffer.toByteArray()
            // Create luminance source for ZXing
            val source = PlanarYUVLuminanceSource(
                bytes,
                image.width,
                image.height,
                0,
                0,
                image.width,
                image.height,
                false
            )
            // Convert to binary bitmap for decoding
            val binaryBitMap = BinaryBitmap(HybridBinarizer(source))
            try {
                // Try to decode QR code using ZXing
                val result = MultiFormatReader().apply {
                    setHints(
                        mapOf(
                            DecodeHintType.POSSIBLE_FORMATS to arrayListOf(
                                BarcodeFormat.QR_CODE
                            )
                        )
                    )
                }.decode(binaryBitMap)
                // Invoke callback with QR code text
                onQrCodeScanned(result.text)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                // Always close the image to free resources
                image.close()
            }
        }
    }

    // Extension function to convert ByteBuffer to ByteArray
    private fun ByteBuffer.toByteArray() : ByteArray {
        rewind()
        return ByteArray(remaining()).also {
            get(it)
        }
    }
}