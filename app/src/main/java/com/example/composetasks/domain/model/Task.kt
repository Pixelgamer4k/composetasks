package com.example.composetasks.domain.model

import java.util.Date

enum class Priority {
    LOW, MEDIUM, HIGH
}

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDate: Date? = null,
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val createdAt: Date = Date()
)
