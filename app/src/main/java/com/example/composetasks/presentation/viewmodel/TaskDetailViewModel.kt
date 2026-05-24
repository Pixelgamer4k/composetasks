package com.example.composetasks.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetasks.domain.usecase.DeleteTaskUseCase
import com.example.composetasks.domain.usecase.GetTaskByIdUseCase
import com.example.composetasks.domain.usecase.ToggleTaskCompleteUseCase
import com.example.composetasks.domain.usecase.UpdateTaskUseCase
import com.example.composetasks.presentation.state.TaskDetailEvent
import com.example.composetasks.presentation.state.TaskDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val toggleTaskCompleteUseCase: ToggleTaskCompleteUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskDetailUiState(isLoading = true))
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TaskDetailEvent>()
    val events = _events.asSharedFlow()

    fun onEvent(event: TaskDetailEvent) {
        when (event) {
            is TaskDetailEvent.LoadTask -> loadTask(event.taskId)
            TaskDetailEvent.ToggleComplete -> toggleComplete()
            TaskDetailEvent.DeleteTask -> deleteTask()
            is TaskDetailEvent.EditTask -> {
                viewModelScope.launch {
                    _events.emit(event) // or handle navigation in UI
                }
            }
        }
    }

    private fun loadTask(taskId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getTaskByIdUseCase(taskId)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collectLatest { task ->
                    _uiState.update { 
                        it.copy(
                            task = task, 
                            isLoading = false,
                            error = if (task == null) "Task not found" else null
                        ) 
                    }
                }
        }
    }

    private fun toggleComplete() {
        val currentTask = _uiState.value.task ?: return
        viewModelScope.launch {
            toggleTaskCompleteUseCase(currentTask.id, !currentTask.isCompleted)
            // Reload to reflect change
            loadTask(currentTask.id)
        }
    }

    private fun deleteTask() {
        val id = _uiState.value.task?.id ?: return
        viewModelScope.launch {
            deleteTaskUseCase(id)
            _events.emit(TaskDetailEvent.DeleteTask) // signal UI to navigate back
        }
    }
}
