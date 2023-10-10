package com.example.artisticgx

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.ar.core.Config
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.light.direction
import io.github.sceneview.math.Direction
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.Node
import io.github.sceneview.node.VideoNode

@Composable
fun Arframe(frame: String, navController: NavController) {
    val context = LocalContext.current
    val nodes = remember {
        mutableListOf<Node>()
    }
    val modelNode = remember {
        mutableStateOf<ArModelNode?>(null)
    }
    val modelNode2 = remember {
        mutableStateOf<VideoNode?>(null)
    }
    val mediaPlayer = remember {
        MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }
    }
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            videoUri = uri
            val selectedVideoUri = videoUri
            if (selectedVideoUri != null) {
                try {
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(
                        context,
                        selectedVideoUri
                    )
                    mediaPlayer.prepare()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            nodes = nodes,
            planeRenderer = true,
            onCreate = { arSceneView ->
                arSceneView.lightEstimationMode = Config.LightEstimationMode.DISABLED
                arSceneView.planeRenderer.isShadowReceiver = false
                arSceneView.planeFindingEnabled
                arSceneView.mainLight?.direction = Direction(-1f)
                modelNode.value =
                    ArModelNode(arSceneView.engine, PlacementMode.PLANE_VERTICAL).apply {
                        loadModelGlbAsync(
                            glbFileLocation = "https://users.metropolia.fi/~eeturo/frames/$frame.glb",
                            scaleToUnits = 1f,
                            centerOrigin = Position(x = 0.0f, y = 0.0f, z = 0.0f),
                        ) {
                        }
                    }
                modelNode2.value = VideoNode(
                    arSceneView.engine,
                    glbFileLocation = "plane.glb",
                    player = mediaPlayer,
                    scaleToUnits = 0.8f,
                    centerOrigin = Position(x = 0.0f, y = 0.0f, z = 30f),
                    scaleToVideoRatio = false
                ).apply {
                    rotation = Rotation(0f, 0f, 180f)
                }

                nodes.add(modelNode.value!!)
                nodes.add(modelNode2.value!!)
            },
            onSessionCreate = {

            },
            onTap = {
                mediaPlayer.start()
            }

        )
        Box(
            modifier = Modifier

                .fillMaxSize()
                .wrapContentSize(Alignment.BottomCenter)
        ) {
            Button(onClick = {
                galleryLauncher.launch("video/*")
            }) {
                Text(text = "Choose video")
            }

    }
}}
