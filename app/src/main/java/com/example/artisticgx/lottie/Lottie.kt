package com.example.artisticgx.lottie
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.artisticgx.R

/**
 * Composable function that displays a clickable Lottie animation.
 *
 * @param navController The NavController used for navigation when the animation completes.
 * @param modifier The modifier for the Lottie animation.
 */
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
        modifier = modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() })
        {
            isPlaying = !isPlaying
        }
    )
}