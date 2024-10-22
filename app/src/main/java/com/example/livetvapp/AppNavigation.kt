package com.example.livetvapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.tv.material3.ExperimentalTvMaterial3Api

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("channels") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }
        composable("channels") {
            ChannelGrid(onChannelSelected = { channel ->
                navController.navigate("player/${channel.id}")
            })
        }
        composable("player/{channelId}") { backStackEntry ->
            val channelId = backStackEntry.arguments?.getString("channelId")
            if (channelId != null) {
                PlayerScreen(channelId = channelId)
            }
        }
    }
}