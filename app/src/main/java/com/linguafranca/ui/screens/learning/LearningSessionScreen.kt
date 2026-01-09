package com.linguafranca.ui.screens.learning

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.linguafranca.domain.model.LearningSessionType
import com.linguafranca.ui.theme.LinguaFrancaColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningSessionScreen(
    sessionType: LearningSessionType,
    onSessionComplete: (correctCount: Int, totalCount: Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: LearningSessionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Check if session is complete
    if (uiState.isComplete) {
        onSessionComplete(uiState.correctCount, uiState.totalWords)
        return
    }

    val progressAnim by animateFloatAsState(
        targetValue = if (uiState.totalWords > 0) {
            uiState.currentIndex.toFloat() / uiState.totalWords
        } else 0f,
        animationSpec = tween(300),
        label = "progress"
    )

    val sessionTitle = when (sessionType) {
        LearningSessionType.FLASH_CARDS -> "Flash Cards"
        LearningSessionType.WRITE_WORD -> "Write the Word"
        LearningSessionType.WRITE_TRANSLATION -> "Write Translation"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text(sessionTitle, style = MaterialTheme.typography.labelMedium)
                        Text("${uiState.currentIndex + 1} / ${uiState.totalWords}")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading words...")
                }
            }
        } else if (uiState.words.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Text(
                        text = "No words to review!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "All caught up! Add more words or check back later.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onNavigateBack) {
                        Text("Go Back")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Progress bar
                LinearProgressIndicator(
                    progress = { progressAnim },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                
                // Score display
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ScoreChip(
                        label = "Correct",
                        count = uiState.correctCount,
                        color = LinguaFrancaColors.ParrotGreen
                    )
                    ScoreChip(
                        label = "Incorrect",
                        count = uiState.incorrectCount,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Word card based on session type
                val currentWord = uiState.words.getOrNull(uiState.currentIndex)
                
                if (currentWord != null) {
                    AnimatedContent(
                        targetState = uiState.currentIndex,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(300)) + 
                             slideInHorizontally { width -> width }) togetherWith
                            (fadeOut(animationSpec = tween(300)) + 
                             slideOutHorizontally { width -> -width })
                        },
                        label = "wordCard"
                    ) { targetIndex ->
                        val wordAtThisState = uiState.words[targetIndex]

                        when (sessionType) {
                            LearningSessionType.FLASH_CARDS -> {
                                FlashCard(
                                    word = wordAtThisState.word.original,
                                    translation = wordAtThisState.word.mainTranslation,
                                    additionalTranslations = wordAtThisState.word.additionalTranslations,
                                    isRevealed = uiState.isAnswerRevealed,
                                    onReveal = { viewModel.revealAnswer() },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            LearningSessionType.WRITE_WORD -> {
                                WriteWordCard(
                                    translation = wordAtThisState.word.mainTranslation,
                                    additionalTranslations = wordAtThisState.word.additionalTranslations,
                                    correctWord = wordAtThisState.word.original,
                                    userInput = uiState.userInput,
                                    isCorrect = uiState.isInputCorrect,
                                    showCorrectAnswer = uiState.showCorrectAnswer,
                                    onInputChange = viewModel::updateUserInput,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            LearningSessionType.WRITE_TRANSLATION -> {
                                WriteTranslationCard(
                                    word = wordAtThisState.word.original,
                                    correctTranslation = wordAtThisState.word.mainTranslation,
                                    additionalTranslations = wordAtThisState.word.additionalTranslations,
                                    userInput = uiState.userInput,
                                    isCorrect = uiState.isInputCorrect,
                                    showCorrectAnswer = uiState.showCorrectAnswer,
                                    onInputChange = viewModel::updateUserInput,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action buttons based on session type
                    when (sessionType) {
                        LearningSessionType.FLASH_CARDS -> {
                            FlashCardButtons(
                                isAnswerRevealed = uiState.isAnswerRevealed,
                                onReveal = viewModel::revealAnswer,
                                onCorrect = viewModel::answerCorrect,
                                onIncorrect = viewModel::answerIncorrect
                            )
                        }
                        LearningSessionType.WRITE_WORD,
                        LearningSessionType.WRITE_TRANSLATION -> {
                            WriteSessionButtons(
                                isCorrect = uiState.isInputCorrect,
                                showCorrectAnswer = uiState.showCorrectAnswer,
                                onSkip = viewModel::skipWord
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FlashCardButtons(
    isAnswerRevealed: Boolean,
    onReveal: () -> Unit,
    onCorrect: () -> Unit,
    onIncorrect: () -> Unit
) {
    if (isAnswerRevealed) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onIncorrect,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Close, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text("Forgot", fontWeight = FontWeight.SemiBold)
            }
            
            Button(
                onClick = onCorrect,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text("Got it!", fontWeight = FontWeight.SemiBold)
            }
        }
    } else {
        Button(
            onClick = onReveal,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Visibility, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text("Show Answer", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun WriteSessionButtons(
    isCorrect: Boolean?,
    showCorrectAnswer: Boolean,
    onSkip: () -> Unit
) {
    if (!showCorrectAnswer && isCorrect != true) {
        TextButton(
            onClick = onSkip,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.SkipNext, contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
            Text("Skip (Don't know)", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun FlashCard(
    word: String,
    translation: String,
    additionalTranslations: List<String> = emptyList(),
    isRevealed: Boolean,
    onReveal: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = word,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center
            )
            
            if (isRevealed) {
                Spacer(modifier = Modifier.height(24.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                            RoundedCornerShape(1.dp)
                        )
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = translation,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
                
                if (additionalTranslations.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = additionalTranslations.joinToString(", "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun WriteWordCard(
    translation: String,
    additionalTranslations: List<String>,
    correctWord: String,
    userInput: String,
    isCorrect: Boolean?,
    showCorrectAnswer: Boolean,
    onInputChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    
    val borderColor by animateColorAsState(
        targetValue = when {
            showCorrectAnswer -> MaterialTheme.colorScheme.error
            isCorrect == true -> LinguaFrancaColors.ParrotGreen
            isCorrect == false -> MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
            else -> MaterialTheme.colorScheme.outline
        },
        label = "borderColor"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = LinguaFrancaColors.TropicalBlue.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Type the word:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Show translation
            Text(
                text = translation,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = LinguaFrancaColors.TropicalBlue,
                textAlign = TextAlign.Center
            )
            
            if (additionalTranslations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = additionalTranslations.joinToString(", "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Show correct answer if skipped
            if (showCorrectAnswer) {
                Text(
                    text = "Correct answer:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = correctWord,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                // Text input
                OutlinedTextField(
                    value = userInput,
                    onValueChange = onInputChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    placeholder = { Text("Type here...") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = borderColor,
                        unfocusedBorderColor = borderColor
                    ),
                    trailingIcon = {
                        if (isCorrect == true) {
                            Icon(
                                Icons.Default.Check, 
                                contentDescription = "Correct",
                                tint = LinguaFrancaColors.ParrotGreen
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp)
                )
                
                if (isCorrect == true) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Correct! 🎉",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = LinguaFrancaColors.ParrotGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun WriteTranslationCard(
    word: String,
    correctTranslation: String,
    additionalTranslations: List<String>,
    userInput: String,
    isCorrect: Boolean?,
    showCorrectAnswer: Boolean,
    onInputChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    
    val borderColor by animateColorAsState(
        targetValue = when {
            showCorrectAnswer -> MaterialTheme.colorScheme.error
            isCorrect == true -> LinguaFrancaColors.ParrotGreen
            isCorrect == false -> MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
            else -> MaterialTheme.colorScheme.outline
        },
        label = "borderColor"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = LinguaFrancaColors.TropicalOrange.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Type the Russian translation:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Show word
            Text(
                text = word,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = LinguaFrancaColors.TropicalOrange,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Show correct answer if skipped
            if (showCorrectAnswer) {
                Text(
                    text = "Correct answer:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = correctTranslation,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                if (additionalTranslations.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Also accepted: ${additionalTranslations.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Text input
                OutlinedTextField(
                    value = userInput,
                    onValueChange = onInputChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    placeholder = { Text("Type here...") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = borderColor,
                        unfocusedBorderColor = borderColor
                    ),
                    trailingIcon = {
                        if (isCorrect == true) {
                            Icon(
                                Icons.Default.Check, 
                                contentDescription = "Correct",
                                tint = LinguaFrancaColors.ParrotGreen
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp)
                )
                
                if (isCorrect == true) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Correct! 🎉",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = LinguaFrancaColors.ParrotGreen
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreChip(
    label: String,
    count: Int,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = "$label: $count",
            style = MaterialTheme.typography.labelLarge,
            color = color
        )
    }
}
