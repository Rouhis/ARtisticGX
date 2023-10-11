package com.example.artisticgx.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.artisticgx.ClickableLottieAnimation

@Composable
fun noNetwork(navController:NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Oops! It seems you're not connected to the internet. Please enable your Wi-Fi or mobile hotspot and then click the button below to access your AR models.",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 18.sp,
            ),
            modifier = Modifier.padding(top = 16.dp)
        )
        ClickableLottieAnimation( navController = navController, modifier = Modifier.size(200.dp))

    }


}