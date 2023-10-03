package com.example.artisticgx

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.google.android.filament.utils.h
import com.google.ar.core.Config
import dev.romainguy.kotlin.math.rotation
import io.github.sceneview.Scene
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import io.github.sceneview.node.Node


@Composable
fun ARScreen(model:String) {
    val nodes = remember {
        mutableListOf<ArNode>()
    }
    val modelNode = remember {
        mutableStateOf<ArModelNode?>(null)
    }
    Box(modifier = Modifier.fillMaxSize()){
        ARScene(
            modifier = Modifier.fillMaxSize(),
            nodes = nodes,
            planeRenderer = true,
            onCreate = {arSceneView ->

                arSceneView.lightEstimationMode = Config.LightEstimationMode.DISABLED
                arSceneView.planeRenderer.isShadowReceiver = false
                arSceneView.planeFindingEnabled
                modelNode.value = ArModelNode(arSceneView.engine,PlacementMode.INSTANT, ).apply {
                    loadModelGlbAsync(
                        glbFileLocation = "${model}.glb",
                        scaleToUnits = 0.8f,
                        centerOrigin = Position(x = 0.0f, y = 0.0f, z = 0.0f),

                    ){
                    }
                }
                nodes.add(modelNode.value!!)
            },
            onSessionCreate = {
                planeRenderer.isVisible = true
            }
        )

    }

    LaunchedEffect(key1 = model){
        modelNode.value?.loadModelGlbAsync(
            glbFileLocation = "${model}.glb",
            scaleToUnits = 0.8f,

        )
        Log.e("errorloading","ERROR")
    }

}


@Composable
fun Painting() {
    Image(painter = painterResource(id = R.drawable.testpoto), contentDescription = "asd")
}


