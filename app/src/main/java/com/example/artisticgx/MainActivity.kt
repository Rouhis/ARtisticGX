package com.example.artisticgx

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
            val navController = rememberNavController()
            val currentModel = remember {
                mutableStateOf("sofa")
            }
            //  QRScreen()
            // ARScreen(currentModel.value)
            ARtisticGXTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AppNavigation(
                            controller = navController,
                            viewModel = viewModel,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    controller: NavHostController,
    viewModel: ArtisticViewModel,
    navController: NavController
) {

    NavHost(controller, startDestination = "ARScreen") {
        composable("GetModelsTest") {
            ModelList(viewModel, navController)
        }
        composable("QRScreen") { navBackStackEntry ->
            QRScreen(navController)
        }
        composable("ARScreen") { navBackStackEntry ->
            ARScreen(model = navBackStackEntry.arguments?.getString("model") ?: "", navController)
        }
        composable("ARScreen/{model}") { navBackStackEntry ->
            ARScreen(model = navBackStackEntry.arguments?.getString("model") ?: "", navController)
        }
        composable("ArFrame/{frame}/{video}") { navBackStackEntry ->
            navBackStackEntry.arguments?.getString("video")
                ?.let {
                    Arframe(
                        frame = navBackStackEntry.arguments?.getString("frame")!!,
                        video = it
                    )
                }
        }
    }
}

@Composable
fun ModelList(model: ArtisticViewModel, navController: NavController) {
    val isEmpty = model.isEmpty().observeAsState()
    val framesIsEmpty = model.framesIsEmpty().observeAsState()
    val urls = listOf("sofa", "table_lamp", "lilly_chair", "wooden_cabinet")
    val frameurls = listOf("frame")
    var selectedOption by remember { mutableStateOf("Furniture") }
    val context = MyApp.appContext
    var expanded by remember { mutableStateOf(false) }


    if (isNetworkAvailable(context)) {
        if (isEmpty.value != null) {
            if (isEmpty.value!! < urls.size) {
                println("toimii xdd")
                LaunchedEffect(urls) {
                    getAndSaveModels(model, urls)
                }
            }
        }
        if (framesIsEmpty.value != null) {
            if (framesIsEmpty.value!! < frameurls.size) {
                println("toimii xdd")
                LaunchedEffect(frameurls) {
                    getAndSaveFrames(model, frameurls)
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center)
        ) {
            Button(onClick = { expanded = true}) {
                Text(text = "Choose")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Furniture") },
                    onClick = {
                        Toast.makeText(context, "Furniture", Toast.LENGTH_SHORT).show()
                        selectedOption = "Furniture"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Paintings") },
                    onClick = {
                        Toast.makeText(context, "Paintings", Toast.LENGTH_SHORT).show()
                        selectedOption = "Paintings"
                        expanded = false
                    }
                )
            }
        }

        if (selectedOption == "Furniture") {
            FurnitureList(model = model, navController = navController)
        } else if (selectedOption == "Paintings") {
            FrameList(model = model, navController = navController)
        }
    }else{
        noNetwork(navController)
    }
}

@Composable
fun FurnitureList(model: ArtisticViewModel, navController: NavController){
    val models = model.getAllModels().observeAsState(listOf())
    val initData = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    var modelBitMap by remember { mutableStateOf(initData) }

    Box(modifier = Modifier.padding(50.dp))
    {
        LazyVerticalGrid(
            GridCells.Adaptive(minSize = 128.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            items(models.value) {
                val imageBitMap = remember {
                    BitmapFactory.decodeByteArray(it.image, 0, it.image!!.size)
                }
                modelBitMap = imageBitMap
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Image(
                        bitmap = modelBitMap.asImageBitmap(),
                        contentDescription = "Bitmap image",
                        modifier = Modifier
                            .size(200.dp)
                            .clickable {
                                if (!isNetworkAvailable(MyApp.appContext)) {
                                    showConfirmationDialog(navController.context, navController)

                                } else {
                                    navController.navigate("ARScreen/${it.name}")
                                    Log.i("tiedot", "ARScreen/${it.name}")
                                }

                            }
                    )
                }
            }
        }

    }
}
@Composable
fun FrameList(model: ArtisticViewModel, navController: NavController){

    val models = model.getAllFrames().observeAsState(listOf())
    val initData = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    var modelBitMap by remember { mutableStateOf(initData) }

    Box(modifier = Modifier.padding(50.dp))
    {
        LazyVerticalGrid(
            GridCells.Adaptive(minSize = 128.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            items(models.value) {
                val imageBitMap = remember {
                    BitmapFactory.decodeByteArray(it.image, 0, it.image!!.size)
                }
                modelBitMap = imageBitMap
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Image(
                        bitmap = modelBitMap.asImageBitmap(),
                        contentDescription = "Bitmap image",
                        modifier = Modifier
                            .size(200.dp)
                            .clickable {
                                if (!isNetworkAvailable(MyApp.appContext)) {
                                    showConfirmationDialog(navController.context, navController)

                                } else {
                                    navController.navigate("ArFrame/${it.name}/kolibri")
                                    Log.i("tiedot", "ArFrame/{frame}/{kolibri}")
                                }

                            }
                    )
                }
            }
        }

    }
}


// Function for getting a BitMap from the given URL
private suspend fun getImage(url: String): Bitmap =
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

private suspend fun getAndSaveModels(model: ArtisticViewModel, urls: List<String>) {
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
private suspend fun getAndSaveFrames(frame: ArtisticViewModel, urls: List<String>) {
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
fun showConfirmationDialog(context: Context,navController: NavController) {
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

fun isNetworkAvailable(context: Context?): Boolean {
    if (context == null) return false
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    return true
                }

                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    return true
                }

                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    return true
                }
            }
        }
    } else {
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            return true
        }
    }
    return false
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ARtisticGXTheme {

    }
}