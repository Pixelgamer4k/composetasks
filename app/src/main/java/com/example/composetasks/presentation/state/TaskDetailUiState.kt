package com.example.composetasks.presentation.state

import com.example.composetasks.domain.model.Task
import java.util.Date

data class TaskDetailUiState(
    val task: Task? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class TaskDetailEvent {
    data class LoadTask(val taskId: Long) : TaskDetailEvent()
    object ToggleComplete : TaskDetailEvent()
    object DeleteTask : TaskDetailEvent()
    data class EditTask(val taskId: Long) : TaskDetailEvent()
}
