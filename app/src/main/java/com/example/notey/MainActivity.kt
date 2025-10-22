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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
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
import com.example.notey.ui.theme.NoteyTheme
import com.example.notey.viewmodel.NoteViewModel
import com.example.notey.viewmodel.NoteViewModelFactory

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
                val notes by noteViewModel.allNotes.observeAsState(emptyList())

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
                                        .padding(16.dp)
                                ) {

                                    OutlinedTextField(
                                        value = "",
                                        onValueChange = {},
                                        placeholder = { Text("Search your notes...") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(55.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                            cursorColor = MaterialTheme.colorScheme.primary
                                        ),
                                        singleLine = true,
                                        enabled = false
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    AnimatedVisibility(visible = notes.isNotEmpty()) {
                                        LazyVerticalGrid(
                                            columns = GridCells.Adaptive(160.dp),
                                            modifier = Modifier.fillMaxSize(),
                                            contentPadding = PaddingValues(4.dp)
                                        ) {
                                            items(notes) { note ->
                                                NoteCard(
                                                    note = note,
                                                    onClick = {
                                                        navController.navigate("detail/${note.id}")
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    AnimatedVisibility(visible = notes.isEmpty()) {
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
        Color.Black.copy(alpha = 0.9f) // Use slightly transparent black for softer look
    } else {
        Color.White.copy(alpha = 0.9f) // Use slightly transparent white for softer look
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
            // Apply a slightly muted version of the contrasting color to the description
            Text(
                text = note.description,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor.copy(alpha = 0.8f),
                maxLines = 5
            )
        }
    }
}