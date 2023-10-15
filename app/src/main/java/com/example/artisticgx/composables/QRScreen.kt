package com.example.artisticgx.composables

import android.util.Log
import android.util.Size
import android.view.Surface.ROTATION_270
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.artisticgx.applogic.QRCodeAnalyzer
import com.example.artisticgx.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


/**
 * This composable function represents the QR code scanning screen.
 *
 * @param navController The navigation controller for handling screen transitions.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRScreen(navController: NavController) {

    // Mutable state for storing the scanned QR code.
    var code: String? by remember {
        mutableStateOf(null)
    }

    // Get the current context and lifecycle owner.
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Create a camera provider future to initialize the camera.
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }

    // Get the camera permission state.
    val cameraPermissionState: PermissionState =
        rememberPermissionState(android.Manifest.permission.CAMERA)

    // Check if the camera permission is granted.
    val hasPermission = cameraPermissionState.status.isGranted

    // Define the permission request function.
    val onRequestPermission = cameraPermissionState::launchPermissionRequest

    // Check if the camera permission is granted.
    if (hasPermission) {
        // Main content inside a Box composable.
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // AndroidView for displaying the camera preview.
            AndroidView(
                factory = { context ->
                    val previewView = PreviewView(context)
                    val preview = Preview.Builder().build()
                    val selector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)

                    // Create an ImageAnalysis for QR code scanning.
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setTargetResolution(Size(previewView.width, previewView.height))
                        .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                        .setTargetRotation(ROTATION_270)
                        .build()
                    imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(context),
                        QRCodeAnalyzer { result ->
                            code = result
                            Log.d("qr", "Scanned QR code: $code")
                        }
                    )

                    try {
                        // Bind the camera to the lifecycle.
                        cameraProviderFuture.get().bindToLifecycle(
                            lifecycleOwner,
                            selector,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        Log.d("qr", "Error binding camera to lifecycle")
                        e.printStackTrace()
                    }
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            // Icons for navigation to other screens.
            Image(painter = painterResource(id = R.drawable.kivaa), contentDescription = "Image",
                modifier = Modifier
                    .size(60.dp)
                    .padding(10.dp)
                    .clickable {
                        navController.navigate("ARScreen")
                    }
            )

            Image(painter = painterResource(id = R.drawable.lista2), contentDescription = "Lista",
                modifier = Modifier
                    .size(60.dp)
                    .padding(10.dp)
                    .clickable {
                        navController.navigate("GetModelsTest")
                    }
                    .align(Alignment.TopEnd)
            )

            // Display the scanned QR code and navigate based on its content.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
                    .background(color = Color.Gray, shape = RoundedCornerShape(8.dp))
                    .clickable {
                        if (code == "frame") {
                            navController.navigate("ARFrame/$code")
                        } else {
                            navController.navigate("ARScreen/$code")
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = code ?: "",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    } else {
        // Display a screen for handling the case when camera permission is not granted.
        NoPermissionScreen(onRequestPermission)
    }
}


