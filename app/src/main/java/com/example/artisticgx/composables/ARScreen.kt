package com.example.artisticgx.composables

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
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.artisticgx.MyApp
import com.example.artisticgx.data.ArtisticViewModel
import com.google.ar.core.Anchor
import com.example.artisticgx.R
import com.google.ar.core.Config
import com.google.ar.core.Config.LightEstimationMode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.light.direction
import io.github.sceneview.math.Direction
import io.github.sceneview.math.Position


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
    var enabled by remember { mutableStateOf(true) }
    var removeAnchorEnabled by remember { mutableStateOf(true) }
    if (model == "") {
        enabled = false
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
                arSceneView.mainLight?.direction = Direction(-1f)

                arSceneView.cloudAnchorEnabled = true


                modelNode.value =
                    ArModelNode(arSceneView.engine, PlacementMode.PLANE_HORIZONTAL_AND_VERTICAL).apply {
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
                    removeAnchorEnabled = false
                    enabled = false
                    if (model.isNotEmpty()) {
                        if (modelNode.value?.isAnchored == false) {
                            modelNode.value?.anchor()
                        }
                        modelNode.value?.hostCloudAnchor { anchor: Anchor, success: Boolean ->
                            if (success) {
                                println("XPX anchor: ${anchor.cloudAnchorId}")
                                models.value.forEach {
                                    if (id == it.id) {
                                        println("XPX adding to DB")
                                        viewModel.addNewCloudAnchor(anchor.cloudAnchorId, id)
                                        Toast.makeText(
                                            MyApp.appContext,
                                            "Anchor saved",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    enabled = true
                                    removeAnchorEnabled = true
                                }
                            } else {
                                println("XPX failed: ${anchor.cloudAnchorState}")
                                Toast.makeText(
                                    MyApp.appContext,
                                    "Adding the new anchor failed, please try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                                enabled = true
                                removeAnchorEnabled = true
                            }
                        }
                    }
                },
                enabled = enabled
            ) {
                Text("Add anchor and host it")
            }
            /*  onClick uses a cloud anchor ID to get a saved anchor from the ARCore API's cloud storage.
                App gets the cloud anchor ID from DB */
            Button(
                onClick = {
                    enabled = false
                    cloudAnchorFromDB.value?.let {
                        val tipToast = Toast.makeText(
                            MyApp.appContext,
                            "Please aim your camera to the anchored position",
                            Toast.LENGTH_SHORT
                        )
                        tipToast.show()
                        println("XXX $it")
                        modelNode.value?.resolveCloudAnchor(it) { anchor: Anchor, success: Boolean ->
                            if (success) {
                                println("XPX anchorOnResolve: $anchor")
                                tipToast.cancel()
                                Toast.makeText(
                                    MyApp.appContext,
                                    "Moved model back to the anchored position",
                                    Toast.LENGTH_SHORT
                                ).show()
                                enabled = true
                            } else {
                                println("XPX anchorOnResolveFailed ${anchor.cloudAnchorState}")
                                Toast.makeText(
                                    MyApp.appContext,
                                    "Moving model back to the anchored position failed ${anchor.cloudAnchorState}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                enabled = true
                            }
                        }
                    }
                },
                enabled = enabled
            ) {
                if (cloudAnchorFromDB.value?.isNotEmpty() == true) {
                    Text("Move model back to anchored spot")
                } else {
                    Text("No saved anchors for this model")
                }
            }
        }
        /*  Clear the current model's anchor so that it can be moved around freely again.
            Anchor can be re-added by pressing on the "Move model back to anchored spot" button. */
        Button(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp),
            onClick = {
                enabled = true
                if (modelNode.value?.isAnchored == true) {
                    /* If resolving the anchor doesn't move the model back to anchored position, this button
                       can be pressed to stop resolving. */
                    modelNode.value?.cancelCloudAnchorResolveTask()
                }
            },
            enabled = removeAnchorEnabled
        ) {
            Text(
                "Reset model"
            )
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


