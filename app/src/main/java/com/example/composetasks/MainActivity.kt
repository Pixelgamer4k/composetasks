package com.example.composetasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.composetasks.presentation.ui.screens.AddEditTaskBottomSheet
import com.example.composetasks.presentation.ui.screens.CalendarScreen
import com.example.composetasks.presentation.ui.screens.SettingsScreen
import com.example.composetasks.presentation.ui.screens.TaskDetailScreen
import com.example.composetasks.presentation.ui.screens.TaskListScreen
import com.example.composetasks.presentation.viewmodel.TaskListViewModel
import com.example.composetasks.ui.theme.ComposeTasksTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTasksTheme {
                ComposeTasksApp()
            }
        }
    }
}

@Composable
fun ComposeTasksApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        BottomNavItem("tasks", Icons.Default.List, R.string.nav_tasks),
        BottomNavItem("calendar", Icons.Default.DateRange, R.string.nav_calendar),
        BottomNavItem("settings", Icons.Default.Settings, R.string.nav_settings)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = stringResource(item.labelRes)) },
                        label = { Text(stringResource(item.labelRes)) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "tasks",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("tasks") {
                val viewModel: TaskListViewModel = hiltViewModel()
                TaskListScreen(
                    viewModel = viewModel,
                    onTaskClick = { taskId -> navController.navigate("task_detail/$taskId") },
                    onAddTask = { navController.navigate("add_edit_task") }
                )
            }
            composable("task_detail/{taskId}") { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId")?.toLongOrNull() ?: 0L
                TaskDetailScreen(
                    taskId = taskId,
                    onNavigateBack = { navController.popBackStack() },
                    onEditTask = { id -> navController.navigate("add_edit_task?taskId=$id") }
                )
            }
            composable("add_edit_task?taskId={taskId}") { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId")?.toLongOrNull()
                AddEditTaskBottomSheet(
                    taskId = taskId,
                    onDismiss = { navController.popBackStack() },
                    onSaveComplete = { navController.popBackStack() }
                )
            }
            composable("calendar") {
                CalendarScreen(onNavigateBack = { /* stub */ })
            }
            composable("settings") {
                SettingsScreen(onNavigateBack = { /* stub */ })
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val labelRes: Int
)
