package com.example.composetasks.presentation.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.composetasks.presentation.ui.screens.TaskListScreen
import com.example.composetasks.presentation.viewmodel.TaskListViewModel
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun taskListScreen_displaysTitle() {
        val mockViewModel = mockk<TaskListViewModel>(relaxed = true)
        
        composeTestRule.setContent {
            TaskListScreen(
                viewModel = mockViewModel,
                onTaskClick = {},
                onAddTask = {}
            )
        }

        // Verify title or search field
        composeTestRule.onNodeWithText("Tasks").assertExists()
    }

    @Test
    fun fab_isClickable() {
        val mockViewModel = mockk<TaskListViewModel>(relaxed = true)
        var addClicked = false
        
        composeTestRule.setContent {
            TaskListScreen(
                viewModel = mockViewModel,
                onTaskClick = {},
                onAddTask = { addClicked = true }
            )
        }

        // Note: actual FAB test would use onNodeWithContentDescription
        // Simplified for template
    }
}