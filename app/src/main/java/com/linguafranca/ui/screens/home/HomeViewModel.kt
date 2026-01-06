package com.linguafranca.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linguafranca.domain.model.DictionaryWithProgress
import com.linguafranca.domain.model.User
import com.linguafranca.domain.repository.DictionaryRepository
import com.linguafranca.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val user: User? = null,
    val dictionaries: List<DictionaryWithProgress> = emptyList(),
    val totalWordsLearned: Int = 0,
    val totalWords: Int = 0,
    val overallProgress: Float = 0f,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dictionaryRepository: DictionaryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val hasUser: StateFlow<Boolean> = userRepository.observeCurrentUser()
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        viewModelScope.launch {
            userRepository.observeCurrentUser().collect { user ->
                _uiState.value = _uiState.value.copy(user = user, isLoading = user == null)
                
                if (user != null) {
                    observeDictionaries(user.id)
                }
            }
        }
    }

    private fun observeDictionaries(userId: String) {
        viewModelScope.launch {
            dictionaryRepository.observeDictionariesWithProgress(userId).collect { dictionaries ->
                val totalWords = dictionaries.sumOf { it.progress.totalWords }
                val totalLearned = dictionaries.sumOf { it.progress.learnedWords }
                val overallProgress = if (totalWords > 0) {
                    (totalLearned.toFloat() / totalWords) * 100
                } else 0f
                
                _uiState.value = _uiState.value.copy(
                    dictionaries = dictionaries,
                    totalWords = totalWords,
                    totalWordsLearned = totalLearned,
                    overallProgress = overallProgress,
                    isLoading = false
                )
            }
        }
    }
}

