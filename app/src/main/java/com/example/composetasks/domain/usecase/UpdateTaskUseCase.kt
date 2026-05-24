package com.example.composetasks.domain.usecase

import com.example.composetasks.domain.model.Task
import com.example.composetasks.domain.repository.TaskRepository
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task) = repository.updateTask(task)
}