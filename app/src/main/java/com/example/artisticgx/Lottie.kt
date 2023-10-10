package com.example.artisticgx
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun ClickableLottieAnimation(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val lottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            R.raw.ympyra
        )
    )

    var isPlaying by remember { mutableStateOf(false) }

    val progress by animateLottieCompositionAsState(
        lottieComposition,
        isPlaying = isPlaying,
        iterations = 1,
    )

    DisposableEffect(progress) {
        if (progress == 1f) {
            navController.navigate("GetModelsTest")
        }
        onDispose { }
    }

    LottieAnimation(
        composition = lottieComposition,
        progress = progress,
        modifier = modifier.clickable {
            isPlaying = !isPlaying
        }
    )
}