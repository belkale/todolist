# Todo List Android App

A modern Android TODO list application built with Jetpack Compose, Room database, and modern Android architecture components.

## Features

- ✅ **Add, Edit, and Delete Todos** - Full CRUD operations for managing todos
- 📝 **Rich Todo Details** - Title, description, priority, and due date
- 🏷️ **Priority Levels** - Low, Medium, High priority with visual indicators
- 📅 **Due Dates** - Optional due dates with date picker
- 🔍 **Filtering** - Filter todos by All, Active, or Completed
- ✅ **Mark Complete** - Toggle todo completion status
- 🗑️ **Bulk Actions** - Clear all completed todos at once
- 🎨 **Modern UI** - Beautiful Material 3 design with Jetpack Compose
- 🌙 **Dark/Light Theme** - Automatic theme switching support

## Architecture

This app follows modern Android development best practices:

- **MVVM Architecture** - Model-View-ViewModel pattern
- **Repository Pattern** - Clean data access layer
- **Dependency Injection** - Hilt for dependency management
- **Room Database** - Local SQLite database with type converters
- **Jetpack Compose** - Modern declarative UI toolkit
- **Navigation Component** - Type-safe navigation between screens
- **State Management** - Reactive state management with StateFlow
- **Coroutines** - Asynchronous programming with Kotlin coroutines

## Tech Stack

- **UI**: Jetpack Compose, Material 3
- **Database**: Room (SQLite)
- **Dependency Injection**: Hilt
- **Navigation**: Navigation Compose
- **Architecture**: MVVM, Repository Pattern
- **Async**: Kotlin Coroutines
- **Language**: Kotlin

## Project Structure

```
app/src/main/java/com/example/todolist/
├── data/
│   ├── Todo.kt              # Entity and data classes
│   ├── TodoDao.kt           # Data Access Object
│   ├── TodoDatabase.kt      # Room database
│   ├── TodoRepository.kt    # Repository pattern
│   └── Converters.kt        # Type converters
├── di/
│   └── DatabaseModule.kt    # Hilt dependency injection
├── navigation/
│   └── TodoNavigation.kt    # Navigation setup
├── ui/
│   ├── TodoViewModel.kt     # ViewModel
│   └── screens/
│       ├── TodoListScreen.kt    # Main todo list screen
│       └── AddEditTodoScreen.kt # Add/Edit todo screen
├── MainActivity.kt          # Main activity
├── TodoApplication.kt       # Application class
└── ui/theme/               # Theme and styling
```

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Run on device or emulator

## Requirements

- Android API 29+ (Android 10+)
- Kotlin 2.2.20+
- Android Gradle Plugin 8.13.0+

## Screenshots

The app features a clean, modern interface with:
- Todo list with cards showing title, description, priority, and due date
- Floating action button to add new todos
- Filter chips to view all, active, or completed todos
- Edit and delete actions for each todo
- Date picker for setting due dates
- Priority selection with visual indicators
