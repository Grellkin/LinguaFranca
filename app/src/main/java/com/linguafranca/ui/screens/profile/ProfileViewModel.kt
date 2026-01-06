package com.linguafranca.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linguafranca.domain.model.User
import com.linguafranca.domain.repository.DictionaryRepository
import com.linguafranca.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val totalWords: Int = 0,
    val learnedWords: Int = 0,
    val dictionaryCount: Int = 0,
    val overallProgress: Float = 0f
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dictionaryRepository: DictionaryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            userRepository.observeCurrentUser().collect { user ->
                _uiState.value = _uiState.value.copy(user = user)
                
                user?.let { loadStats(it.id) }
            }
        }
    }

    private fun loadStats(userId: String) {
        viewModelScope.launch {
            dictionaryRepository.observeDictionariesWithProgress(userId).collect { dictionaries ->
                val totalWords = dictionaries.sumOf { it.progress.totalWords }
                val learnedWords = dictionaries.sumOf { it.progress.learnedWords }
                val overallProgress = if (totalWords > 0) {
                    (learnedWords.toFloat() / totalWords) * 100
                } else 0f

                _uiState.value = _uiState.value.copy(
                    totalWords = totalWords,
                    learnedWords = learnedWords,
                    dictionaryCount = dictionaries.size,
                    overallProgress = overallProgress
                )
            }
        }
    }
}

