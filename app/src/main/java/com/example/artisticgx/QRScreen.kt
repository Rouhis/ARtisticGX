package com.example.artisticgx

import android.util.Log
import android.util.Size
import android.view.Surface.ROTATION_0
import android.view.Surface.ROTATION_270
import android.view.Surface.ROTATION_90
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner


@Composable
fun QRScreen(){

    var code by remember{
        mutableStateOf("")
    }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = {context ->
            val previewView = PreviewView(context)
            val preview = Preview.Builder().build()
            val selector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
            preview.setSurfaceProvider(previewView.surfaceProvider)
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(previewView.width, previewView.height))
                .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                .setTargetRotation(ROTATION_270)
                .build()
            imageAnalysis.setAnalyzer(
                ContextCompat.getMainExecutor(context),
                QRCodeAnalyzer{ result ->
                    code = result
                    Log.d("qr", "tääässs $code")

                }
            )
            try {
                cameraProviderFuture.get().bindToLifecycle(
                    lifecycleOwner,
                    selector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception){
                Log.d("qr","satan")

                e.printStackTrace()
            }
            previewView
        },
            modifier = Modifier.weight(1f))
        Text(text = code, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().padding(32.dp))
    }
}