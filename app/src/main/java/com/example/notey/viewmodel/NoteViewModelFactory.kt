package com.example.notey.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notey.repository.NotesRepository
//if your viewmodel needs extra params then use NoteviewmodelFactory
class NoteViewModelFactory(private val repository: NotesRepository)
    : ViewModelProvider.Factory{
        override fun <T: ViewModel> create(modelClass:Class<T>):T{
         if(modelClass.isAssignableFrom(NoteViewModel::class.java)){
             @Suppress("UNCHECKED_CAST")
             return NoteViewModel(repository) as T
         }
            throw IllegalArgumentException("Unknown ViewModel class")
    }

}
