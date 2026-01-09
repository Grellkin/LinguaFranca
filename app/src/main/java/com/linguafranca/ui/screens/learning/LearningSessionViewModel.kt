package com.linguafranca.ui.screens.learning

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linguafranca.domain.model.LearningSessionType
import com.linguafranca.domain.model.WordWithProgress
import com.linguafranca.domain.repository.LearningRepository
import com.linguafranca.domain.repository.UserRepository
import com.linguafranca.domain.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    val isComplete: Boolean = false,
    val sessionType: LearningSessionType = LearningSessionType.FLASH_CARDS,
    val userInput: String = "",
    val isInputCorrect: Boolean? = null, // null = not checked yet, true = correct, false = incorrect
    val showCorrectAnswer: Boolean = false // For showing correct answer after skip
) {
    val totalWords: Int get() = words.size
}

@HiltViewModel
class LearningSessionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val wordRepository: WordRepository,
    private val learningRepository: LearningRepository
) : ViewModel() {

    private val sessionType: LearningSessionType = savedStateHandle.get<String>("sessionType")
        ?.let { LearningSessionType.valueOf(it) }
        ?: LearningSessionType.FLASH_CARDS

    private val _uiState = MutableStateFlow(LearningSessionUiState(sessionType = sessionType))
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

    fun updateUserInput(input: String) {
        val currentWord = _uiState.value.words.getOrNull(_uiState.value.currentIndex) ?: return
        
        val isCorrect = when (_uiState.value.sessionType) {
            LearningSessionType.WRITE_WORD -> {
                // Check if input matches the English word (case-insensitive, trimmed)
                input.trim().equals(currentWord.word.original.trim(), ignoreCase = true)
            }
            LearningSessionType.WRITE_TRANSLATION -> {
                // Check if input matches main translation OR any additional translation
                val allTranslations = listOf(currentWord.word.mainTranslation) + 
                                      currentWord.word.additionalTranslations
                allTranslations.any { it.trim().equals(input.trim(), ignoreCase = true) }
            }
            else -> null
        }
        
        _uiState.value = _uiState.value.copy(
            userInput = input,
            isInputCorrect = if (input.isBlank()) null else isCorrect
        )
        
        // Auto-advance when correct
        if (isCorrect == true) {
            viewModelScope.launch {
                delay(600) // Brief pause to show success
                answerCorrectInternal()
            }
        }
    }

    fun answerCorrect() {
        answerCorrectInternal()
    }

    private fun answerCorrectInternal() {
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

    fun skipWord() {
        // Show correct answer first, then mark as incorrect
        _uiState.value = _uiState.value.copy(showCorrectAnswer = true)
        
        viewModelScope.launch {
            delay(1500) // Show correct answer for 1.5 seconds
            answerIncorrect()
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
            isComplete = isComplete,
            userInput = "", // Reset input for next word
            isInputCorrect = null,
            showCorrectAnswer = false
        )
    }
}
