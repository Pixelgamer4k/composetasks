package com.example.composetasks.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composetasks.data.drive.GoogleSignInHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val signInHelper: GoogleSignInHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(isSignedIn = signInHelper.isSignedIn()) }
    }

    fun signIn() {
        viewModelScope.launch {
            val result = signInHelper.signIn()
            if (result.isSuccess) {
                _uiState.update { it.copy(isSignedIn = true) }
            }
        }
    }

    fun signOut() {
        // TODO: Implement proper sign out
        _uiState.update { it.copy(isSignedIn = false) }
    }
}

data class SettingsUiState(
    val isSignedIn: Boolean = false
)