package com.example.composetasks.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composetasks.ui.theme.ComposeTasksTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(onNavigateBack: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Calendar") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Calendar View (Stub)")
            Text("Monthly/weekly task calendar with due dates would be implemented here using CalendarView or Compose calendar libs.")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    ComposeTasksTheme {
        CalendarScreen()
    }
}
