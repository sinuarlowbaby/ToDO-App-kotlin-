package com.sinuarlowbaby.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sinuarlowbaby.myapplication.ui.theme.ToDoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.todoDao()

        setContent {
            ToDoTheme {
                val navController = rememberNavController()

                // Create ViewModel Factory
                val viewModel = viewModel<TodoViewModel>(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return TodoViewModel(dao) as T
                        }
                    }
                )

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        val todoList by viewModel.todoList.collectAsState()
                        val currentSort by viewModel.currentSortOption.collectAsState()

                        HomeScreen(
                            todoList = todoList,
                            currentSort = currentSort,
                            onSortSelected = { viewModel.onSortOptionChanged(it) },
                            onFabClick = { navController.navigate("add") },
                            onToggle = { viewModel.toggleTodoCompletion(it) },
                            onDelete = { viewModel.removeTodo(it) }
                        )
                    }
                    composable("add") {
                        AddTodoScreen(
                            onBackClick = { navController.popBackStack() },
                            onSave = { title, label, priority ->
                                viewModel.addTodo(title, label, priority)
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}