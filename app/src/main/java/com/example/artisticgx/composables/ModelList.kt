package com.example.artisticgx.composables

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.artisticgx.MyApp
import com.example.artisticgx.data.ArtisticViewModel
import com.example.artisticgx.applogic.getAndSaveFrames
import com.example.artisticgx.applogic.getAndSaveModels
import com.example.artisticgx.applogic.isNetworkAvailable
import com.example.artisticgx.applogic.showConfirmationDialog

@Composable
fun ModelList(model: ArtisticViewModel, navController: NavController) {
    // Obsever liveData
    val isEmpty = model.isEmpty().observeAsState()
    val framesIsEmpty = model.framesIsEmpty().observeAsState()
    val isEmptyValue = isEmpty.value
    val isFramesEmpty = framesIsEmpty.value

    // List of the models and frames that are in the Metropolia webdisk.
    val urls = listOf("sofa", "table_lamp", "lilly_chair", "wooden_cabinet")
    val frameurls = listOf("frame", "frame2")

    var selectedOption by remember { mutableStateOf("Paintings") }
    val context = MyApp.appContext
    var expanded by remember { mutableStateOf(false) }

    // Check if the user has network connection before calling any functions
    if (isNetworkAvailable(context)) {
        if (isEmptyValue != null) {
            if (isEmptyValue < urls.size) {
                // Call the function that gets the models from the internet and saves them to the DB from a coroutine
                LaunchedEffect(urls) {
                    getAndSaveModels(model, urls)
                }
            }
        }
        if (isFramesEmpty != null) {
            if (isFramesEmpty < frameurls.size) {
                // Call the function that gets the frames from the internet and saves them to the DB from a coroutine
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
            Button(onClick = { expanded = true }) {
                Text(text = "$selectedOption")
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
    } else {
        noNetwork(navController)
    }
}


@Composable
fun FrameList(model: ArtisticViewModel, navController: NavController) {

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
                    it.image?.let { it1 -> BitmapFactory.decodeByteArray(it.image, 0, it1.size) }
                }
                if (imageBitMap != null) {
                    modelBitMap = imageBitMap
                }
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
                                    navController.navigate("ArFrame/${it.name}")
                                    Log.i("tiedot", "ArFrame/{frame}")
                                }

                            }
                    )
                }
            }
        }

    }
}



