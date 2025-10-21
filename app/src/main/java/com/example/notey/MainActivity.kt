package com.example.notey

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight


import androidx.lifecycle.ViewModelProvider
import com.example.notey.repository.NotesRepository

import com.example.notey.roomdb.NotesDB
import com.example.notey.screens.DisplayDialog
import com.example.notey.screens.DisplayNotesList
import com.example.notey.ui.theme.NoteyTheme
import com.example.notey.viewmodel.NoteViewModel
import com.example.notey.viewmodel.NoteViewModelFactory
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //Room DB
        val db= NotesDB.getInstance(applicationContext)
        //Repository
        val repo= NotesRepository(db.notesDao)
        //ViewModelFactory
        val viewModelFactory= NoteViewModelFactory(repo)
        //viewmodel
        val noteViewModel= ViewModelProvider(
            this,
            viewModelFactory
        )[NoteViewModel::class.java]
//        val note1= Note(0,"This is a demo","Please give us our app 5 star rating... ","#f59597".toColorInt())
//        //insert note into DB
//        noteViewModel.insert(note1)

        setContent {
            NoteyTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Notey: Your Digital Notes",

                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold

                            ) },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    },
                    floatingActionButton = {
                        myFAB(viewModel = noteViewModel)
                    }
                ) { innerPadding ->
                    val notes by noteViewModel.allNotes.observeAsState(emptyList())

                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        DisplayNotesList(notes = notes)
                    }
                }

            }
        }
    }
}

    @Composable
    fun myFAB(viewModel: NoteViewModel){
        var showDialog by remember {
            mutableStateOf(false)
        }
        DisplayDialog(
            viewModel = viewModel,
            showDialog =showDialog) {
                showDialog = false
            }

        FloatingActionButton(onClick = {showDialog = true },
            containerColor = Color.Blue,
            contentColor = Color.White
            ) {
            Icon(imageVector = Icons.Filled.Add,
                contentDescription="Add Note")

        }
    }
