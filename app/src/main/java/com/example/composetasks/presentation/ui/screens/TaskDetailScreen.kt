package com.example.composetasks.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composetasks.domain.model.Priority
import com.example.composetasks.domain.model.Task
import com.example.composetasks.presentation.state.TaskDetailEvent
import com.example.composetasks.presentation.state.TaskDetailUiState
import com.example.composetasks.presentation.viewmodel.TaskDetailViewModel
import com.example.composetasks.ui.theme.ComposeTasksTheme
import com.example.composetasks.util.DateFormatter
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Long,
    onNavigateBack: () -> Unit,
    onEditTask: (Long) -> Unit,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(taskId) {
        viewModel.onEvent(TaskDetailEvent.LoadTask(taskId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.onEvent(TaskDetailEvent.DeleteTask) },
                        modifier = Modifier.semantics { contentDescription = "Delete task" }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                    }
                    IconButton(
                        onClick = { onEditTask(taskId) },
                        modifier = Modifier.semantics { contentDescription = "Edit task" }
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                ErrorDetailState(uiState.error!!, onNavigateBack)
            }
            uiState.task != null -> {
                TaskDetailContent(
                    task = uiState.task!!,
                    onToggleComplete = { viewModel.onEvent(TaskDetailEvent.ToggleComplete) },
                    onNavigateBack = onNavigateBack,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun TaskDetailContent(
    task: Task,
    onToggleComplete: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = task.description.ifBlank { "No description" },
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text("Due Date", style = MaterialTheme.typography.labelMedium)
                        Text(DateFormatter.formatDate(task.dueDate))
                    }
                    Column {
                        Text("Priority", style = MaterialTheme.typography.labelMedium)
                        Text(task.priority.name)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = onToggleComplete,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (task.isCompleted) "Mark as Incomplete" else "Mark as Complete")
                }
            }
        }
    }
}

@Composable
private fun ErrorDetailState(message: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
        Button(onClick = onBack) {
            Text("Go Back")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskDetailScreenPreview() {
    ComposeTasksTheme {
        TaskDetailContent(
            task = Task(
                id = 1,
                title = "Sample Task",
                description = "This is a preview task",
                dueDate = Date(),
                priority = Priority.HIGH,
                isCompleted = false
            ),
            onToggleComplete = {},
            onNavigateBack = {}
        )
    }
}
