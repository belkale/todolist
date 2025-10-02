package com.example.todolist.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.todolist.ui.screens.AddEditTodoScreen
import com.example.todolist.ui.screens.TodoListScreen

@Composable
fun TodoNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "todo_list"
    ) {
        composable("todo_list") {
            TodoListScreen(
                onNavigateToAddTodo = {
                    navController.navigate("add_todo")
                },
                onNavigateToEditTodo = { todoId ->
                    navController.navigate("edit_todo/$todoId")
                }
            )
        }
        
        composable("add_todo") {
            AddEditTodoScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("edit_todo/{todoId}") { backStackEntry ->
            val todoId = backStackEntry.arguments?.getString("todoId")?.toLongOrNull()
            AddEditTodoScreen(
                todoId = todoId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
