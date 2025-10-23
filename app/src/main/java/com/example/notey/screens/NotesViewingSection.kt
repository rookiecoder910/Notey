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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notey.viewmodel.NoteViewModel
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.CoroutineScope
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

    if (note == null) {
        Text("Note not found", modifier = Modifier.padding(16.dp))
        return
    }

    // State for UI management
    var isEditing by remember { mutableStateOf(false) }
    var editedTitle by remember(note) { mutableStateOf(note.title) }
    var editedDesc by remember(note) { mutableStateOf(note.description) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isPolishing by remember { mutableStateOf(false) } // State for AI loading

    // Focus and Coroutine setup
    val descriptionFocusRequester = remember { FocusRequester() }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Determine colors
    val noteBackgroundColor = Color(note.color)
    val activeCardColor = noteBackgroundColor.copy(alpha = 0.95f)
    val cardContentColor = if (noteBackgroundColor.red * 0.299 + noteBackgroundColor.green * 0.587 + noteBackgroundColor.blue * 0.114 > 0.5)
        Color.Black else Color.White

    // AI Model initialization (Protorype only: Key MUST be secured)
    val model = remember {
        GenerativeModel(
            modelName = "gemini-2.5-flash",
            // WARNING: Storing key in code is INSECURE for production!
            apiKey = "AIzaSyBifx_eHy4hP058d9PXGoKylS2jf7-DFO0"
        )
    }

    // AI function to polish content
    // ... inside NotesViewingSection Composable

    fun polishNoteContent(scope: CoroutineScope, originalText: String) {
        isPolishing = true
        scope.launch { // Ensure the entire logic runs within the coroutine scope
            try {
                val prompt = "You are a professional editor. Review this note content for grammar, spelling, and clarity. Only return the corrected, improved version of the text without any introductory or concluding remarks. Text: \"\"\"$originalText\"\"\""

                // Use runCatching to safely execute the API call and handle all exceptions
                val result = runCatching {
                    // This is the core API call
                    model.generateContent(prompt)
                }

                if (result.isSuccess) {
                    // Successful response
                    val response = result.getOrThrow()
                    editedDesc = response.text ?: originalText
                    snackbarHostState.showSnackbar("Content polished by AI successfully!")
                } else {
                    // Handle the failure (including 503 ServerException and MissingFieldException)
                    val error = result.exceptionOrNull()

                    // Log the error for debugging
                    error?.printStackTrace()

                    snackbarHostState.showSnackbar("AI Polishing failed. Please try again later. Error: ${error?.message?.take(30)}...")
                }

            } catch (e: Exception) {
                // Fallback catch, mostly for logic errors, though runCatching should handle network/API errors
                snackbarHostState.showSnackbar("An unknown error occurred: ${e.message}")
            } finally {
                isPolishing = false
            }
        }
    }
// ... rest of the Composable

    // Auto-Focus feature
    LaunchedEffect(isEditing) {
        if (isEditing) {
            kotlinx.coroutines.delay(50)
            descriptionFocusRequester.requestFocus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edit Note" else note.title,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                },
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

                                viewModel.update(
                                    note.copy(
                                        title = editedTitle,
                                        description = editedDesc,
//                                        lastModified = System.currentTimeMillis() // FIXED: Update timestamp
                                    )
                                )
                                isEditing = false
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Note updated successfully",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                            enabled = editedTitle.isNotBlank() && (editedTitle != note.title || editedDesc != note.description)
                        ) {
                            Text("Save", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    } else {
                        // View mode actions (Edit and Delete icons only)
                        IconButton(onClick = {
                            editedTitle = note.title
                            editedDesc = note.description
                            isEditing = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                },
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(noteBackgroundColor)
                .padding(padding)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = MaterialTheme.shapes.medium,
                color = activeCardColor,
                shadowElevation = 4.dp
            ) {

                // Main content column, enabling scrolling for long notes.
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                        .verticalScroll(scrollState)
                ) {
                    if (isEditing) {
                        // --- EDITING UI ---

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
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Transparent,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = cardContentColor.copy(alpha = 0.7f),
                                cursorColor = MaterialTheme.colorScheme.primary,
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp)) // Reduced space slightly

                        // --- FIX: AI Button moved here for proper placement ---
                        Button(
                            onClick = { polishNoteContent(coroutineScope, editedDesc) },
                            enabled = editedDesc.isNotBlank() && !isPolishing,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                        ) {
                            if (isPolishing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Improve Content with AI")
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Description Input Field with Auto-Focus
                        OutlinedTextField(
                            value = editedDesc,
                            onValueChange = { editedDesc = it },
                            label = { Text("Note Content") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 200.dp)
                                .focusRequester(descriptionFocusRequester),
                            singleLine = false,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = cardContentColor),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = Color.Transparent,
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                unfocusedLabelColor = cardContentColor.copy(alpha = 0.7f),
                                cursorColor = MaterialTheme.colorScheme.primary,
                            )
                        )
                    } else {


                        // Note Title
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
                                ) { isEditing = true }
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Last Modified Timestamp - FIXED: Enabled
//                        Text(
//                            text = "Edited: ${formatTimestamp(note.lastModified)}",
//                            style = MaterialTheme.typography.labelSmall,
//                            color = cardContentColor.copy(alpha = 0.6f),
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .clickable(
//                                    interactionSource = remember { MutableInteractionSource() },
//                                    indication = null
//                                ) { isEditing = true } // Also clickable
//                        )
//
//                        Spacer(modifier = Modifier.height(16.dp))

                        // Note Description: RENDERED AS PARAGRAPHS
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { isEditing = true } // Entire block is clickable
                        ) {
                            val paragraphs = note.description.split("\n\n").filter { it.isNotBlank() }
                            paragraphs.forEachIndexed { index, paragraph ->
                                Text(
                                    text = paragraph,
                                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 28.sp),
                                    color = cardContentColor.copy(alpha = 0.9f),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                // Add spacing between paragraphs, but not after the last one
                                if (index < paragraphs.size - 1) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
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