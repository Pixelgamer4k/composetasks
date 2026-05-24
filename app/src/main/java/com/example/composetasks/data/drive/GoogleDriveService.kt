package com.example.composetasks.data.drive

import android.content.Context
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class GoogleDriveService(
    private val context: Context,
    private val credential: GoogleAccountCredential
) {
    private val drive: Drive by lazy {
        Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName("ComposeTasks")
            .build()
    }

    suspend fun saveTasksJson(json: String, fileName: String = "composetasks_backup.json"): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Find or create app folder
            val folderId = getOrCreateAppFolder()
            
            // Delete existing file if present
            deleteExistingFile(folderId, fileName)
            
            // Create new file
            val fileMetadata = com.google.api.services.drive.model.File().apply {
                name = fileName
                parents = listOf(folderId)
                mimeType = "application/json"
            }
            
            val mediaContent = com.google.api.client.http.ByteArrayContent.fromString("application/json", json)
            val file = drive.files().create(fileMetadata, mediaContent).execute()
            
            Result.success(file.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getOrCreateAppFolder(): String {
        val query = "name='ComposeTasks' and mimeType='application/vnd.google-apps.folder' and trashed=false"
        val result = drive.files().list()
            .setQ(query)
            .setSpaces("drive")
            .execute()
        
        return if (result.files.isNotEmpty()) {
            result.files[0].id
        } else {
            val folderMetadata = com.google.api.services.drive.model.File().apply {
                name = "ComposeTasks"
                mimeType = "application/vnd.google-apps.folder"
            }
            drive.files().create(folderMetadata).execute().id
        }
    }

    private fun deleteExistingFile(folderId: String, fileName: String) {
        val query = "name='$fileName' and '$folderId' in parents and trashed=false"
        val result = drive.files().list().setQ(query).execute()
        result.files.forEach { drive.files().delete(it.id).execute() }
    }
}