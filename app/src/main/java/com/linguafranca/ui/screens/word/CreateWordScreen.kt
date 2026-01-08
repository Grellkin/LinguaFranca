package com.linguafranca.ui.screens.word

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWordScreen(
    dictionaryId: String,
    onNavigateBack: () -> Unit,
    onWordCreated: () -> Unit,
    viewModel: CreateWordViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Word") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "New Word",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Add a new word to your dictionary",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = uiState.original,
                onValueChange = { viewModel.updateOriginal(it) },
                label = { Text("Word (English)") },
                placeholder = { Text("Enter the word") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = uiState.error != null && uiState.original.isBlank()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Auto-translate button
            OutlinedButton(
                onClick = { viewModel.translateWord() },
                enabled = uiState.original.isNotBlank() && !uiState.isTranslating,
                modifier = Modifier.align(Alignment.End)
            ) {
                if (uiState.isTranslating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Translating...")
                } else {
                    Icon(
                        Icons.Default.Translate,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Auto-translate")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.mainTranslation,
                onValueChange = { viewModel.updateMainTranslation(it) },
                label = { Text("Main Translation (Russian)") },
                placeholder = { Text("Enter the main translation") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = uiState.error != null && uiState.mainTranslation.isBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Additional Translations Section
            AdditionalTranslationsSection(
                translations = uiState.additionalTranslations,
                pendingTranslation = uiState.pendingAdditionalTranslation,
                onPendingTranslationChange = { viewModel.updatePendingAdditionalTranslation(it) },
                onAddTranslation = { viewModel.addAdditionalTranslation(it) },
                onRemoveTranslation = { viewModel.removeAdditionalTranslation(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Examples Section
            ExamplesSection(
                examples = uiState.examples,
                pendingPhrase = uiState.pendingExamplePhrase,
                pendingTranslation = uiState.pendingExampleTranslation,
                onPendingPhraseChange = { viewModel.updatePendingExamplePhrase(it) },
                onPendingTranslationChange = { viewModel.updatePendingExampleTranslation(it) },
                onAddExample = { phrase, translation -> viewModel.addExample(phrase, translation) },
                onRemoveExample = { viewModel.removeExample(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = { viewModel.updateNotes(it) },
                label = { Text("Notes (optional)") },
                placeholder = { Text("Add any helpful notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                shape = RoundedCornerShape(12.dp)
            )

            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.saveWord(onWordCreated) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isSaving,
                shape = RoundedCornerShape(16.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = "Save Word",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AdditionalTranslationsSection(
    translations: List<String>,
    pendingTranslation: String,
    onPendingTranslationChange: (String) -> Unit,
    onAddTranslation: (String) -> Unit,
    onRemoveTranslation: (Int) -> Unit
) {
    Column {
        Text(
            text = "Additional Translations (optional)",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Existing translations
        translations.forEachIndexed { index, translation ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = translation,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { onRemoveTranslation(index) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Add new translation
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = pendingTranslation,
                onValueChange = onPendingTranslationChange,
                placeholder = { Text("Add another translation") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (pendingTranslation.isNotBlank()) {
                        onAddTranslation(pendingTranslation)
                    }
                },
                enabled = pendingTranslation.isNotBlank()
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add translation",
                    tint = if (pendingTranslation.isNotBlank()) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ExamplesSection(
    examples: Map<String, String?>,
    pendingPhrase: String,
    pendingTranslation: String,
    onPendingPhraseChange: (String) -> Unit,
    onPendingTranslationChange: (String) -> Unit,
    onAddExample: (String, String?) -> Unit,
    onRemoveExample: (String) -> Unit
) {
    Column {
        Text(
            text = "Examples (optional)",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Existing examples
        examples.forEach { (phrase, translation) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = phrase,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        if (!translation.isNullOrBlank()) {
                            Text(
                                text = translation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(
                        onClick = { onRemoveExample(phrase) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Add new example
        Column {
            OutlinedTextField(
                value = pendingPhrase,
                onValueChange = onPendingPhraseChange,
                label = { Text("Example phrase (English)") },
                placeholder = { Text("e.g., \"The cat is sleeping\"") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = pendingTranslation,
                onValueChange = onPendingTranslationChange,
                label = { Text("Translation (optional)") },
                placeholder = { Text("e.g., \"Кот спит\"") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    if (pendingPhrase.isNotBlank()) {
                        onAddExample(pendingPhrase, pendingTranslation.ifBlank { null })
                    }
                },
                enabled = pendingPhrase.isNotBlank(),
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Example")
            }
        }
    }
}

