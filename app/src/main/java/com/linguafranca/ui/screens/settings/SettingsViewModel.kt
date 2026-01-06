package com.linguafranca.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linguafranca.domain.model.UserSettings
import com.linguafranca.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val darkTheme: Boolean = false,
    val targetLanguage: String = "en",
    val nativeLanguage: String = "ru"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private var userId: String? = null

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser() ?: return@launch
            userId = user.id
            
            userRepository.observeUserSettings(user.id).collect { settings ->
                settings?.let {
                    _uiState.value = _uiState.value.copy(
                        darkTheme = it.darkTheme,
                        targetLanguage = it.targetLanguage,
                        nativeLanguage = it.nativeLanguage
                    )
                }
            }
        }
    }

    fun toggleDarkTheme() {
        viewModelScope.launch {
            userId?.let { id ->
                val currentSettings = userRepository.getUserSettings(id)
                val newSettings = currentSettings?.copy(
                    darkTheme = !currentSettings.darkTheme
                ) ?: UserSettings(
                    userId = id,
                    darkTheme = !_uiState.value.darkTheme
                )
                userRepository.saveUserSettings(newSettings)
            }
        }
    }
}

