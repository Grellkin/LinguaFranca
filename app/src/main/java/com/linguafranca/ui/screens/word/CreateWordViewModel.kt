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
    val mainTranslation: String = "",
    val additionalTranslations: List<String> = emptyList(),
    val pendingAdditionalTranslation: String = "",
    val examples: Map<String, String?> = emptyMap(),
    val pendingExamplePhrase: String = "",
    val pendingExampleTranslation: String = "",
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

    fun updateMainTranslation(text: String) {
        _uiState.value = _uiState.value.copy(mainTranslation = text, error = null)
    }

    fun updateAdditionalTranslations(translations: List<String>) {
        _uiState.value = _uiState.value.copy(additionalTranslations = translations)
    }

    fun updatePendingAdditionalTranslation(text: String) {
        _uiState.value = _uiState.value.copy(pendingAdditionalTranslation = text)
    }

    fun addAdditionalTranslation(translation: String) {
        if (translation.isNotBlank()) {
            val current = _uiState.value.additionalTranslations
            _uiState.value = _uiState.value.copy(
                additionalTranslations = current + translation.trim(),
                pendingAdditionalTranslation = ""
            )
        }
    }

    fun removeAdditionalTranslation(index: Int) {
        val current = _uiState.value.additionalTranslations.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _uiState.value = _uiState.value.copy(additionalTranslations = current)
        }
    }

    fun updateExamples(examples: Map<String, String?>) {
        _uiState.value = _uiState.value.copy(examples = examples)
    }

    fun updatePendingExamplePhrase(text: String) {
        _uiState.value = _uiState.value.copy(pendingExamplePhrase = text)
    }

    fun updatePendingExampleTranslation(text: String) {
        _uiState.value = _uiState.value.copy(pendingExampleTranslation = text)
    }

    fun addExample(phrase: String, translation: String?) {
        if (phrase.isNotBlank()) {
            val current = _uiState.value.examples.toMutableMap()
            current[phrase.trim()] = translation?.trim()?.ifBlank { null }
            _uiState.value = _uiState.value.copy(
                examples = current,
                pendingExamplePhrase = "",
                pendingExampleTranslation = ""
            )
        }
    }

    fun removeExample(phrase: String) {
        val current = _uiState.value.examples.toMutableMap()
        current.remove(phrase)
        _uiState.value = _uiState.value.copy(examples = current)
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
                mainTranslation = result ?: _uiState.value.mainTranslation,
                isTranslating = false,
                error = if (result == null) "Translation failed. Please enter manually." else null
            )
        }
    }

    fun saveWord(onSaved: () -> Unit) {
        val original = _uiState.value.original.trim()
        val mainTranslation = _uiState.value.mainTranslation.trim()

        if (original.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please enter a word")
            return
        }

        if (mainTranslation.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please enter a translation")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)

            try {
                // Include any pending additional translation that wasn't explicitly added
                val allAdditionalTranslations = _uiState.value.additionalTranslations.toMutableList()
                val pendingTranslation = _uiState.value.pendingAdditionalTranslation.trim()
                if (pendingTranslation.isNotBlank()) {
                    allAdditionalTranslations.add(pendingTranslation)
                }

                // Include any pending example that wasn't explicitly added
                val allExamples = _uiState.value.examples.toMutableMap()
                val pendingPhrase = _uiState.value.pendingExamplePhrase.trim()
                if (pendingPhrase.isNotBlank()) {
                    val pendingExampleTrans = _uiState.value.pendingExampleTranslation.trim().ifBlank { null }
                    allExamples[pendingPhrase] = pendingExampleTrans
                }

                val word = Word(
                    id = UUID.randomUUID().toString(),
                    dictionaryId = dictionaryId,
                    original = original,
                    mainTranslation = mainTranslation,
                    additionalTranslations = allAdditionalTranslations.filter { it.isNotBlank() },
                    examples = allExamples.filterKeys { it.isNotBlank() },
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

