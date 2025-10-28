package com.example.notey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.notey.repository.NotesRepository
import com.example.notey.roomdb.Note
import com.example.notey.roomdb.NotesDB
import com.example.notey.screens.DisplayDialog
import com.example.notey.screens.NotesViewingSection
import com.example.notey.screens.formatTimestamp
import com.example.notey.ui.theme.NoteyTheme
import com.example.notey.viewmodel.NoteViewModel
import com.example.notey.viewmodel.NoteViewModelFactory
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = NotesDB.getInstance(applicationContext)
        val repo = NotesRepository(db.notesDao)
        val viewModelFactory = NoteViewModelFactory(repo)
        val noteViewModel = ViewModelProvider(this, viewModelFactory)[NoteViewModel::class.java]

        setContent {
            NoteyTheme {
                val navController = rememberNavController()
                var showDialog by remember { mutableStateOf(false) }

                // 💡 Search state for the OutlinedTextField
                var searchQuery by remember { mutableStateOf("") }

                val allNotes by noteViewModel.allNotes.observeAsState(emptyList())

                // 💡 Filtering Logic: Filter the list based on the search query
                val filteredNotes = remember(allNotes, searchQuery) {
                    if (searchQuery.isBlank()) {
                        allNotes
                    } else {
                        allNotes.filter { note ->
                            // Search by title or description, ignoring case
                            note.title.contains(searchQuery, ignoreCase = true) ||
                                    note.description.contains(searchQuery, ignoreCase = true)
                        }
                    }
                }


                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    // HOME SCREEN
                    composable("home") {
                        Scaffold(
                            topBar = {
                                CenterAlignedTopAppBar(
                                    title = {
                                        Text(
                                            "Notey",
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    },
                                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            },
                            floatingActionButton = {
                                FloatingActionButton(
                                    onClick = { showDialog = true },
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White,
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = "Add Note")
                                }
                            }
                        ) { innerPadding ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(innerPadding)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp) // Apply horizontal padding
                                        .padding(top = 16.dp) // Top padding for content below TopBar
                                ) {

                                    OutlinedTextField(
                                        value = searchQuery, // 💡 Bind to search state
                                        onValueChange = { searchQuery = it }, // 💡 Update search state
                                        placeholder = { Text("Search your notes...") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(55.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        leadingIcon = {
                                            Icon(Icons.Default.Search, contentDescription = "Search")
                                        },
                                        // Clear button
                                        trailingIcon = {
                                            if (searchQuery.isNotEmpty()) {
                                                IconButton(onClick = { searchQuery = "" }) {
                                                    Icon(Icons.Default.Close, contentDescription = "Clear Search")
                                                }
                                            }
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                            cursorColor = MaterialTheme.colorScheme.primary
                                        ),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(
                                            imeAction = ImeAction.Search
                                        )
                                        // Removed: enabled = false
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // 💡 Use the filtered list for display
                                    AnimatedVisibility(visible = filteredNotes.isNotEmpty()) {
                                        LazyVerticalGrid(
                                            columns = GridCells.Adaptive(160.dp),
                                            modifier = Modifier.fillMaxSize(),
                                            contentPadding = PaddingValues(4.dp)
                                        ) {
                                            items(filteredNotes) { note -> // Using filteredNotes
                                                NoteCard(
                                                    note = note,
                                                    onClick = {
                                                        navController.navigate("detail/${note.id}")
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    // 💡 Display if no notes match the search
                                    AnimatedVisibility(visible = filteredNotes.isEmpty() && allNotes.isNotEmpty() && searchQuery.isNotEmpty()) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "No notes match \"$searchQuery\"",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = Color.Gray
                                            )
                                        }
                                    }

                                    // Original 'No notes yet' message (when no search is active)
                                    AnimatedVisibility(visible = allNotes.isEmpty()) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "No notes yet. Tap + to add one!",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }

                            DisplayDialog(
                                viewModel = noteViewModel,
                                showDialog = showDialog
                            ) { showDialog = false }
                        }
                    }


                    composable(
                        route = "detail/{noteId}",
                        arguments = listOf(navArgument("noteId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val noteId = backStackEntry.arguments?.getInt("noteId")
                        NotesViewingSection(
                            noteId = noteId,
                            viewModel = noteViewModel,
                            navController = navController
                        )
                    }

                }
            }
        }
    }
}

fun getContrastColor(backgroundColor: Color): Color {

    val argb = backgroundColor.toArgb()


    return if (ColorUtils.calculateLuminance(argb) > 0.5) {
        Color.Black.copy(alpha = 0.9f)
    } else {
        Color.White.copy(alpha = 0.9f)
    }
}



@Composable
fun NoteCard(note: Note, onClick: () -> Unit) {

    val cardBackgroundColor = Color(note.color)


    val contentColor = getContrastColor(cardBackgroundColor)

    Card(
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()
            .heightIn(min = 120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            // Set the dynamic background color
            containerColor = cardBackgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Apply the contrasting color to the title
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = contentColor
            )
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = note.description,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor.copy(alpha = 0.8f),
                maxLines = 5
            )
            Spacer(modifier = Modifier.height(6.dp))

            Text(

                // 💡 Using the new formatRelativeTime function
                text = "Edited: ${formatRelativeTime(note.lastModified)}",


                // 💡 Enhanced style for subtlety
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,


                // 💡 Subtler color
                color = contentColor.copy(alpha = 0.3f),
            )
        }
    }
}