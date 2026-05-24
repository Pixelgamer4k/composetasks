package com.example.composetasks.presentation.state

import com.example.composetasks.domain.model.Priority
import com.example.composetasks.domain.model.Task

data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val selectedPriorityFilter: Priority? = null,
    val sortOption: SortOption = SortOption.DUE_DATE,
    val showCompleted: Boolean = true
)

enum class SortOption {
    DUE_DATE, PRIORITY, CREATED_DATE, TITLE
}

sealed class TaskListEvent {
    data class SearchQueryChanged(val query: String) : TaskListEvent()
    data class PriorityFilterChanged(val priority: Priority?) : TaskListEvent()
    data class SortOptionChanged(val option: SortOption) : TaskListEvent()
    data class ShowCompletedChanged(val show: Boolean) : TaskListEvent()
    data class ToggleTaskComplete(val taskId: Long) : TaskListEvent()
    data class DeleteTask(val taskId: Long) : TaskListEvent()
    object RefreshTasks : TaskListEvent()
}

sealed class TaskEvent {
    data class ShowSnackbar(val message: String) : TaskEvent()
    object NavigateBack : TaskEvent()
    data class NavigateToEdit(val taskId: Long) : TaskEvent()
}
