package com.example.notey.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester // NEW: For auto-focus
import androidx.compose.ui.focus.focusRequester // NEW: For auto-focus
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notey.viewmodel.NoteViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


fun formatTimestamp(timestamp: Long): String {
    val date = java.util.Date(timestamp)

    val format = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault())
    return format.format(date)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesViewingSection(
    noteId: Int?,
    viewModel: NoteViewModel,
    navController: NavController
) {
    // Fetch the specific note to display based on the ID.
    val allNotes by viewModel.allNotes.observeAsState(emptyList())
    val note = allNotes.firstOrNull { it.id == noteId }

    // Display a simple error if the note couldn't be found.
    if (note == null) {
        Text("Note not found", modifier = Modifier.padding(16.dp))
        return
    }

    // State for UI management
    var isEditing by remember { mutableStateOf(false) }
    var editedTitle by remember(note) { mutableStateOf(note.title) }
    var editedDesc by remember(note) { mutableStateOf(note.description) }
    var showDeleteDialog by remember { mutableStateOf(false) } // NEW: State for the delete confirmation

    // Focus management for automatic keyboard popup
    val descriptionFocusRequester = remember { FocusRequester() } // NEW: Focus requester object

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Determine the theme-aware colors for the background and text content.
    val noteBackgroundColor = Color(note.color)
    val cardSurfaceColor = MaterialTheme.colorScheme.surfaceVariant
    val cardContentColor = MaterialTheme.colorScheme.onSurfaceVariant

    // Effect to request focus when entering edit mode (Auto-Focus feature)
    LaunchedEffect(isEditing) {
        if (isEditing) {
            // Wait briefly for the UI to transition before requesting focus/keyboard
            kotlinx.coroutines.delay(50)
            descriptionFocusRequester.requestFocus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                // Display "Edit Note" or the note's title in the TopBar.
                title = {
                    Text(
                        if (isEditing) "Edit Note" else note.title,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                },
                // Back button to leave the note view.
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isEditing) {
                        // Show Save button when in edit mode.
                        TextButton(
                            onClick = {
                                // Update the note in the database with the new values and the current time.
                                viewModel.update(
                                    note.copy(
                                        title = editedTitle,
                                        description = editedDesc
                                        // You should ensure 'note' has a lastModified: Long field in your Note data class
                                        // and update it here:
                                        // , lastModified = System.currentTimeMillis()
                                    )
                                )
                                isEditing = false // Exit edit mode after saving.
                                // Show a confirmation message.
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Note updated successfully",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                            // Only allow saving if the title is not blank AND something has actually changed.
                            enabled = editedTitle.isNotBlank() && (editedTitle != note.title || editedDesc != note.description)
                        ) {
                            Text("Save", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    } else {
                        // Show Edit and Delete buttons when in view mode.

                        // Edit Button: switches to edit mode.
                        IconButton(onClick = {
                            // Reset editing fields to current note values before entering edit mode.
                            editedTitle = note.title
                            editedDesc = note.description
                            isEditing = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onPrimary)
                        }

                        // Delete Button: triggers the confirmation dialog.
                        IconButton(onClick = { showDeleteDialog = true }) { // <-- NEW: Show dialog
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                },
                // Set TopAppBar colors based on the theme's primary color.
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        // Container Box to hold the background color.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(noteBackgroundColor) // Apply the note's color as the screen background.
                .padding(padding)
        ) {
            // Surface element to create the card-like container for the note content.
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = MaterialTheme.shapes.medium,
                color = cardSurfaceColor, // Theme-aware card color (adapts to dark mode).
                shadowElevation = 4.dp // Subtle shadow for a professional look.
            ) {

                // Main content column, enabling scrolling for long notes.
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(scrollState)
                ) {
                    if (isEditing) {
                        // Editing UI: Two transparent OutlinedTextFields.

                        // Title Input Field
                        OutlinedTextField(
                            value = editedTitle,
                            onValueChange = { editedTitle = it },
                            label = { Text("Title") },
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = cardContentColor
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            // Custom colors to make the field look integrated into the card.
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Transparent,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = cardContentColor.copy(alpha = 0.7f),
                                cursorColor = MaterialTheme.colorScheme.primary,
                            )
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // Description Input Field with Auto-Focus
                        OutlinedTextField(
                            value = editedDesc,
                            onValueChange = { editedDesc = it },
                            label = { Text("Note Content") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 200.dp)
                                .focusRequester(descriptionFocusRequester), // <-- NEW: Apply FocusRequester
                            singleLine = false,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = cardContentColor),
                            // Custom colors for integrated look.
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Transparent,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = cardContentColor.copy(alpha = 0.7f),
                                cursorColor = MaterialTheme.colorScheme.primary,
                            )
                        )
                    } else {
                        // Viewing UI: Static Text fields.

                        // Note Title: Clicking makes it switch to edit mode.
                        Text(
                            text = note.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = cardContentColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { isEditing = true } // Switch to editing on click.
                        )

                        Spacer(modifier = Modifier.height(4.dp)) // Small space for subtitle

                        // Last Modified Timestamp
                        // NOTE: This assumes your 'note' object has a 'lastModified: Long' property.
//                        Text(
//                            text = "Edited: ${formatTimestamp(note.lastModified)}", // <-- NEW: Display timestamp
//                            style = MaterialTheme.typography.labelSmall,
//                            color = cardContentColor.copy(alpha = 0.6f)
//                        )

                        Spacer(modifier = Modifier.height(16.dp)) // Separation before the main body

                        // Note Description: Clicking makes it switch to edit mode.
                        Text(
                            text = note.description,
                            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 28.sp), // Increased line height for readability.
                            color = cardContentColor.copy(alpha = 0.9f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { isEditing = true } // Switch to editing on click.
                        )
                    }
                }
            }
        }
    }

    // --- Delete Confirmation Dialog (Appears over the Scaffold) ---
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this note? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.delete(note)
                        navController.popBackStack()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}