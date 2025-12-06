package com.sinuarlowbaby.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Setup Database and ViewModel
            val context = LocalContext.current
            val db = AppDatabase.getDatabase(context)
            val viewModel: TodoViewModel = viewModel(factory = TodoViewModelFactory(db.todoDao()))

            // --- FIX START: Initialize Theme ONCE here ---
            val systemDark = isSystemInDarkTheme()
            // We only set this if the ViewModel hasn't been initialized yet.
            // Since this runs once on app launch, we can just set it.
            // (If you want to persist across app restarts, you'd need SharedPreferences/DataStore,
            // but for now, this fixes the session persistence).
            if (viewModel.isDarkTheme.value == false && !systemDark) {
                // Do nothing, default is false
            } else if (viewModel.isDarkTheme.value == false && systemDark) {
                viewModel.setDarkTheme(true)
            }
            // --- FIX END ---

            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "home") {

                // 1. Home Screen
                composable("home") {
                    HomeScreen(
                        viewModel = viewModel,
                        onAddClick = { navController.navigate("add") },
                        onEditClick = { task -> navController.navigate("add?taskId=${task.id}") }
                    )
                }

                // 2. Add/Edit Screen
                composable(
                    route = "add?taskId={taskId}",
                    arguments = listOf(navArgument("taskId") {
                        type = NavType.IntType
                        defaultValue = -1
                    })
                ) { backStackEntry ->
                    val taskId = backStackEntry.arguments?.getInt("taskId")

                    AddTodoScreen(
                        viewModel = viewModel,
                        taskId = taskId,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}