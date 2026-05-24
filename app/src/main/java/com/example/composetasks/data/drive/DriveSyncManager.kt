package com.example.composetasks.data.drive

import com.example.composetasks.domain.model.Task
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DriveSyncManager @Inject constructor(
    private val googleDriveService: GoogleDriveService
) {
    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO)

    fun startAutoSync(taskFlow: kotlinx.coroutines.flow.Flow<List<Task>>) {
        scope.launch {
            taskFlow.collectLatest { tasks ->
                val json = gson.toJson(tasks)
                googleDriveService.saveTasksJson(json)
            }
        }
    }
}