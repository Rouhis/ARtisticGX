package com.example.artisticgx

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.artisticgx.composables.ARScreen
import com.example.artisticgx.composables.Arframe
import com.example.artisticgx.composables.ModelList
import com.example.artisticgx.composables.QRScreen
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
            ARScreen(model = navBackStackEntry.arguments?.getString("model") ?: "", navBackStackEntry.arguments?.getInt("model") ?: 0, navController, viewModel)
        }
        composable("ARScreen/{model}/{id}", arguments = listOf(
            navArgument("model") { type = NavType.StringType},
            navArgument("id") { type = NavType.IntType}
        )) { navBackStackEntry ->
            ARScreen(model = navBackStackEntry.arguments?.getString("model") ?: "", navBackStackEntry.arguments?.getInt("id") ?: 0, navController, viewModel)
        }
        composable("ARFrame/{frame}") { navBackStackEntry ->
            Arframe(frame = navBackStackEntry.arguments?.getString("frame") ?: "", navController)
        }
    }
}