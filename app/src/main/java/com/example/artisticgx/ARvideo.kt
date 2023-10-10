package com.example.artisticgx

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toFile
import com.google.ar.core.Config
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.Scale
import io.github.sceneview.math.Size
import io.github.sceneview.node.Node
import io.github.sceneview.node.VideoNode
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun Arframe(frame: String, video: String) {
    val context = LocalContext.current // Obtain the Context
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
    val mediaPlayer = remember {
        MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            // Do not set a data source initially; we'll set it when a video is selected.
        }
    }
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            videoUri = uri
            Log.d("videotestiprkl", videoUri.toString())
            val selectedVideoUri = videoUri // Store in a separate variable

            // Update the MediaPlayer's data source when a video is selected
            if (selectedVideoUri != null) {
                try {
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(
                        context,
                        selectedVideoUri
                    ) // Explicitly specify the type here
                    mediaPlayer.prepare()
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Handle exceptions here
                }
            }
        }



    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            nodes = nodes,
            planeRenderer = true,
            onCreate = { arSceneView ->
                arSceneView.lightEstimationMode = Config.LightEstimationMode.DISABLED
                arSceneView.planeRenderer.isShadowReceiver = false
                arSceneView.planeFindingEnabled
                modelNode.value =
                    ArModelNode(arSceneView.engine, PlacementMode.PLANE_VERTICAL).apply {
                        loadModelGlbAsync(
                            glbFileLocation = "https://users.metropolia.fi/~eeturo/glb/$frame.glb",
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
                ).apply {
                    rotation = Rotation(0f, 0f, 180f)

                    // Adjust the scale as needed

                }

                nodes.add(modelNode.value!!)
                nodes.add(modelNode2.value!!)
                Log.d("tuhma", "${modelNode.value}")
                Log.d("tuhma", "$nodes")

            },
            onSessionCreate = {

            },
            onTap = {
                if (videoUri != null) {
                    mediaPlayer.start()

                } else {
                    galleryLauncher.launch("video/*")

                }
            }

        )

    }
}
