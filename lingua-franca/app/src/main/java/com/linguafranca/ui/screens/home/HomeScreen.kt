package com.linguafranca.ui.screens.home

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.linguafranca.domain.model.DictionaryType
import com.linguafranca.domain.model.DictionaryWithProgress
import com.linguafranca.ui.theme.LinguaFrancaColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    initialTab: Int = 0,
    onNavigateToLearning: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToCreateDictionary: () -> Unit,
    onNavigateToDictionary: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by rememberSaveable { mutableIntStateOf(initialTab) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Lingua Franca",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        uiState.user?.let { user ->
                            Text(
                                text = "Welcome back, ${user.displayName}!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(
                    onClick = onNavigateToCreateDictionary,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add dictionary")
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            if (selectedTab == 1) Icons.Filled.Book else Icons.Outlined.Book,
                            contentDescription = "Dictionaries"
                        )
                    },
                    label = { Text("Library") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 0; onNavigateToLearning() },
                    icon = {
                        Icon(
                             Icons.Outlined.School,
                            contentDescription = "Learn"
                        )
                    },
                    label = { Text("Learn") }
                )
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
            when (selectedTab) {
                0 -> HomeTab(
                    uiState = uiState,
                    onNavigateToDictionary = onNavigateToDictionary,
                    onNavigateToDictionaries = { selectedTab = 1 },
                    onNavigateToLearning = onNavigateToLearning,
                    modifier = Modifier.padding(paddingValues)
                )
                1 -> DictionariesTab(
                    dictionaries = uiState.dictionaries,
                    onNavigateToDictionary = onNavigateToDictionary,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun HomeTab(
    uiState: HomeUiState,
    onNavigateToDictionary: (String) -> Unit,
    onNavigateToDictionaries: () -> Unit,
    onNavigateToLearning: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress Overview Card
        item {
            ProgressOverviewCard(
                totalWords = uiState.totalWords,
                learnedWords = uiState.totalWordsLearned,
                progress = uiState.overallProgress,
                onStartLearning = onNavigateToLearning
            )
        }

        // Quick Actions
        item {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    QuickActionCard(
                        title = "Start Learning",
                        subtitle = "Review your words",
                        icon = Icons.Default.School,
                        onClick = onNavigateToLearning
                    )
                }
                item {
                    QuickActionCard(
                        title = "Add Words",
                        subtitle = "Expand vocabulary",
                        icon = Icons.Default.Add,
                        onClick = {
                            onNavigateToDictionaries()
                        }
                    )
                }
            }
        }

        // Recent Dictionaries
        if (uiState.dictionaries.isNotEmpty()) {
            item {
                Text(
                    text = "Your Dictionaries",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            items(uiState.dictionaries.take(5)) { dictionaryWithProgress ->
                DictionaryCard(
                    dictionaryWithProgress = dictionaryWithProgress,
                    onClick = { onNavigateToDictionary(dictionaryWithProgress.dictionary.id) }
                )
            }
        }
    }
}

@Composable
private fun ProgressOverviewCard(
    totalWords: Int,
    learnedWords: Int,
    progress: Float,
    onStartLearning: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Your Progress",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$learnedWords of $totalWords words learned",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                
                // Circular progress
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(64.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 6.dp,
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                    )
                    Text(
                        text = "${progress.toInt()}%",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStartLearning() },
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Continue Learning",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DictionaryCard(
    dictionaryWithProgress: DictionaryWithProgress,
    onClick: () -> Unit
) {
    val dictionary = dictionaryWithProgress.dictionary
    val progress = dictionaryWithProgress.progress
    
    val typeColor = when (dictionary.type) {
        DictionaryType.CUSTOM -> MaterialTheme.colorScheme.primary
        DictionaryType.FRANCO -> LinguaFrancaColors.TropicalOrange
        DictionaryType.COMMUNITY -> LinguaFrancaColors.TropicalBlue
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(typeColor)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = dictionary.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${progress.totalWords} words",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Text(
                    text = "${progress.progressPercent.toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = typeColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress.progressPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = typeColor,
                trackColor = typeColor.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
private fun DictionariesTab(
    dictionaries: List<DictionaryWithProgress>,
    onNavigateToDictionary: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "All Dictionaries",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (dictionaries.isEmpty()) {
            item {
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
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No dictionaries yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Create your first dictionary to start learning!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        } else {
            items(dictionaries) { dictionaryWithProgress ->
                DictionaryCard(
                    dictionaryWithProgress = dictionaryWithProgress,
                    onClick = { onNavigateToDictionary(dictionaryWithProgress.dictionary.id) }
                )
            }
        }
    }
}

