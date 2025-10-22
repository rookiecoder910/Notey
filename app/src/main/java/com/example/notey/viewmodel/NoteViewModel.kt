package com.example.notey.viewmodel
import androidx.lifecycle.LiveData

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notey.repository.NotesRepository
import com.example.notey.roomdb.Note
import kotlinx.coroutines.launch

//viewmodel: store and manage ui related data
//seperating ui related logic from UI controller (composable/Activity/frag.)

class NoteViewModel (private val repository: NotesRepository) : ViewModel(){
 val allNotes: LiveData<List<Note>> = repository.allNotes
//used to save notes
    fun insert(note: Note)=
        viewModelScope.launch {
            repository.insertNote(note)
        }
    fun delete(note: Note)=
        viewModelScope.launch {
            repository.deleteNote(note)

        }
    fun update(note: Note)=
        viewModelScope.launch {
            repository.updateNote(note)

        }


}