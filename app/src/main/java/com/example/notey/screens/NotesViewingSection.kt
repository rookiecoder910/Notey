package com.example.notey.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.example.notey.viewmodel.NoteViewModel
@Composable
fun NotesViewingSection(noteId: Int?, viewModel: NoteViewModel) {
    val note = viewModel.allNotes.observeAsState(emptyList())
        .value.firstOrNull { it.id == noteId }

    if (note == null) {
        Text("Note not found", modifier = Modifier.padding(16.dp))
        return
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(note.color))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = note.title,
                style = typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = note.description,
                style = typography.bodyLarge
            )
        }
    }
}
