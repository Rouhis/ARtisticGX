package com.example.artisticgx

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.artisticgx.data.ArtisticViewModel


@Composable
fun AppNavigation(
    controller: NavHostController,
    viewModel: ArtisticViewModel,
    navController: NavController
) {

    NavHost(controller, startDestination = "QRScreen") {
        composable("GetModelsTest") {
            ModelList(viewModel, navController)
        }
        composable("QRScreen") { navBackStackEntry ->
            QRScreen(navController)
        }
        composable("ARScreen") { navBackStackEntry ->
            ARScreen(model = navBackStackEntry.arguments?.getString("model") ?: "", navController)
        }
        composable("ARScreen/{model}") { navBackStackEntry ->
            ARScreen(model = navBackStackEntry.arguments?.getString("model") ?: "", navController)
        }
        composable("ARFrame/{frame}") { navBackStackEntry ->
            Arframe(frame = navBackStackEntry.arguments?.getString("frame") ?: "", navController)
        }
    }
}