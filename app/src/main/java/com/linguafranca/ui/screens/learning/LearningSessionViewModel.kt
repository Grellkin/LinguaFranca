package com.linguafranca.ui.screens.learning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linguafranca.domain.model.WordWithProgress
import com.linguafranca.domain.repository.LearningRepository
import com.linguafranca.domain.repository.UserRepository
import com.linguafranca.domain.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LearningSessionUiState(
    val words: List<WordWithProgress> = emptyList(),
    val currentIndex: Int = 0,
    val isAnswerRevealed: Boolean = false,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val isLoading: Boolean = true,
    val isComplete: Boolean = false
) {
    val totalWords: Int get() = words.size
}

@HiltViewModel
class LearningSessionViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val wordRepository: WordRepository,
    private val learningRepository: LearningRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearningSessionUiState())
    val uiState: StateFlow<LearningSessionUiState> = _uiState.asStateFlow()

    init {
        loadWords()
    }

    private fun loadWords() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser() ?: return@launch
            
            val words = wordRepository.getWordsForLearning(
                userId = user.id,
                limit = 20 // Session size
            ).shuffled() // Randomize order
            
            _uiState.value = _uiState.value.copy(
                words = words,
                isLoading = false
            )
        }
    }

    fun revealAnswer() {
        _uiState.value = _uiState.value.copy(isAnswerRevealed = true)
    }

    fun answerCorrect() {
        val currentWord = _uiState.value.words.getOrNull(_uiState.value.currentIndex) ?: return
        
        viewModelScope.launch {
            // SM-2 quality 4 = correct with some hesitation
            learningRepository.recordAnswer(currentWord.word.id, quality = 4)
            
            moveToNextWord(isCorrect = true)
        }
    }

    fun answerIncorrect() {
        val currentWord = _uiState.value.words.getOrNull(_uiState.value.currentIndex) ?: return
        
        viewModelScope.launch {
            // SM-2 quality 1 = incorrect but recognized
            learningRepository.recordAnswer(currentWord.word.id, quality = 1)
            
            moveToNextWord(isCorrect = false)
        }
    }

    private fun moveToNextWord(isCorrect: Boolean) {
        val nextIndex = _uiState.value.currentIndex + 1
        val isComplete = nextIndex >= _uiState.value.totalWords
        
        _uiState.value = _uiState.value.copy(
            currentIndex = nextIndex,
            isAnswerRevealed = false,
            correctCount = _uiState.value.correctCount + if (isCorrect) 1 else 0,
            incorrectCount = _uiState.value.incorrectCount + if (!isCorrect) 1 else 0,
            isComplete = isComplete
        )
    }
}

