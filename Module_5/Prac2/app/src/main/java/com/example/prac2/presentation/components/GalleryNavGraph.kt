package com.example.prac2.presentation.components

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.prac2.presentation.fullscreen.FullscreenPhotoScreen
import com.example.prac2.presentation.gallery.GalleryScreen
import com.example.prac2.presentation.gallery.GalleryViewModel
import java.net.URLEncoder

@Composable
fun GalleryNavGraph() {
    val navController = rememberNavController()
    val galleryViewModel: GalleryViewModel = viewModel()

    NavHost(navController = navController, startDestination = "gallery") {
        composable("gallery") {
            GalleryScreen(
                viewModel = galleryViewModel,
                onPhotoClick = { path ->
                    val encoded = URLEncoder.encode(path, "UTF-8")
                    navController.navigate("fullscreen/$encoded")
                }
            )
        }
        composable(
            route = "fullscreen/{path}",
            arguments = listOf(navArgument("path") { type = NavType.StringType })
        ) { backStack ->
            val encoded = backStack.arguments?.getString("path") ?: return@composable
            FullscreenPhotoScreen(
                encodedPath = encoded,
                onBack = { navController.popBackStack() }
            )
        }
    }
}