package com.linguafranca.ui.screens.word

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linguafranca.domain.model.LearningProgress
import com.linguafranca.domain.model.Tag
import com.linguafranca.domain.model.Word
import com.linguafranca.domain.repository.LearningRepository
import com.linguafranca.domain.repository.TagRepository
import com.linguafranca.domain.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WordDetailUiState(
    val word: Word? = null,
    val tags: List<Tag> = emptyList(),
    val progress: LearningProgress? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class WordDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val wordRepository: WordRepository,
    private val tagRepository: TagRepository,
    private val learningRepository: LearningRepository
) : ViewModel() {

    private val wordId: String = checkNotNull(savedStateHandle["wordId"])

    private val _uiState = MutableStateFlow(WordDetailUiState())
    val uiState: StateFlow<WordDetailUiState> = _uiState.asStateFlow()

    init {
        loadWord()
    }

    private fun loadWord() {
        viewModelScope.launch {
            combine(
                wordRepository.observeWordById(wordId),
                tagRepository.observeTagsForWord(wordId),
                learningRepository.observeProgressByWordId(wordId)
            ) { word, tags, progress ->
                Triple(word, tags, progress)
            }.collect { (word, tags, progress) ->
                _uiState.value = _uiState.value.copy(
                    word = word,
                    tags = tags,
                    progress = progress,
                    isLoading = false
                )
            }
        }
    }
}

