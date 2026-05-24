package com.example.composetasks.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    fun formatDate(date: Date?): String {
        return date?.let { dateFormat.format(it) } ?: "No due date"
    }

    fun formatDateTime(date: Date?): String {
        return date?.let { "${dateFormat.format(it)} ${timeFormat.format(it)}" } ?: "No due date"
    }
}