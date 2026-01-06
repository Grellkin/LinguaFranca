package com.linguafranca.ui.screens.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linguafranca.domain.model.DictionaryWithProgress
import com.linguafranca.domain.repository.DictionaryRepository
import com.linguafranca.domain.repository.UserRepository
import com.linguafranca.domain.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LearningUiState(
    val dictionaries: List<DictionaryWithProgress> = emptyList(),
    val selectedDictionaries: Set<String> = emptySet(),
    val wordsToReview: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class LearningViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dictionaryRepository: DictionaryRepository,
    private val wordRepository: WordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearningUiState())
    val uiState: StateFlow<LearningUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser() ?: return@launch
            
            dictionaryRepository.observeDictionariesWithProgress(user.id).collect { dictionaries ->
                val activeDictionaries = dictionaries.filter { it.dictionary.isActive }
                val selectedIds = _uiState.value.selectedDictionaries.ifEmpty {
                    activeDictionaries.map { it.dictionary.id }.toSet()
                }
                
                // Calculate words to review
                val wordsToReview = activeDictionaries
                    .filter { selectedIds.contains(it.dictionary.id) }
                    .sumOf { it.progress.totalWords - it.progress.learnedWords }
                    .coerceAtLeast(0)
                
                _uiState.value = _uiState.value.copy(
                    dictionaries = activeDictionaries,
                    selectedDictionaries = selectedIds.intersect(activeDictionaries.map { it.dictionary.id }.toSet()),
                    wordsToReview = wordsToReview,
                    isLoading = false
                )
            }
        }
    }

    fun toggleDictionary(dictionaryId: String) {
        val current = _uiState.value.selectedDictionaries
        val updated = if (current.contains(dictionaryId)) {
            current - dictionaryId
        } else {
            current + dictionaryId
        }
        
        val wordsToReview = _uiState.value.dictionaries
            .filter { updated.contains(it.dictionary.id) }
            .sumOf { it.progress.totalWords - it.progress.learnedWords }
            .coerceAtLeast(0)
        
        _uiState.value = _uiState.value.copy(
            selectedDictionaries = updated,
            wordsToReview = wordsToReview
        )
    }
}

