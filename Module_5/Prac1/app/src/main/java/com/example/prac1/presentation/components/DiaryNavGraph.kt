package com.example.prac1.presentation.components

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prac1.presentation.detail.DiaryDetailScreen
import com.example.prac1.presentation.list.DiaryListScreen
import com.example.prac1.presentation.list.DiaryListViewModel
import java.net.URLDecoder
import java.net.URLEncoder

@Composable
fun DiaryNavGraph() {
    val navController = rememberNavController()
    val listViewModel: DiaryListViewModel = viewModel()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            DiaryListScreen(
                viewModel = listViewModel,
                onOpenEntry = { fileName ->
                    val encoded = URLEncoder.encode(fileName, "UTF-8")
                    navController.navigate("detail/$encoded")
                },
                onNewEntry = { navController.navigate("detail/new") }
            )
        }

        composable(
            route = "detail/{fileName}",
            arguments = listOf(navArgument("fileName") { type = NavType.StringType })
        ) { backStackEntry ->
            val raw = backStackEntry.arguments?.getString("fileName") ?: "new"
            val fileName = if (raw == "new") null
            else URLDecoder.decode(raw, "UTF-8")

            DiaryDetailScreen(
                fileName = fileName,
                onBack = { navController.popBackStack() },
                onSaved = { entry ->
                    if (fileName == null) listViewModel.onEntryAdded(entry)
                    else listViewModel.onEntryUpdated(entry)
                }
            )
        }
    }
}