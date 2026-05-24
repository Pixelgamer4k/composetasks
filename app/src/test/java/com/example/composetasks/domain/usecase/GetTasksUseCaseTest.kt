package com.example.composetasks.domain.usecase

import com.example.composetasks.domain.model.Task
import com.example.composetasks.domain.repository.TaskRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetTasksUseCaseTest {

    private val repository: TaskRepository = mockk()
    private val useCase = GetTasksUseCase(repository)

    @Test
    fun `invoke returns tasks from repository`() = runTest {
        val tasks = listOf(Task(id = 1, title = "Test"))
        every { repository.getAllTasks() } returns flowOf(tasks)

        val result = useCase().first()
        assertEquals(tasks, result)
    }
}