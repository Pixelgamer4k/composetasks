package com.example.composetasks.data.repository

import com.example.composetasks.data.drive.DriveSyncManager
import com.example.composetasks.domain.model.Task
import com.example.composetasks.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val driveSyncManager: DriveSyncManager? = null
) : TaskRepository {

    private val tasks = MutableStateFlow<List<Task>>(emptyList())
    private var nextId = 1L

    init {
        driveSyncManager?.startAutoSync(tasks.asStateFlow())
    }

    override fun getAllTasks(): Flow<List<Task>> = tasks.asStateFlow()

    override fun getTaskById(id: Long): Flow<Task?> = tasks.asStateFlow().map { list ->
        list.find { it.id == id }
    }

    override suspend fun insertTask(task: Task): Long {
        val newTask = task.copy(id = nextId++)
        tasks.value = tasks.value + newTask
        return newTask.id
    }

    override suspend fun updateTask(task: Task) {
        tasks.value = tasks.value.map { if (it.id == task.id) task else it }
    }

    override suspend fun deleteTask(id: Long) {
        tasks.value = tasks.value.filter { it.id != id }
    }

    override suspend fun toggleComplete(id: Long, isCompleted: Boolean) {
        tasks.value = tasks.value.map { 
            if (it.id == id) it.copy(isCompleted = isCompleted) else it 
        }
    }
}