package com.linguafranca.domain.model

data class WordWithTags(
    val word: Word,
    val tags: List<Tag>
)

data class WordWithProgress(
    val word: Word,
    val tags: List<Tag>,
    val progress: LearningProgress?
)

data class DictionaryWithProgress(
    val dictionary: Dictionary,
    val progress: DictionaryProgress
)

