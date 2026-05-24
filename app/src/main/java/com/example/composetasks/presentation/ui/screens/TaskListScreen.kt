package com.example.composetasks.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetasks.domain.model.Priority
import com.example.composetasks.domain.model.Task
import com.example.composetasks.presentation.state.SortOption
import com.example.composetasks.presentation.state.TaskListEvent
import com.example.composetasks.presentation.state.TaskListUiState
import com.example.composetasks.presentation.viewmodel.TaskListViewModel
import com.example.composetasks.ui.theme.ComposeTasksTheme
import com.example.composetasks.util.DateFormatter
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel,
    onTaskClick: (Long) -> Unit,
    onAddTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasks") },
                actions = {
                    // Filter button
                    IconButton(onClick = { /* Show filter dialog */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter tasks")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTask,
                modifier = Modifier.semantics { contentDescription = "Add new task" }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Search TextField
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.onEvent(TaskListEvent.SearchQueryChanged(it)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filters row
            FilterChips(
                selectedPriority = uiState.selectedPriorityFilter,
                sortOption = uiState.sortOption,
                showCompleted = uiState.showCompleted,
                onPrioritySelected = { viewModel.onEvent(TaskListEvent.PriorityFilterChanged(it)) },
                onSortChanged = { viewModel.onEvent(TaskListEvent.SortOptionChanged(it)) },
                onShowCompletedChanged = { viewModel.onEvent(TaskListEvent.ShowCompletedChanged(it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Content based on state
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    ErrorState(
                        message = uiState.error!!,
                        onRetry = { viewModel.onEvent(TaskListEvent.RefreshTasks) }
                    )
                }
                uiState.tasks.isEmpty() -> {
                    EmptyState(
                        hasFilters = uiState.searchQuery.isNotBlank() || 
                                     uiState.selectedPriorityFilter != null || 
                                     !uiState.showCompleted
                    )
                }
                else -> {
                    TaskList(
                        tasks = uiState.tasks,
                        onTaskClick = onTaskClick,
                        onToggleComplete = { taskId ->
                            viewModel.onEvent(TaskListEvent.ToggleTaskComplete(taskId))
                        },
                        onDelete = { taskId ->
                            viewModel.onEvent(TaskListEvent.DeleteTask(taskId))
                            scope.launch {
                                snackbarHostState.showSnackbar("Task deleted")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search tasks...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
        label = { Text("Search") }
    )
}

@Composable
private fun FilterChips(
    selectedPriority: Priority?,
    sortOption: SortOption,
    showCompleted: Boolean,
    onPrioritySelected: (Priority?) -> Unit,
    onSortChanged: (SortOption) -> Unit,
    onShowCompletedChanged: (Boolean) -> Unit
) {
    // Simplified filter UI - in real would use Dropdown or Chips
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Priority filter buttons (simplified)
        Priority.values().forEach { priority ->
            androidx.compose.material3.FilterChip(
                selected = selectedPriority == priority,
                onClick = { 
                    onPrioritySelected(if (selectedPriority == priority) null else priority) 
                },
                label = { Text(priority.name) }
            )
        }
        
        // Show completed toggle (simplified as text for brevity)
        androidx.compose.material3.FilterChip(
            selected = showCompleted,
            onClick = { onShowCompletedChanged(!showCompleted) },
            label = { Text(if (showCompleted) "Hide completed" else "Show completed") }
        )
    }
}

@Composable
private fun TaskList(
    tasks: List<Task>,
    onTaskClick: (Long) -> Unit,
    onToggleComplete: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tasks, key = { it.id }) { task ->
            TaskItem(
                task = task,
                onClick = { onTaskClick(task.id) },
                onToggleComplete = { onToggleComplete(task.id) },
                onDelete = { onDelete(task.id) }
            )
        }
    }
}

@Composable
private fun TaskItem(
    task: Task,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics { contentDescription = "Task: ${task.title}" },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox for complete
            androidx.compose.material3.Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggleComplete() },
                modifier = Modifier.semantics { contentDescription = "Mark complete" }
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = DateFormatter.formatDate(task.dueDate),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Priority indicator
            PriorityBadge(priority = task.priority)
        }
    }
}

@Composable
private fun PriorityBadge(priority: Priority) {
    val (color, text) = when (priority) {
        Priority.HIGH -> MaterialTheme.colorScheme.error to "HIGH"
        Priority.MEDIUM -> MaterialTheme.colorScheme.tertiary to "MED"
        Priority.LOW -> MaterialTheme.colorScheme.secondary to "LOW"
    }
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier.padding(4.dp)
    )
}

@Composable
private fun EmptyState(hasFilters: Boolean) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (hasFilters) "No tasks match your filters" else "No tasks yet",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = if (hasFilters) "Try adjusting your search or filters" else "Tap + to add your first task",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Error: $message", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.material3.Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskListScreenPreview() {
    ComposeTasksTheme {
        // Preview with mock state would require more setup, simplified here
        Text("TaskListScreen Preview - Run on device for full view")
    }
}
