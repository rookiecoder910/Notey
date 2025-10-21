package com.example.notey

import android.os.Bundle
import androidx.activity.ComponentActivity // Corrected import
import androidx.activity.compose.setContent // Corrected import
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.notey.repository.NotesRepository
import com.example.notey.roomdb.NotesDB
import com.example.notey.ui.theme.NoteyTheme
import com.example.notey.viewmodel.NoteViewModel
import com.example.notey.viewmodel.NoteViewModelFactory
class MainActivity : ComponentActivity() {
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


        setContent {
            NoteyTheme {
                // A Scaffold provides a basic layout structure
              val notes by noteViewModel.allNotes.observeAsState(emptyList())
                // A surface container using the 'background' color from the theme
            }
        }
    }
}