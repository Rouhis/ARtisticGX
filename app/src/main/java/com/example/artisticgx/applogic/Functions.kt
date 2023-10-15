package com.example.artisticgx.applogic

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.navigation.NavController
import com.example.artisticgx.data.ArtisticViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.URL


// Function for getting a BitMap from the given URL
suspend fun getImage(url: String): Bitmap =
    withContext(Dispatchers.IO) {
        val imageUrl = URL(url)
        val connection = imageUrl.openConnection()
        val stream = connection.getInputStream()
        return@withContext BitmapFactory.decodeStream(stream)
    }

// Convert a given Bitmap to a ByteArray
 fun getByteFromBitMap(bitmap: Bitmap): ByteArray {
    val bos = ByteArrayOutputStream()
    bos.reset()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
    return bos.toByteArray()
}


suspend fun getAndSaveFrames(frame: ArtisticViewModel, urls: List<String>) {
    urls.forEach {
        val bitmap = getImage("https://users.metropolia.fi/~eeturo/frames/${it}.png")
        println(":DDD $bitmap")
        val frameImage = getByteFromBitMap(bitmap)
        frame.addNewFrame(
            "https://users.metropolia.fi/~eeturo/frames/${it}.glb",
            it,
            frameImage
        )
    }
}
fun showConfirmationDialog(context: Context, navController: NavController) {
    val alertDialogBuilder = AlertDialog.Builder(context)
    alertDialogBuilder.setTitle("Are you sure?")
    alertDialogBuilder.setMessage("Continuing without a model means you won't be able to experience AR content. Are you sure you want to continue without internet connection?")

    // Set a positive button and its click listener on the dialog
    alertDialogBuilder.setPositiveButton("Yes") { dialog, which ->
        navController.navigate("ARScreen/${null}")
    }

    // Set a negative button and its click listener on the dialog
    alertDialogBuilder.setNegativeButton("No") { dialog, which ->
        navController.navigate("GetModelsTest")
    }

    // Create and show the dialog
    val alertDialog = alertDialogBuilder.create()
    alertDialog.show()
}

suspend fun getAndSaveModels(model: ArtisticViewModel, urls: List<String>) {
    urls.forEach {
        val bitmap = getImage("https://users.metropolia.fi/~tuomheik/test/${it}.png")
        println(":DDD $bitmap")
        val modelImage = getByteFromBitMap(bitmap)
        model.addNewModel(
            "https://users.metropolia.fi/~tuomheik/test/${it}.glb",
            it,
            modelImage
        )
    }
}
