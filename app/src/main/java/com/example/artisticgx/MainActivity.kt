package com.example.artisticgx

import android.content.Context
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.LiveData
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
    // Initialize a placeholder BitMap
    val initData = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    var bitmap by remember { mutableStateOf(initData) }
    var bitmapFromDB by remember { mutableStateOf(initData) }
     // Replace with your application context
    val drawableId = R.drawable.testpoto // Replace with the resource ID of your drawable
    val context = MyApp.appContext

// Now, mergedBitmap contains the photo inside the photo frame.

    // Get an Image from the given url as a BitMap and convert it into ByteArray
    val bos = ByteArrayOutputStream()

    if (newFrame.value != null) {

        bitmapFromDB =
            BitmapFactory.decodeByteArray(newFrame.value, 0, newFrame.value!!.size)
    }
    val bitmaptwo = getBitmapFromDrawable(context, drawableId)
    val mergedBitmap = mergeBitmaps(bitmapFromDB, bitmaptwo)

    LaunchedEffect(url) {
        bitmap = getFrame(url)
    }
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
    val byte = bos.toByteArray()
    println(":DDDDD $byte")

    Text("Hello World")
    Row {
        Button(
            onClick = {
                model.addNewFrame(byte) },
            modifier = Modifier.padding(all = 8.dp)
        ) {
            Text("Add frame to db")
        }
        Button(
                onClick = {
                    /*mergedBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
                    val framedImage = bos.toByteArray()
                    model.addNewFramedPicture(framedImage)*/ },
                modifier = Modifier.padding(all = 8.dp)
        ) {
            Text("Add framed picture to db")
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
        val bitmap = BitmapFactory.decodeStream(stream)
        return@withContext bitmap
    }

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ARtisticGXTheme {

    }
}