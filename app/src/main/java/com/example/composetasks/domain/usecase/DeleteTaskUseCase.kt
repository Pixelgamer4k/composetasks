package com.example.composetasks.domain.usecase

import com.example.composetasks.domain.repository.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(id: Long) = repository.deleteTask(id)
}