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
    val preloaderLottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            R.raw.ympyra
        )
    )

    var isPlaying by remember { mutableStateOf(false) }

    val preloaderProgress by animateLottieCompositionAsState(
        preloaderLottieComposition,
        isPlaying = isPlaying,
        iterations = 1,
    )

    DisposableEffect(preloaderProgress) {
        if (preloaderProgress == 1f) {
            navController.navigate("GetModelsTest")
        }
        onDispose { }
    }

    LottieAnimation(
        composition = preloaderLottieComposition,
        progress = preloaderProgress,
        modifier = modifier.clickable {
            isPlaying = !isPlaying
        }
    )
}