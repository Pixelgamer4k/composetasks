package com.example.composetasks.domain.usecase

import com.example.composetasks.domain.model.Task
import com.example.composetasks.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTaskByIdUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(id: Long): Flow<Task?> = repository.getTaskById(id)
}