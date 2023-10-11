package com.example.artisticgx

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
fun ARScreen(model:String, navController: NavController) {
    val nodes = remember {
        mutableListOf<ArNode>()
    }
    val modelNode = remember {
        mutableStateOf<ArModelNode?>(null)
    }
    var anchor: Anchor? = null
    var future: HostCloudAnchorFuture? = null

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
                if (!modelNode.value?.isAnchored!!) {
                    modelNode.value!!.anchor()
                }
                fun onHostComplete(cloudAnchorId: String, cloudState: CloudAnchorState) {
                    if (cloudState == CloudAnchorState.SUCCESS) {
                        println("Cloud Anchor Hosted. ID: $cloudAnchorId")
                    } else {
                        println("Error while hosting: $cloudState");
                    }
                }
                println("XPX toimiiko?: ${future?.state}")
                println("XPX it ${it}")
                println("XPX trackable ${it.trackable.trackingState} trackingstate ${TrackingState.TRACKING}")
                println("XPX hitpose ${it.hitPose.position}")
                println("XPX distance ${it.distance}")
                println("XPX packagename: ${MyApp.appContext.packageName}")
                modelNode.value!!.hostCloudAnchor { anchori: Anchor, success: Boolean ->
                    if (success) {
                        println("XPX anchor: ${anchori.cloudAnchorId}")
                    } else {
                        println("XPX failed: ${anchori.cloudAnchorState}")
                    }
                }
                /*if (it.trackable.trackingState == TrackingState.TRACKING) {
                    val test = it.createAnchor()
                    println("XPX anchor: $test pose: ${test.pose}  trackingState: ${test.trackingState}")
                    if (this.arSession != null && future == null) {
                        println("XPX allAnchors: ${this.arSession?.allAnchors}")
                        future = this.arSession?.hostCloudAnchorAsync(test, 1, null)
                        println("XPX state: ${future?.state}")
                        println("XPX id: ${future?.resultCloudAnchorId}")
                    }
                    if (future != null && future?.state.toString() == "DONE") {
                        println("XPX state: ${future?.state} and ID: ${future?.resultCloudAnchorId}")
                    }
                    //future = session.hostCloudAnchorAsync(test, 1, null)
                    //println("XPX future: $future trackingState: ${test.trackingState}")
                }*/
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
        Button(
            onClick = {
                println("XPX host: ${future?.state}")
            }
        ) {
            Text(":DD")
        }
    }

    /*fun onClearButton() {
        if (anchor != null) {
            anchor!!.detach()
            anchor = null
        }

        if (future != null) {
            future!!.cancel()
            future = null
        }
    }*/

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


