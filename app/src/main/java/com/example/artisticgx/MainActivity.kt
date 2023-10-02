package com.example.artisticgx

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.artisticgx.data.ArtisticViewModel
import com.example.artisticgx.ui.theme.ARtisticGXTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.URL

class MainActivity : ComponentActivity() {
    private val viewModel: ArtisticViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ARtisticGXTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DisplayFrames(
                            viewModel,
                            "https://users.metropolia.fi/~tuomheik/test/test.png"
                        )
                    }
                }
            }
        }
    }
}

// Create buttons for adding frames to DB and and showing a frame from DB
@Composable
fun DisplayFrames(model: ArtisticViewModel, url: String) {
    // Observe the LiveData
    val newFrame = model.getFrame().observeAsState()
    val frames = model.getAllFrames().observeAsState(listOf())
    val newPicture = model.getPicture().observeAsState()

    // Initialize a placeholder BitMap
    val initData = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    var frameBitMap by remember { mutableStateOf(initData) }
    var frameBitMapFromDB by remember { mutableStateOf(initData) }
    var pictureBitMap by remember { mutableStateOf(initData) }
    var pictureBitMapFromDB by remember { mutableStateOf(initData) }


    // Get an Image from the given url as a BitMap
    LaunchedEffect(url) {
        frameBitMap = getFrame(url)
    }

    val pictureUrl = "https://users.metropolia.fi/~tuomheik/test/pictureTest.jpg"
    LaunchedEffect(pictureUrl) {
        pictureBitMap = getFrame(pictureUrl)
    }

     // Replace with your application context
    val drawableId = R.drawable.testpoto // Replace with the resource ID of your drawable
    val context = MyApp.appContext

    if (newFrame.value != null) {
        // get bitmap from the DB as a byteArray and convert it into a bitmap
        frameBitMapFromDB =
            BitmapFactory.decodeByteArray(newFrame.value, 0, newFrame.value!!.size)
    }

    if (newPicture.value != null) {
        pictureBitMapFromDB =
            BitmapFactory.decodeByteArray(newPicture.value, 0, newPicture.value!!.size)
    }

    // Now, mergedBitmap contains the photo inside the photo frame.
    val bitmaptwo = getBitmapFromDrawable(context, drawableId)
    val mergedBitmap = mergeBitmaps(frameBitMapFromDB, pictureBitMapFromDB)

    val frameByteArray = getByteFromBitMap(frameBitMap)
    val pictureByteArray = getByteFromBitMap(pictureBitMap)

    if (frameByteArray == pictureByteArray) {
        println("Values are the same")
    } else {
        println("Values are not the same")
    }

    Text("Hello World")
    Row {
        Button(
            onClick = {
                model.addNewFrame(frameByteArray) },
            modifier = Modifier.padding(all = 8.dp)
        ) {
            Text("Add frame to db")
        }
        Button(
                onClick = {
                    model.addNewPicture(pictureByteArray) },
                modifier = Modifier.padding(all = 8.dp)
        ) {
            Text("Add picture to db")
        }
    }
    // Display the frame from the DB
    Box(modifier = Modifier)
    {
        // First Image (bitmap from DB)
        Image(
            bitmap = mergedBitmap.asImageBitmap(),
            contentDescription = "Bitmap image",
            modifier = Modifier

        )

    }


}

@Composable
fun TestPhoto(){
    val imagePainter = painterResource(id = R.drawable.testpoto)
    Image(
        painter = imagePainter,
        contentDescription = null, // Provide a content description for accessibility (if needed)
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1F)
    )
}

// Function for getting a BitMap from the given URL
private suspend fun getFrame(url: String): Bitmap =
    withContext(Dispatchers.IO) {
        val imageUrl = URL(url)
        val connection = imageUrl.openConnection()
        val stream = connection.getInputStream()
        return@withContext BitmapFactory.decodeStream(stream)
    }

// Convert a given Bitmap to a ByteArray
private fun getByteFromBitMap(bitmap: Bitmap): ByteArray {
    val bos = ByteArrayOutputStream()
    bos.reset()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
    return bos.toByteArray()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ARtisticGXTheme {

    }
}