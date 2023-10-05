package com.example.artisticgx

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.ar.core.Config
import dev.romainguy.kotlin.math.rotation
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.VideoNode

@Composable
fun ARvideo(model:String) {
    val nodes = remember {
        mutableListOf<VideoNode>()
    }
    var videoNode: VideoNode

    var mediaPlayer: MediaPlayer
    Box(modifier = Modifier.fillMaxSize()){
        ARScene(
            modifier = Modifier.fillMaxSize(),
            nodes = nodes,
            planeRenderer = true,
            onCreate =  {arSceneView ->
                mediaPlayer = MediaPlayer.create(MyApp.appContext,R.raw.ad)
                videoNode = VideoNode(arSceneView.engine, glbFileLocation = "plane.glb",player = mediaPlayer ,scaleToUnits = 0.5f, centerOrigin = Position(x = 0.0f, y = 0.0f, z = 30.0f))
                nodes.add(videoNode)
                mediaPlayer.start()

            }
            ,
            onSessionCreate = {

            },
            onTap = {

            }

        )

    }



}