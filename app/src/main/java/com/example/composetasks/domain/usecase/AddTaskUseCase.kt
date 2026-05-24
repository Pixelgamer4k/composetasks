package com.example.composetasks.domain.usecase

import com.example.composetasks.domain.model.Task
import com.example.composetasks.domain.repository.TaskRepository
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Long = repository.insertTask(task)
}