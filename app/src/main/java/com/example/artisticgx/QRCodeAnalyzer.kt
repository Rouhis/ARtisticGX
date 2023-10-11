package com.example.artisticgx

import android.graphics.ImageFormat
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer
/**
 * This class is responsible for analyzing images to detect QR codes.
 *
 * The QRCodeAnalyzer implements the ImageAnalysis.Analyzer interface
 * to process image frames and detect QR codes within them.
 *
 * @param onQrCodeScanned A callback function that is invoked when a QR code is successfully scanned.
 */
class QRCodeAnalyzer(
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    // List of supported image formats for processing.
    private val supportedImageFormats = listOf(
        ImageFormat.YUV_420_888,
        ImageFormat.YUV_422_888,
        ImageFormat.YUV_444_888
    )

    /**
     * Analyzes the provided image to detect QR codes.
     *
     * @param image The image frame to analyze for QR codes.
     */
    override fun analyze(image: ImageProxy) {
        if (image.format in supportedImageFormats) {
            // Convert the image data to a byte array.
            val bytes = image.planes.first().buffer.toByteArray()

            // Create a luminance source from the image data.
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

            // Create a binary bitmap for QR code detection.
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

            try {
                // Use the MultiFormatReader to decode QR codes.
                val result = MultiFormatReader().apply {
                    setHints(
                        mapOf(
                            DecodeHintType.POSSIBLE_FORMATS to arrayListOf(
                                BarcodeFormat.QR_CODE
                            )
                        )
                    )
                }.decode(binaryBitmap)

                // Invoke the callback with the scanned QR code text.
                onQrCodeScanned(result.text)
            } catch (e: Exception) {
                // Handle and log any exceptions that occur during QR code detection.
                Log.d("qr", "Error during QR code detection")

                e.printStackTrace()
            } finally {
                // Close the image to release resources.
                image.close()
            }
        }
    }

    /**
     * Converts a ByteBuffer to a byte array.
     *
     * @return The byte array containing the ByteBuffer's data.
     */
    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        return ByteArray(remaining()).also {
            get(it)
        }
    }
}
