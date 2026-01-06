package com.linguafranca.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linguafranca.domain.model.Dictionary
import com.linguafranca.domain.model.DictionaryType
import com.linguafranca.domain.model.User
import com.linguafranca.domain.model.UserSettings
import com.linguafranca.domain.model.Word
import com.linguafranca.domain.repository.DictionaryRepository
import com.linguafranca.domain.repository.UserRepository
import com.linguafranca.domain.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class SetupUiState(
    val displayName: String = "",
    val targetLanguage: String = "en",
    val nativeLanguage: String = "ru",
    val darkTheme: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dictionaryRepository: DictionaryRepository,
    private val wordRepository: WordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    fun updateDisplayName(name: String) {
        _uiState.value = _uiState.value.copy(displayName = name)
    }

    fun updateDarkTheme(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(darkTheme = enabled)
    }

    fun completeSetup(onComplete: () -> Unit) {
        if (_uiState.value.displayName.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please enter your name")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val userId = UUID.randomUUID().toString()
                
                // Create user
                val user = User(
                    id = userId,
                    email = "", // Local-first, no email needed
                    displayName = _uiState.value.displayName
                )
                userRepository.createUser(user)
                
                // Create user settings
                val settings = UserSettings(
                    userId = userId,
                    targetLanguage = _uiState.value.targetLanguage,
                    nativeLanguage = _uiState.value.nativeLanguage,
                    darkTheme = _uiState.value.darkTheme
                )
                userRepository.saveUserSettings(settings)
                
                // Create Franco's default dictionaries with sample words
                createFrancoDictionaries(userId)
                
                _uiState.value = _uiState.value.copy(isLoading = false)
                onComplete()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to create profile: ${e.message}"
                )
            }
        }
    }

    private suspend fun createFrancoDictionaries(userId: String) {
        // Animals dictionary
        val animalsDictId = UUID.randomUUID().toString()
        dictionaryRepository.createDictionary(
            Dictionary(
                id = animalsDictId,
                userId = userId,
                name = "Animals",
                description = "Common animal names",
                type = DictionaryType.FRANCO,
                isActive = true
            )
        )
        
        val animals = listOf(
            "dog" to "собака",
            "cat" to "кошка",
            "bird" to "птица",
            "fish" to "рыба",
            "horse" to "лошадь",
            "elephant" to "слон",
            "lion" to "лев",
            "bear" to "медведь",
            "wolf" to "волк",
            "rabbit" to "кролик"
        )
        
        animals.forEach { (original, translation) ->
            wordRepository.createWord(
                Word(
                    id = UUID.randomUUID().toString(),
                    dictionaryId = animalsDictId,
                    original = original,
                    translation = translation
                )
            )
        }
        
        // Food dictionary
        val foodDictId = UUID.randomUUID().toString()
        dictionaryRepository.createDictionary(
            Dictionary(
                id = foodDictId,
                userId = userId,
                name = "Food & Drinks",
                description = "Common food and beverage words",
                type = DictionaryType.FRANCO,
                isActive = true
            )
        )
        
        val foods = listOf(
            "water" to "вода",
            "bread" to "хлеб",
            "milk" to "молоко",
            "apple" to "яблоко",
            "cheese" to "сыр",
            "coffee" to "кофе",
            "tea" to "чай",
            "meat" to "мясо",
            "rice" to "рис",
            "egg" to "яйцо"
        )
        
        foods.forEach { (original, translation) ->
            wordRepository.createWord(
                Word(
                    id = UUID.randomUUID().toString(),
                    dictionaryId = foodDictId,
                    original = original,
                    translation = translation
                )
            )
        }
        
        // Basic phrases dictionary
        val phrasesDictId = UUID.randomUUID().toString()
        dictionaryRepository.createDictionary(
            Dictionary(
                id = phrasesDictId,
                userId = userId,
                name = "Basic Phrases",
                description = "Essential everyday phrases",
                type = DictionaryType.FRANCO,
                isActive = true
            )
        )
        
        val phrases = listOf(
            "hello" to "привет",
            "goodbye" to "до свидания",
            "thank you" to "спасибо",
            "please" to "пожалуйста",
            "yes" to "да",
            "no" to "нет",
            "excuse me" to "извините",
            "good morning" to "доброе утро",
            "good night" to "спокойной ночи",
            "how are you" to "как дела"
        )
        
        phrases.forEach { (original, translation) ->
            wordRepository.createWord(
                Word(
                    id = UUID.randomUUID().toString(),
                    dictionaryId = phrasesDictId,
                    original = original,
                    translation = translation
                )
            )
        }
    }
}

