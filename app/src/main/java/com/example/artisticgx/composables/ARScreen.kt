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

    val nodes = remember {
        mutableListOf<ArNode>()
    }
    val modelNode = remember {
        mutableStateOf<ArModelNode?>(null)
    }

    // Use these values to enable or disable a button
    var enabled by remember { mutableStateOf(true) }
    var removeAnchorEnabled by remember { mutableStateOf(true) }

    // If ARScreen was openened without passing a model, disable add anchor and move model buttons
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
            Button(
                onClick = {
                    /*  Disable all of the buttons during the API request,
                        so that the app can't be intentionally crashed by spamming buttons.
                        Buttons are re-enabled once the request is done.*/
                    removeAnchorEnabled = false
                    enabled = false
                    if (model.isNotEmpty()) {
                        // If the model hasn't already been anchored, onclick anchors the model to the current position
                        if (modelNode.value?.isAnchored == false) {
                            modelNode.value?.anchor()
                        }
                        /*  Adds a new cloud anchor to ARCore API's cloud storage for 24 hours.
                            hostCloudAnchor returns the new cloud anchor that was created
                            and a boolean for whether the operation was successful on not.
                            These values are then used to save the cloudanchorID to the DB and show a toast,
                            or show a toast containing the error message if the operation failed */
                        modelNode.value?.hostCloudAnchor { anchor: Anchor, success: Boolean ->
                            if (success) {
                                models.value.forEach {
                                    if (id == it.id) {
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
                Text("Save this position for the model")
            }
            /*  onClick uses a cloud anchor ID to get a saved anchor from the ARCore API's cloud storage.
                App gets the cloud anchor ID from DB */
            Button(
                onClick = {
                    enabled = false
                    /* Get the selected model's cloud anchor from DB as liveData
                    and use it to resolve the cloudAnchor */
                    cloudAnchorFromDB.value?.let {
                        val tipToast = Toast.makeText(
                            MyApp.appContext,
                            "Please aim your camera to the saved position",
                            Toast.LENGTH_SHORT
                        )
                        tipToast.show()
                        modelNode.value?.resolveCloudAnchor(it) { anchor: Anchor, success: Boolean ->
                            if (success) {
                                tipToast.cancel()
                                Toast.makeText(
                                    MyApp.appContext,
                                    "Moved model back to the saved position",
                                    Toast.LENGTH_SHORT
                                ).show()
                                enabled = true
                            } else {
                                Toast.makeText(
                                    MyApp.appContext,
                                    "Moving model back to the saved position failed ${anchor.cloudAnchorState}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                enabled = true
                            }
                        }
                    }
                },
                enabled = enabled
            ) {
                // Change the button text depending on if the current model has a cloudAnchor or not
                if (cloudAnchorFromDB.value?.isNotEmpty() == true) {
                    Text("Move model back to the saved spot spot")
                } else {
                    Text("Model position hasn't been saved")
                }
            }
        }
        /* If the anchor isn't returning to the anchored position when the move model back button is pressed,
           it likely is because during the resolving process the plane that the camera is pointed at isn't recognized as the anchored position.
           If this happens the user can click Reset model state button to stop the resolving process */
        Button(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp),
            onClick = {
                enabled = true
                if (modelNode.value?.isAnchored == true) {
                    modelNode.value?.cancelCloudAnchorResolveTask()
                }
            },
            enabled = removeAnchorEnabled
        ) {
            Text(
                "Reset model state"
            )
        }
    }
}


