package com.linguafranca.ui.screens.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.linguafranca.domain.model.Dictionary
import com.linguafranca.domain.model.DictionaryType
import com.linguafranca.domain.repository.DictionaryRepository
import com.linguafranca.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateDictionaryViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val dictionaryRepository: DictionaryRepository
) : ViewModel() {

    fun createDictionary(name: String, description: String, onCreated: () -> Unit) {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser() ?: return@launch
            
            val dictionary = Dictionary(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                name = name,
                description = description,
                type = DictionaryType.CUSTOM,
                isActive = true
            )
            
            dictionaryRepository.createDictionary(dictionary)
            onCreated()
        }
    }
}

