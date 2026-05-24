package com.example.composetasks.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetasks.domain.model.Priority
import com.example.composetasks.domain.model.Task
import com.example.composetasks.domain.usecase.AddTaskUseCase
import com.example.composetasks.domain.usecase.DeleteTaskUseCase
import com.example.composetasks.domain.usecase.GetTasksUseCase
import com.example.composetasks.domain.usecase.ToggleTaskCompleteUseCase
import com.example.composetasks.presentation.state.SortOption
import com.example.composetasks.presentation.state.TaskListEvent
import com.example.composetasks.presentation.state.TaskListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val toggleTaskCompleteUseCase: ToggleTaskCompleteUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _priorityFilter = MutableStateFlow<Priority?>(null)
    private val _sortOption = MutableStateFlow(SortOption.DUE_DATE)
    private val _showCompleted = MutableStateFlow(true)

    private val _uiState = MutableStateFlow(TaskListUiState(isLoading = true))
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    // Combined flow for filtered/sorted tasks
    private val filteredTasks = combine(
        getTasksUseCase(),
        _searchQuery,
        _priorityFilter,
        _sortOption,
        _showCompleted
    ) { tasks, query, priority, sort, showCompleted ->
        var result = tasks

        // Search filter
        if (query.isNotBlank()) {
            result = result.filter { task ->
                task.title.contains(query, ignoreCase = true) ||
                task.description.contains(query, ignoreCase = true)
            }
        }

        // Priority filter
        priority?.let {
            result = result.filter { task -> task.priority == it }
        }

        // Completed filter
        if (!showCompleted) {
            result = result.filter { !it.isCompleted }
        }

        // Sort
        result = when (sort) {
            SortOption.DUE_DATE -> result.sortedBy { it.dueDate ?: Date(Long.MAX_VALUE) }
            SortOption.PRIORITY -> result.sortedByDescending { it.priority.ordinal }
            SortOption.CREATED_DATE -> result.sortedByDescending { it.createdAt }
            SortOption.TITLE -> result.sortedBy { it.title }
        }

        result
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        observeTasks()
    }

    private fun observeTasks() {
        viewModelScope.launch {
            combine(
                filteredTasks,
                _searchQuery,
                _priorityFilter,
                _sortOption,
                _showCompleted
            ) { tasks, query, priority, sort, showCompleted ->
                TaskListUiState(
                    tasks = tasks,
                    isLoading = false,
                    searchQuery = query,
                    selectedPriorityFilter = priority,
                    sortOption = sort,
                    showCompleted = showCompleted
                )
            }.collect { state ->
                _uiState.update { state }
            }
        }
    }

    fun onEvent(event: TaskListEvent) {
        when (event) {
            is TaskListEvent.SearchQueryChanged -> {
                _searchQuery.update { event.query }
            }
            is TaskListEvent.PriorityFilterChanged -> {
                _priorityFilter.update { event.priority }
            }
            is TaskListEvent.SortOptionChanged -> {
                _sortOption.update { event.option }
            }
            is TaskListEvent.ShowCompletedChanged -> {
                _showCompleted.update { event.show }
            }
            is TaskListEvent.ToggleTaskComplete -> {
                viewModelScope.launch {
                    val task = _uiState.value.tasks.find { it.id == event.taskId }
                    task?.let {
                        toggleTaskCompleteUseCase(event.taskId, !it.isCompleted)
                    }
                }
            }
            is TaskListEvent.DeleteTask -> {
                viewModelScope.launch {
                    deleteTaskUseCase(event.taskId)
                }
            }
            TaskListEvent.RefreshTasks -> {
                // Trigger refresh if needed, but since Flow, auto updates
                _uiState.update { it.copy(isLoading = true) }
                // In real impl would reload
            }
        }
    }

    // Helper for adding quick task from FAB or elsewhere
    fun addQuickTask(title: String) {
        viewModelScope.launch {
            val newTask = Task(
                title = title,
                dueDate = null,
                priority = Priority.MEDIUM
            )
            addTaskUseCase(newTask)
        }
    }
}
