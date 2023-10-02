package com.example.artisticgx

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.unit.dp


@RequiresApi(Build.VERSION_CODES.P)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewImageCapture() {

    val result = remember { mutableStateOf<Bitmap?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryImage = remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    val previewLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            result.value = it }

    //Open gallery
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    galleryLauncher.launch("image/*")
                },
            ) {
                Icon(Icons.Filled.AddCircle, "Choose gallery photo button")
                Text(text = "Choose photo")
            }

        }, floatingActionButtonPosition = FabPosition.End
    ) {
        Column {
            Button(onClick = {
                previewLauncher.launch(null)
            }, modifier = Modifier.size(width = 80.dp, height = 80.dp)) {
                Text("preview")
            }

            //Show photo chosen from gallery
            imageUri?.let {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                galleryImage.value = ImageDecoder.decodeBitmap(source)
            }
            galleryImage.value?.let { image ->
             //   Log.d("qwerty", "valittu $image" )
                Image(
                    bitmap = image.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )
            }

            // Show taken photo
            result.value?.let { image ->
              //  Log.d("qwerty", "otettu $image" )
                Image(
                    bitmap = image.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth())
            }
        }
    }
}




