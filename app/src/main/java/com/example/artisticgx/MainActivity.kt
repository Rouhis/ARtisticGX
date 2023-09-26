package com.example.artisticgx

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
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
import androidx.compose.ui.tooling.preview.Preview
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

@Composable
fun DisplayFrames(model: ArtisticViewModel, url: String) {
    val test = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    var bitmap by remember { mutableStateOf(test) }
    val bos = ByteArrayOutputStream()
    Text("Hello World")
    //println(":DDD ${model.getAllFrames().value}")
    LaunchedEffect(url) {
        bitmap = getFrame(url)
    }
    println(":DDDD $bitmap")
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
    val byte = bos.toByteArray()
    println(":DDDDD $byte")
    Button(
        onClick = { model.addNewFrame(byte) }
    ) {
        Text("Add to db")
    }
}

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