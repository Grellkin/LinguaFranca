package com.linguafranca.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linguafranca.domain.model.Dictionary
import com.linguafranca.domain.model.Word
import com.linguafranca.domain.repository.DictionaryRepository
import com.linguafranca.domain.repository.UserRepository
import com.linguafranca.domain.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val words: List<Word> = emptyList(),
    val dictionaries: List<Dictionary> = emptyList(),
    val isLoading: Boolean = false
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val wordRepository: WordRepository,
    private val dictionaryRepository: DictionaryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")
    private var userId: String? = null

    init {
        viewModelScope.launch {
            userId = userRepository.getCurrentUser()?.id

            searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isBlank()) {
                        _uiState.value = _uiState.value.copy(
                            words = emptyList(),
                            dictionaries = emptyList()
                        )
                    } else {
                        search(query)
                    }
                }
        }
    }

    fun updateQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        searchQuery.value = query
    }

    private fun search(query: String) {
        val currentUserId = userId ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Search words
            wordRepository.searchAllWords(currentUserId, query).collect { words ->
                _uiState.value = _uiState.value.copy(
                    words = words.take(10),
                    isLoading = false
                )
            }
        }

        viewModelScope.launch {
            // Search dictionaries
            dictionaryRepository.searchDictionaries(currentUserId, query).collect { dictionaries ->
                _uiState.value = _uiState.value.copy(
                    dictionaries = dictionaries.take(5)
                )
            }
        }
    }
}

