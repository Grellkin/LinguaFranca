# Lingua Franca

A language learning Android app featuring Franco, a friendly green Eclectus parrot mascot.

## Features

### MVP (Current)
- **Custom Dictionaries**: Create and manage your own word collections
- **Franco's Dictionaries**: Pre-built dictionaries with common vocabulary (Animals, Food, Basic Phrases)
- **Word Management**: Add, edit, delete words with automatic translation support
- **Smart Learning**: Spaced repetition algorithm (SM-2) for efficient memorization
- **Progress Tracking**: Track your learning progress for each dictionary and overall
- **Search**: Search across all your words and dictionaries
- **Dark/Light Theme**: Beautiful, parrot-inspired color scheme with theme support

### Languages
- **Learning**: English
- **Native**: Russian

*More languages coming in future updates!*

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture**: Clean Architecture + MVVM
- **Database**: Room (SQLite)
- **Dependency Injection**: Hilt
- **Translation**: Google ML Kit
- **Navigation**: Compose Navigation

## Project Structure

```
app/
├── src/main/java/com/linguafranca/
│   ├── MainActivity.kt
│   ├── LinguaFrancaApp.kt
│   ├── di/                     # Hilt modules
│   ├── ui/
│   │   ├── theme/              # Material 3 theming
│   │   ├── navigation/         # Navigation setup
│   │   └── screens/            # All app screens
│   ├── domain/
│   │   ├── model/              # Domain entities
│   │   ├── repository/         # Repository interfaces
│   │   └── usecase/            # Business logic
│   └── data/
│       ├── local/              # Room database
│       └── repository/         # Repository implementations
```

## Building the App

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 34

### Build Steps

1. Clone or download the project
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on emulator or device (API 26+)

```bash
./gradlew assembleDebug
```

## Learning Algorithm

The app uses the **SM-2 (SuperMemo 2)** spaced repetition algorithm:

- Words are reviewed based on your performance
- Correct answers increase the interval until next review
- Incorrect answers reset the word to be reviewed sooner
- Words reaching level 3+ are considered "learned"

### Quality Ratings
- **0-2**: Incorrect (word resets)
- **3**: Correct with difficulty
- **4**: Correct after hesitation
- **5**: Perfect recall

## Future Plans

### Phase 2
- Cloud sync with Spring Boot backend
- Real authentication (sign up/sign in)
- Community dictionaries (share with other users)
- More languages

### Phase 3
- Desktop app via Kotlin Compose Multiplatform
- iOS support
- Franco AI assistant integration

## License

Private project - All rights reserved.

---

*Built with love for language learners everywhere!* 🦜

