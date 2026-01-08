package com.linguafranca.ui.screens.dictionary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linguafranca.domain.model.Dictionary
import com.linguafranca.domain.model.DictionaryProgress
import com.linguafranca.domain.model.Word
import com.linguafranca.domain.repository.DictionaryRepository
import com.linguafranca.domain.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DictionaryDetailUiState(
    val dictionary: Dictionary? = null,
    val words: List<Word> = emptyList(),
    val progress: DictionaryProgress? = null,
    val searchQuery: String = "",
    val filteredWords: List<Word> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class DictionaryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dictionaryRepository: DictionaryRepository,
    private val wordRepository: WordRepository
) : ViewModel() {

    private val dictionaryId: String = checkNotNull(savedStateHandle["dictionaryId"])

    private val _uiState = MutableStateFlow(DictionaryDetailUiState())
    val uiState: StateFlow<DictionaryDetailUiState> = _uiState.asStateFlow()

    init {
        loadDictionary()
    }

    private fun loadDictionary() {
        viewModelScope.launch {
            combine(
                dictionaryRepository.observeDictionaryById(dictionaryId),
                wordRepository.observeWordsByDictionary(dictionaryId),
                dictionaryRepository.observeDictionaryProgress(dictionaryId)
            ) { dictionary, words, progress ->
                Triple(dictionary, words, progress)
            }.collect { (dictionary, words, progress) ->
                val query = _uiState.value.searchQuery
                _uiState.value = _uiState.value.copy(
                    dictionary = dictionary,
                    words = words,
                    progress = progress,
                    filteredWords = if (query.isBlank()) words else filterWords(words, query),
                    isLoading = false
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredWords = if (query.isBlank()) _uiState.value.words 
                           else filterWords(_uiState.value.words, query)
        )
    }

    private fun filterWords(words: List<Word>, query: String): List<Word> {
        return words.filter { word ->
            word.original.contains(query, ignoreCase = true) ||
            word.mainTranslation.contains(query, ignoreCase = true) ||
            word.additionalTranslations.any { it.contains(query, ignoreCase = true) }
        }
    }

    fun deleteWord(wordId: String) {
        viewModelScope.launch {
            wordRepository.deleteWord(wordId)
        }
    }

    fun toggleDictionaryActive() {
        viewModelScope.launch {
            _uiState.value.dictionary?.let { dict ->
                dictionaryRepository.setDictionaryActive(dict.id, !dict.isActive)
            }
        }
    }

    fun deleteDictionary(onDeleted: () -> Unit) {
        viewModelScope.launch {
            dictionaryRepository.deleteDictionary(dictionaryId)
            onDeleted()
        }
    }
}

