package com.example.composetasks.domain.usecase

import com.example.composetasks.domain.repository.TaskRepository
import javax.inject.Inject

class ToggleTaskCompleteUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(id: Long, isCompleted: Boolean) = 
        repository.toggleComplete(id, isCompleted)
}