package com.example.artisticgx

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.ar.core.Config
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.Node
import io.github.sceneview.node.VideoNode
import kotlinx.coroutines.delay

@Composable
fun Arframe(frame: String, video: String) {
    val nodes = remember {
        mutableListOf<Node>()
    }
    val modelNode = remember {
        mutableStateOf<ArModelNode?>(null)
    }
    val modelNode2 = remember {
        mutableStateOf<VideoNode?>(null)
    }
    val url = "https://users.metropolia.fi/~eeturo/videos/$video.mp4"
    val mediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
        )
        setDataSource(url)
        prepare() // might take long! (for buffering, etc)
        start()
    }
    Box(modifier = Modifier.fillMaxSize()){
        ARScene(
            modifier = Modifier.fillMaxSize(),
            nodes = nodes ,
            planeRenderer = true,
            onCreate =  {arSceneView ->
                arSceneView.lightEstimationMode = Config.LightEstimationMode.DISABLED
                arSceneView.planeRenderer.isShadowReceiver = false
                arSceneView.planeFindingEnabled
                modelNode.value = ArModelNode(arSceneView.engine,PlacementMode.PLANE_VERTICAL ).apply {
                    loadModelGlbAsync(
                        glbFileLocation = "https://users.metropolia.fi/~eeturo/glb/$frame.glb",
                        scaleToUnits = 1f,
                        centerOrigin = Position(x = 0.0f, y = 0.0f, z = 0.0f),

                        ){
                    }
                }
                modelNode2.value = VideoNode(arSceneView.engine, glbFileLocation = "plane.glb",player = mediaPlayer ,scaleToUnits = 0.8f, centerOrigin = Position(x = 0.0f, y = 0.0f, z = 30f)).apply {
                    rotation = Rotation(0f, 0f, 180f)

                }

                nodes.add(modelNode.value!!)
                nodes.add(modelNode2.value!!)
                Log.d("tuhma", "${modelNode.value}")
                Log.d("tuhma", "$nodes")

            },
            onSessionCreate = {

            },
            onTap = {
                mediaPlayer.start()
            }

        )

    }
}
