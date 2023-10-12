package com.example.artisticgx.composables

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.artisticgx.MyApp
import com.example.artisticgx.data.ArtisticViewModel
import com.example.artisticgx.isNetworkAvailable
import com.example.artisticgx.showConfirmationDialog


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
                                    navController.navigate("ARScreen/${it.name}/${it.id}")
                                    Log.i("tiedot", "ARScreen/${it.name}/${it.id}")
                                }

                            }
                    )
                }
            }
        }

    }
}