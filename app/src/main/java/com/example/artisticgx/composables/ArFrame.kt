package com.example.artisticgx.composables

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.navigation.NavController
import com.example.artisticgx.MyApp
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

//This composable is used to display an ArScreen and  populate it with a frame and a video player
@Composable
fun Arframe(frame: String, navController: NavController) {

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
                        MyApp.appContext,
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
        //This ARScene is used to populate the screen with an camera that is AR capable
        ARScene(
            modifier = Modifier.fillMaxSize(),
            nodes = nodes,
            planeRenderer = true,
            onCreate = { arSceneView ->
                arSceneView.lightEstimationMode = Config.LightEstimationMode.DISABLED
                arSceneView.planeRenderer.isShadowReceiver = false
                arSceneView.planeFindingEnabled
                arSceneView.mainLight?.direction = Direction(-1f)
                //We add a 3d model to a modelNode
                modelNode.value =
                    ArModelNode(arSceneView.engine, PlacementMode.PLANE_VERTICAL).apply {
                        loadModelGlbAsync(
                            glbFileLocation = "https://users.metropolia.fi/~eeturo/frames/$frame.glb",
                            scaleToUnits = 1f,
                            centerOrigin = Position(x = 0.0f, y = 0.0f, z = 0.0f),
                        ) {
                        }
                    }
                //We add a VideoNode to a model node
                modelNode2.value = VideoNode(
                    arSceneView.engine,
                    //The plane is used as the base where the mediaplayer plays
                    glbFileLocation = "plane.glb",
                    player = mediaPlayer,
                    scaleToUnits = 0.8f,
                    centerOrigin = Position(x = 0.0f, y = 0.0f, z = 30f),
                    scaleToVideoRatio = false
                ).apply {
                    //For some reason we need to rotate this by 180 degrees to show the video the right way
                    rotation = Rotation(0f, 0f, 180f)
                }

                nodes.add(modelNode.value!!)
                nodes.add(modelNode2.value!!)
            },
            onSessionCreate = {

            },
            onTap = {
                //On tap of the screen the mediaplayer starts
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
    }
}
