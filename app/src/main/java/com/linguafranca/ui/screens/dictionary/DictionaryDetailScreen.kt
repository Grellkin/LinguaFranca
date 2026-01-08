package com.linguafranca.ui.screens.dictionary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.linguafranca.domain.model.DictionaryType
import com.linguafranca.domain.model.Word
import com.linguafranca.ui.theme.LinguaFrancaColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryDetailScreen(
    dictionaryId: String,
    onNavigateBack: () -> Unit,
    onNavigateToWord: (String) -> Unit,
    onNavigateToCreateWord: () -> Unit,
    viewModel: DictionaryDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var isSearchVisible by remember { mutableStateOf(false) }

    val dictionary = uiState.dictionary

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchVisible) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            placeholder = { Text("Search words...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        )
                    } else {
                        Text(
                            text = dictionary?.name ?: "Dictionary",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isSearchVisible) {
                            isSearchVisible = false
                            viewModel.updateSearchQuery("")
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(
                            imageVector = if (isSearchVisible) Icons.Default.Close 
                                         else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isSearchVisible) "Close search" else "Back"
                        )
                    }
                },
                actions = {
                    if (!isSearchVisible) {
                        IconButton(onClick = { isSearchVisible = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                    
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { 
                                    Text(
                                        if (dictionary?.isActive == true) "Exclude from learning" 
                                        else "Include in learning"
                                    ) 
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (dictionary?.isActive == true) 
                                            Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                },
                                onClick = {
                                    viewModel.toggleDictionaryActive()
                                    showMenu = false
                                }
                            )
                            
                            if (dictionary?.type == DictionaryType.CUSTOM) {
                                DropdownMenuItem(
                                    text = { Text("Delete dictionary") },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (dictionary?.type == DictionaryType.CUSTOM) {
                FloatingActionButton(
                    onClick = onNavigateToCreateWord,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add word")
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Dictionary info card
                item {
                    DictionaryInfoCard(
                        dictionary = dictionary,
                        progress = uiState.progress,
                        wordCount = uiState.words.size
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Words (${uiState.filteredWords.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (uiState.filteredWords.isEmpty()) {
                    item {
                        EmptyWordsCard(
                            isSearching = uiState.searchQuery.isNotBlank(),
                            isCustomDictionary = dictionary?.type == DictionaryType.CUSTOM,
                            onAddWord = onNavigateToCreateWord
                        )
                    }
                } else {
                    items(
                        items = uiState.filteredWords,
                        key = { it.id }
                    ) { word ->
                        WordCard(
                            word = word,
                            onClick = { onNavigateToWord(word.id) },
                            onDelete = if (dictionary?.type == DictionaryType.CUSTOM) {
                                { viewModel.deleteWord(word.id) }
                            } else null
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Dictionary?") },
            text = { 
                Text("This will permanently delete \"${dictionary?.name}\" and all its words. This action cannot be undone.") 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteDictionary(onNavigateBack)
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DictionaryInfoCard(
    dictionary: com.linguafranca.domain.model.Dictionary?,
    progress: com.linguafranca.domain.model.DictionaryProgress?,
    wordCount: Int
) {
    val typeColor = when (dictionary?.type) {
        DictionaryType.CUSTOM -> MaterialTheme.colorScheme.primary
        DictionaryType.FRANCO -> LinguaFrancaColors.TropicalOrange
        DictionaryType.COMMUNITY -> LinguaFrancaColors.TropicalBlue
        null -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = typeColor.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(typeColor)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = when (dictionary?.type) {
                        DictionaryType.CUSTOM -> "Custom Dictionary"
                        DictionaryType.FRANCO -> "Franco's Dictionary"
                        DictionaryType.COMMUNITY -> "Community Dictionary"
                        null -> "Dictionary"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = typeColor
                )
                
                if (dictionary?.isActive == false) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(Excluded)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!dictionary?.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = dictionary?.description ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "$wordCount",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = typeColor
                    )
                    Text(
                        text = "Total words",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${progress?.learnedWords ?: 0}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = typeColor
                    )
                    Text(
                        text = "Learned",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { (progress?.progressPercent ?: 0f) / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = typeColor,
                trackColor = typeColor.copy(alpha = 0.2f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WordCard(
    word: Word,
    onClick: () -> Unit,
    onDelete: (() -> Unit)?
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart && onDelete != null) {
                onDelete()
                true
            } else false
        }
    )

    if (onDelete != null) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            MaterialTheme.colorScheme.error,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onError
                    )
                }
            },
            enableDismissFromStartToEnd = false
        ) {
            WordCardContent(word = word, onClick = onClick)
        }
    } else {
        WordCardContent(word = word, onClick = onClick)
    }
}

@Composable
private fun WordCardContent(
    word: Word,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = word.original,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = word.mainTranslation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyWordsCard(
    isSearching: Boolean,
    isCustomDictionary: Boolean,
    onAddWord: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isSearching) "No matching words" else "No words yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = if (isSearching) "Try a different search term"
                       else if (isCustomDictionary) "Add your first word to get started!"
                       else "This dictionary has no words",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
            
            if (!isSearching && isCustomDictionary) {
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onAddWord) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Word")
                }
            }
        }
    }
}

