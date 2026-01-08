package com.linguafranca.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.linguafranca.ui.screens.auth.SetupScreen
import com.linguafranca.ui.screens.auth.WelcomeScreen
import com.linguafranca.ui.screens.dictionary.CreateDictionaryScreen
import com.linguafranca.ui.screens.dictionary.DictionaryDetailScreen
import com.linguafranca.ui.screens.home.HomeScreen
import com.linguafranca.ui.screens.home.HomeViewModel
import com.linguafranca.ui.screens.learning.LearningResultScreen
import com.linguafranca.ui.screens.learning.LearningScreen
import com.linguafranca.ui.screens.learning.LearningSessionScreen
import com.linguafranca.ui.screens.profile.ProfileScreen
import com.linguafranca.ui.screens.search.SearchScreen
import com.linguafranca.ui.screens.settings.SettingsScreen
import com.linguafranca.ui.screens.word.CreateWordScreen
import com.linguafranca.ui.screens.word.WordDetailScreen

@Composable
fun LinguaFrancaNavHost(
    navController: NavHostController = rememberNavController()
) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val hasUser by homeViewModel.hasUser.collectAsState()
    
    val startDestination = if (hasUser) Screen.Home.route else Screen.Welcome.route
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(300)
            )
        }
    ) {
        // Auth flow
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onGetStarted = { navController.navigate(Screen.Setup.route) }
            )
        }
        
        composable(Screen.Setup.route) {
            SetupScreen(
                onSetupComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Main screens
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToLearning = { navController.navigate(Screen.Learning.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToCreateDictionary = { navController.navigate(Screen.CreateDictionary.route) },
                onNavigateToDictionary = { id ->
                    navController.navigate(Screen.DictionaryDetail.createRoute(id)) 
                }
            )
        }
        
        composable(Screen.Dictionaries.route) {
            // Reuses HomeScreen with dictionary tab selected
            HomeScreen(
                initialTab = 1,
                onNavigateToLearning = { navController.navigate(Screen.Learning.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToCreateDictionary = { navController.navigate(Screen.CreateDictionary.route) },
                onNavigateToDictionary = { id ->
                    navController.navigate(Screen.DictionaryDetail.createRoute(id)) 
                }
            )
        }
        
        composable(Screen.Learning.route) {
            LearningScreen(
                onStartSession = { navController.navigate(Screen.LearningSession.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.LearningSession.route) {
            LearningSessionScreen(
                onSessionComplete = { correct, total ->
                    navController.navigate(Screen.LearningResult.createRoute(correct, total)) {
                        popUpTo(Screen.Learning.route)
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.LearningResult.route,
            arguments = listOf(
                navArgument("correctCount") { type = NavType.IntType },
                navArgument("totalCount") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val correctCount = backStackEntry.arguments?.getInt("correctCount") ?: 0
            val totalCount = backStackEntry.arguments?.getInt("totalCount") ?: 0
            LearningResultScreen(
                correctCount = correctCount,
                totalCount = totalCount,
                onContinueLearning = {
                    navController.navigate(Screen.LearningSession.route) {
                        popUpTo(Screen.Learning.route)
                    }
                },
                onFinish = {
                    navController.popBackStack(Screen.Home.route, false)
                }
            )
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToWord = { wordId -> 
                    navController.navigate(Screen.WordDetail.createRoute(wordId)) 
                },
                onNavigateToDictionary = { dictionaryId ->
                    navController.navigate(Screen.DictionaryDetail.createRoute(dictionaryId))
                }
            )
        }
        
        // Dictionary screens
        composable(
            route = Screen.DictionaryDetail.route,
            arguments = listOf(navArgument("dictionaryId") { type = NavType.StringType })
        ) { backStackEntry ->
            val dictionaryId = backStackEntry.arguments?.getString("dictionaryId") ?: ""
            DictionaryDetailScreen(
                dictionaryId = dictionaryId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToWord = { wordId -> 
                    navController.navigate(Screen.WordDetail.createRoute(wordId)) 
                },
                onNavigateToCreateWord = { 
                    navController.navigate(Screen.CreateWord.createRoute(dictionaryId)) 
                }
            )
        }
        
        composable(Screen.CreateDictionary.route) {
            CreateDictionaryScreen(
                onNavigateBack = { navController.popBackStack() },
                onDictionaryCreated = { navController.popBackStack() }
            )
        }
        
        // Word screens
        composable(
            route = Screen.WordDetail.route,
            arguments = listOf(navArgument("wordId") { type = NavType.StringType })
        ) { backStackEntry ->
            val wordId = backStackEntry.arguments?.getString("wordId") ?: ""
            WordDetailScreen(
                wordId = wordId,
                onEditWord = { dictionaryId, word ->
                    navController.navigate(Screen.EditWord.createRoute(dictionaryId, word))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.CreateWord.route,
            arguments = listOf(
                navArgument("dictionaryId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val dictionaryId = backStackEntry.arguments?.getString("dictionaryId") ?: ""
            CreateWordScreen(
                dictionaryId = dictionaryId,
                onNavigateBack = { navController.popBackStack() },
                onWordCreated = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditWord.route,
            arguments = listOf(
                navArgument("dictionaryId") { type = NavType.StringType },
                navArgument("wordId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val dictionaryId = backStackEntry.arguments?.getString("dictionaryId") ?: ""
            CreateWordScreen(
                dictionaryId = dictionaryId,
                onNavigateBack = { navController.popBackStack() },
                onWordCreated = { navController.popBackStack() }
            )
        }
    }
}