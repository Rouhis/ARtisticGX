package com.example.artisticgx

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.artisticgx.data.ArtisticViewModel
import com.google.ar.core.Anchor
import com.google.ar.core.Anchor.CloudAnchorState
import com.google.ar.core.Config
import com.google.ar.core.Config.CloudAnchorMode
import com.google.ar.core.Config.LightEstimationMode
import com.google.ar.core.Future
import com.google.ar.core.HostCloudAnchorFuture
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.position
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import kotlinx.coroutines.delay

@Composable
fun ARScreen(model: String, id: Int, navController: NavController, viewModel: ArtisticViewModel) {
    // Observe LiveData
    val models = viewModel.getAllModels().observeAsState(listOf())
    val cloudAnchorFromDB = viewModel.getCloudAnchor(id).observeAsState()
    println("XPP cloudAnchorFromDB ${cloudAnchorFromDB.value}")

    println("XPP $id")
    val nodes = remember {
        mutableListOf<ArNode>()
    }
    val modelNode = remember {
        mutableStateOf<ArModelNode?>(null)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            nodes = nodes,
            planeRenderer = true,
            onCreate = { arSceneView ->
                arSceneView.lightEstimationMode = Config.LightEstimationMode.DISABLED
                arSceneView.planeRenderer.isShadowReceiver = false
                arSceneView.planeFindingEnabled
                arSceneView.cloudAnchorEnabled = true


                modelNode.value =
                    ArModelNode(arSceneView.engine, PlacementMode.PLANE_VERTICAL).apply {
                        loadModelGlbAsync(
                            glbFileLocation = "https://users.metropolia.fi/~tuomheik/test/$model.glb",
                            scaleToUnits = 0.8f,
                            centerOrigin = Position(x = 0.0f, y = 0.0f, z = 0.0f),

                            ) {
                            //
                            //     rotation = Rotation(0f, 0f, 0f)
                        }
                    }
                nodes.add(modelNode.value!!)
            },
            onSessionCreate = {
                LightEstimationMode.DISABLED
                planeRenderer.isVisible = true
            },
            onTap = {

            }
        )
        Image(painter = painterResource(id = R.drawable.qr_code_png5),
            contentDescription = "qrkuva",
            modifier = Modifier
                .size(60.dp)
                .padding(10.dp)
                .clickable {
                    navController.navigate("QRScreen")
                }
        )

        Image(
            painter = painterResource(id = R.drawable.lista2),
            contentDescription = "lista",
            modifier = Modifier
                .size(60.dp)
                .padding(10.dp)
                .clickable {
                    navController.navigate("GetModelsTest")
                    // Handle click action for the second image here
                }
                .align(Alignment.TopEnd) // Align the image to the top end (right corner)
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /*  Onclick adds a new cloud anchor to ARCore API's cloud storage for 24 hours.
                Also adds the cloud anchor's ID to the models table in DB */
            Button(
                onClick = {
                    if (model.isNotEmpty()) {
                        if (modelNode.value?.isAnchored == false) {
                            modelNode.value?.anchor()
                        }
                        modelNode.value?.hostCloudAnchor { anchor: Anchor, success: Boolean ->
                            Toast.makeText(MyApp.appContext, "Saving the new anchor, please wait a moment", Toast.LENGTH_SHORT).show()
                            if (success) {
                                println("XPX anchor: ${anchor.cloudAnchorId}")
                                models.value.forEach {
                                    if (id == it.id) {
                                        println("XPX adding to DB")
                                        viewModel.addNewCloudAnchor(anchor.cloudAnchorId, id)
                                        Toast.makeText(MyApp.appContext, "Added the new anchor to the DB", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                println("XPX failed: ${anchor.cloudAnchorState}")
                                Toast.makeText(MyApp.appContext, "Adding the new anchor failed, please try again", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            ) {
                Text("Add anchor and host it")
            }
            /*  onClick uses a cloud anchor ID to get a saved anchor from the ARCore API's cloud storage.
                App gets the cloud anchor ID from DB */
            Button(
                onClick = {
                    cloudAnchorFromDB.value?.let {
                        val tipToast = Toast.makeText(MyApp.appContext, "Please aim your camera to the anchored position", Toast.LENGTH_SHORT)
                        tipToast.show()
                        modelNode.value?.resolveCloudAnchor(it) { anchor: Anchor, success: Boolean ->
                            if (success) {
                                println("XPX anchorOnResolve: $anchor")
                                tipToast.cancel()
                                Toast.makeText(MyApp.appContext, "Moved model back to the anchored position", Toast.LENGTH_SHORT).show()
                            } else {
                                println("XPX anchorOnResolveFailed ${anchor.cloudAnchorState}")
                                Toast.makeText(MyApp.appContext, "Moving model back to the anchored position failed ${anchor.cloudAnchorState}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            ) {
                if (cloudAnchorFromDB.value?.isNotEmpty() == true) {
                    Text("Move model back to anchored spot")
                } else {
                    Text("No saved anchors for this model")
                }
            }
        }
        /*  Clear the current model's anchor so that it can be moved around freely again.
            Anchor can be re-added by pressing on the "Move model back to anchored spot" button */
        Button(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp),
            onClick = { modelNode.value?.detachAnchor() }
        ) {
            Text("Clear model anchor \n" +
                    "temporarily")
        }
    }

    /*LaunchedEffect(key1 = model){
        modelNode.value?.loadModelGlbAsync(
            glbFileLocation = "${model}.glb",
            scaleToUnits = 0.8f,

        )
        Log.e("errorloading","ERROR")
    }*/

}


/*@Composable
fun Painting() {
    Image(painter = painterResource(id = R.drawable.testpoto), contentDescription = "asd")
}*/


