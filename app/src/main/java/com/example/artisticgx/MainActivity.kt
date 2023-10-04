package com.example.artisticgx

import android.database.DatabaseUtils
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.URL
import kotlin.io.encoding.ExperimentalEncodingApi

class MainActivity : ComponentActivity() {
    private val viewModel: ArtisticViewModel by viewModels()

    // Create a reference to the FireBase database
    private val fireBaseDB =
        FirebaseDatabase.getInstance("https://artisticgx-acd29-default-rtdb.europe-west1.firebasedatabase.app")
    private val myRef = fireBaseDB.getReference("qrcodes")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //MainScreen()
            ARtisticGXTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DisplayFrames(viewModel, myRef)
                    }
                }
            }
        }
    }
}

// Create buttons for adding frames to DB and and showing a frame from DB
@OptIn(ExperimentalEncodingApi::class)
@Composable
fun DisplayFrames(model: ArtisticViewModel, myRef: DatabaseReference) {
    // Observe the LiveData
    val newFrame = model.getFrame().observeAsState()
    val frames = model.getAllFrames().observeAsState(listOf())
    val newPicture = model.getPicture().observeAsState()

    // Check if there are no rows in the frame table. Returns 0 if table is empty, else returns the amount of rows
    val isEmpty = model.isEmpty().observeAsState()

    // Initialize a placeholder BitMap
    val initData = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    var frameBitMapFromDB by remember { mutableStateOf(initData) }
    var pictureBitMap by remember { mutableStateOf(initData) }
    var pictureBitMapFromDB by remember { mutableStateOf(initData) }

    // Get an Image from the given url as a BitMap

    val pictureUrl = "https://users.metropolia.fi/~tuomheik/test/pictureTest.jpg"
    LaunchedEffect(pictureUrl) {
        pictureBitMap = getPicture(pictureUrl)
    }

    // get 8 different frames
    val frameUrl = listOf(
        "https://users.metropolia.fi/~tuomheik/test/frame1",
        "https://users.metropolia.fi/~tuomheik/test/frame2",
        "https://users.metropolia.fi/~tuomheik/test/frame3",
        "https://users.metropolia.fi/~tuomheik/test/frame5",
        "https://users.metropolia.fi/~tuomheik/test/frame6",
        "https://users.metropolia.fi/~tuomheik/test/frame7",
        "https://users.metropolia.fi/~tuomheik/test/frame8"
    )
    // check that there are under 9 rows in the frame table and that the value isn't null
    LaunchedEffect(frameUrl) {
        // Go through all of the frame URLs and add them to the database
        frameUrl.forEach {
            val frameBitMap = getPicture(it)
            val frameByteArray = getByteFromBitMap(frameBitMap)
            isEmpty.value?.let { it1 -> model.addNewFrame(frameByteArray, it1) }
        }
    }

    // Replace with your application context
    val drawableId = R.drawable.testpoto // Replace with the resource ID of your drawable
    val context = MyApp.appContext

    if (newPicture.value != null) {
        pictureBitMapFromDB =
            BitmapFactory.decodeByteArray(newPicture.value, 0, newPicture.value!!.size)
    }

    // Now, mergedBitmap contains the photo inside the photo frame.
    val bitmaptwo = getBitmapFromDrawable(context, drawableId)
    val mergedBitmap = mergeBitmaps(frameBitMapFromDB, pictureBitMapFromDB)

    val pictureByteArray = getByteFromBitMap(pictureBitMap)
    val mergedByteArray = getByteFromBitMap(mergedBitmap)
    val test = Base64.encodeToString(mergedByteArray, Base64.DEFAULT)
    /*val test2 = test.byteInputStream()
    val test3 = IOUtils.toByteArray(test2)
    val xdd = BitmapFactory.decodeByteArray(test3, 0, test3.size)*/
    var id by remember { mutableLongStateOf(0) }
    println("XDDD $id")
    LaunchedEffect(id) {
        if (id < 5) {
            println("id: $id")
            readFromFirebase(myRef, id)
            writeToFirebase(myRef, id, test)
            id += 1
            println("id after + 1: $id")
        }
    }

    Text("Hello World")
    Row {
        Button(
            onClick = {
                model.addNewPicture(pictureByteArray)
            },
            modifier = Modifier.padding(all = 8.dp)
        ) {
            Text("Add picture to db")
        }
    }
    // Display the frame from the DB
    // First Image (bitmap from DB)
    LazyVerticalGrid(GridCells.Adaptive(minSize = 128.dp), userScrollEnabled = true) {

        items(frames.value) {
            if (it.frame != null) {
                // get bitmap from the DB as a byteArray and convert it into a bitmap
                frameBitMapFromDB =
                    BitmapFactory.decodeByteArray(it.frame, 0, it.frame!!.size)
            }
            Image(
                bitmap = frameBitMapFromDB.asImageBitmap(),
                contentDescription = "Bitmap image",
                modifier = Modifier
            )
        }

    }
}

@Composable
fun TestPhoto() {
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
private suspend fun getPicture(url: String): Bitmap =
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

private suspend fun readFromFirebase(myRef: DatabaseReference, id: Long): Long {

    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val value = snapshot.value
            println("value: $value")
        }

        override fun onCancelled(error: DatabaseError) {
            println("failed to read value ${error.toException()}")
        }
    })
    delay(5000)
    return id
}

private fun writeToFirebase(myRef: DatabaseReference, id: Long, mergedString: String) {
    println("id is: $id")
    val mergedBitMap = MergedBitMap(id, mergedString)
    myRef.child("qrcode$id").setValue(mergedBitMap)
}

@IgnoreExtraProperties
data class MergedBitMap(val id: Long? = null, val base64string: String? = null) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ARtisticGXTheme {

    }
}