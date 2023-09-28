package com.example.artisticgx

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                        DisplayFrames(viewModel, "https://users.metropolia.fi/~tuomheik/test/test.png")
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

    // Get an Image from the given url as a BitMap and convert it into ByteArray
    val bos = ByteArrayOutputStream()
    LaunchedEffect(url) {
        bitmap = getFrame(url)
    }
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
    val byte = bos.toByteArray()
    println(":DDDDD $byte")
    val byteArrayToBitMap = BitmapFactory.decodeByteArray(byte, 0, byte.size)

    Text("Hello World")
    Row {
        Button(
            onClick = { model.addNewFrame(byte) },
            modifier = Modifier.padding(all = 8.dp)
        ) {
            Text("Add to db")
        }
        Button(
            onClick = {
                println("xpdpx ${newFrame.value}")
                println("xpdd ${newFrame.value!!.size}")
                // Use the value from observing LiveData, and if the value is not empty
                // Convert the ByteArray from the DB into a bitmap and assign it to a variable
                if (newFrame.value != null) {
                    bitmapFromDB = BitmapFactory.decodeByteArray(newFrame.value, 0, newFrame.value!!.size)
                }
            },
            modifier = Modifier.padding(all = 8.dp)
        ) {
            Text("Get frame from db")
        }
    }
    // Display the frame from the DB
    Image(
        bitmap = bitmapFromDB.asImageBitmap(),
        contentDescription = "Bitmap image"
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