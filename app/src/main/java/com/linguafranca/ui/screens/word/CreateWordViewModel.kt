package com.linguafranca.ui.screens.word

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linguafranca.domain.model.Word
import com.linguafranca.domain.repository.WordRepository
import com.linguafranca.domain.usecase.TranslateWordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CreateWordUiState(
    val original: String = "",
    val translation: String = "",
    val notes: String = "",
    val isTranslating: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CreateWordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val wordRepository: WordRepository,
    private val translateWordUseCase: TranslateWordUseCase
) : ViewModel() {

    private val dictionaryId: String = checkNotNull(savedStateHandle["dictionaryId"])

    private val _uiState = MutableStateFlow(CreateWordUiState())
    val uiState: StateFlow<CreateWordUiState> = _uiState.asStateFlow()

    fun updateOriginal(text: String) {
        _uiState.value = _uiState.value.copy(original = text, error = null)
    }

    fun updateTranslation(text: String) {
        _uiState.value = _uiState.value.copy(translation = text, error = null)
    }

    fun updateNotes(text: String) {
        _uiState.value = _uiState.value.copy(notes = text)
    }

    fun translateWord() {
        val original = _uiState.value.original.trim()
        if (original.isBlank()) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTranslating = true)
            
            val result = translateWordUseCase(original)
            
            _uiState.value = _uiState.value.copy(
                translation = result ?: _uiState.value.translation,
                isTranslating = false,
                error = if (result == null) "Translation failed. Please enter manually." else null
            )
        }
    }

    fun saveWord(onSaved: () -> Unit) {
        val original = _uiState.value.original.trim()
        val translation = _uiState.value.translation.trim()

        if (original.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please enter a word")
            return
        }

        if (translation.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please enter a translation")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)

            try {
                val word = Word(
                    id = UUID.randomUUID().toString(),
                    dictionaryId = dictionaryId,
                    original = original,
                    translation = translation,
                    notes = _uiState.value.notes.trim()
                )
                
                wordRepository.createWord(word)
                _uiState.value = _uiState.value.copy(isSaving = false)
                onSaved()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Failed to save word: ${e.message}"
                )
            }
        }
    }
}

