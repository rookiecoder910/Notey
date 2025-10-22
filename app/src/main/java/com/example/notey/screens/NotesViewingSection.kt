package com.example.notey.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notey.viewmodel.NoteViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesViewingSection(
    noteId: Int?,
    viewModel: NoteViewModel,
    navController: NavController
) {
    //  Observe the list and find the specific note
    val allNotes by viewModel.allNotes.observeAsState(emptyList())
    val note = allNotes.firstOrNull { it.id == noteId }

    if (note == null) {
        Text("Note not found", modifier = Modifier.padding(16.dp))
        return
    }


    var isEditing by remember { mutableStateOf(false) }


    var editedTitle by remember(note) { mutableStateOf(note.title) }
    var editedDesc by remember(note) { mutableStateOf(note.description) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Convert the note's color Int to a Compose Color
    val noteBackgroundColor = Color(note.color)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Note" else note.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isEditing) {
                        //  Save Button
                        TextButton(onClick = {
                            //  Update changes in the database
                            viewModel.update(
                                note.copy(
                                    title = editedTitle,
                                    description = editedDesc
                                )
                            )
                            isEditing = false // Exit edit mode

                            //  Show Snackbar
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Note updated successfully",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }) {
                            Text("Save", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    } else {
                        // ✏️ Edit Button
                        IconButton(onClick = {
                            // Reset editing state to current note values before entering edit mode
                            editedTitle = note.title
                            editedDesc = note.description
                            isEditing = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                        // 🗑️ Delete Button
                        IconButton(onClick = {
                            viewModel.delete(note)
                            navController.popBackStack()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                // Apply the note's color as the background
                .background(noteBackgroundColor)
                .padding(padding)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    // Optional: Give the main content area a different background if you prefer
                    .background(Color.Transparent)
                    .padding(16.dp),
                color = Color.Transparent // Surface background is transparent to show Box color
            ) {
                if (isEditing) {
                    // ✏️ Edit mode UI
                    Column {
                        OutlinedTextField(
                            value = editedTitle,
                            onValueChange = { editedTitle = it },
                            label = { Text("Title") },
                            textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp)) // Increased spacing
                        OutlinedTextField(
                            value = editedDesc,
                            onValueChange = { editedDesc = it },
                            label = { Text("Description") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            singleLine = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                } else {
                    // 📖 View mode UI - Improved look
                    Column {
                        // Note Title
                        Text(
                            text = note.title,
                            style = MaterialTheme.typography.headlineLarge, // Larger font for title
                            fontWeight = FontWeight.ExtraBold, // Bolder font
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp)) // Clear separation

                        // Note Description
                        Text(
                            text = note.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}