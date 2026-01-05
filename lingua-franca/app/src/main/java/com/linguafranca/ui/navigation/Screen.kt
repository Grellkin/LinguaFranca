package com.linguafranca.ui.navigation

sealed class Screen(val route: String) {
    // Auth flow
    data object Welcome : Screen("welcome")
    data object Setup : Screen("setup")
    
    // Main screens
    data object Home : Screen("home")
    data object Dictionaries : Screen("dictionaries")
    data object Learning : Screen("learning")
    data object Profile : Screen("profile")
    data object Settings : Screen("settings")
    
    // Dictionary screens
    data object DictionaryDetail : Screen("dictionary/{dictionaryId}") {
        fun createRoute(dictionaryId: String) = "dictionary/$dictionaryId"
    }
    data object CreateDictionary : Screen("dictionary/create")
    data object EditDictionary : Screen("dictionary/{dictionaryId}/edit") {
        fun createRoute(dictionaryId: String) = "dictionary/$dictionaryId/edit"
    }
    
    // Word screens
    data object WordDetail : Screen("word/{wordId}") {
        fun createRoute(wordId: String) = "word/$wordId"
    }
    data object CreateWord : Screen("dictionary/{dictionaryId}/word/create") {
        fun createRoute(dictionaryId: String) = "dictionary/$dictionaryId/word/create"
    }
    data object EditWord : Screen("word/{wordId}/edit") {
        fun createRoute(wordId: String) = "word/$wordId/edit"
    }
    
    // Learning screens
    data object LearningSession : Screen("learning/session")
    data object LearningResult : Screen("learning/result/{correctCount}/{totalCount}") {
        fun createRoute(correctCount: Int, totalCount: Int) = "learning/result/$correctCount/$totalCount"
    }
    
    // Tag screens
    data object Tags : Screen("tags")
    data object CreateTag : Screen("tag/create")
    data object EditTag : Screen("tag/{tagId}/edit") {
        fun createRoute(tagId: String) = "tag/$tagId/edit"
    }
    
    // Search
    data object Search : Screen("search")
}

