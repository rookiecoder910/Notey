Notey: Your Digital Note-Taking App

Notey is a modern, responsive Android application built using Jetpack Compose and Kotlin for fast, local note-taking. It is designed to demonstrate best practices in Android architecture, including the use of MVVM (Model-View-ViewModel), Kotlin Coroutines, and Room Persistence Library.

✨ Features

Create and Save Notes: Quickly add a title and description for new notes.

Color-Coding: Select a custom color from a palette for each note to improve organization and visual recognition.

Real-time Updates: Notes are displayed in a dynamic, staggered grid layout and update instantly when new notes are added or existing ones are modified/deleted.

Persistent Storage: Data is securely saved locally on the device using the Room database.

🛠️ Technology Stack & Architecture

This project strictly adheres to modern Android development standards.

Core Technologies

Technology

Purpose

Kotlin

Primary programming language.

Jetpack Compose

Declarative UI toolkit for building the native Android interface.

Room

Persistence library for SQLite database access.

Kotlin Coroutines

For asynchronous operations and non-blocking I/O (database transactions).

Architecture & Patterns

MVVM (Model-View-ViewModel): Separation of concerns ensuring a clean, scalable, and testable codebase.

Repository Pattern: Acts as a single source of truth for data, abstracting the data source (local Room DB) from the rest of the application.

LiveData: Used within the ViewModel to hold and observe data, ensuring the UI automatically updates when the data changes, without memory leaks.

Singleton Database: The NotesDB uses a Companion Object with @Volatile and synchronized blocks to ensure only a single instance of the database is ever running in the application.

⚙️ Key Architectural Components

1. Data Layer (roomdb & repository)

Note Entity: The blueprint for the database table, annotated with @Entity.

NoteDao: The Data Access Object, defining abstract methods like @Insert and @Query to interact with the database.

NotesRepository: A simple class that mediates between the ViewModel and the DAO, providing a clean API (e.g., insertNote(note: Note)) for data operations.

2. ViewModel Layer (viewmodel)

NoteViewModel: Holds all the UI-related data (allNotes: LiveData<List<Note>>) and business logic. It utilizes viewModelScope.launch to call suspend functions in the Repository, keeping the UI thread responsive.

NoteViewModelFactory: Necessary to correctly instantiate NoteViewModel because it requires the NotesRepository as a constructor parameter.

3. UI Layer (screens)

MainActivity: Sets up the dependencies (DB, Repo, VM), manages the overall UI state (showDialog), and hosts the main Scaffold.

DisplayNotesList: Displays the notes in a responsive LazyVerticalStaggeredGrid.

DisplayDialog: A modal dialog containing text fields and the MyColorPicker for creating a new note.

🚀 Getting Started

To run this project locally, clone the repository and open it in Android Studio:

git clone [YOUR-REPO-URL]


This README was generated with assistance from an AI coding tutor.
