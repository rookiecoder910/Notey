package com.example.notey.repository
import androidx.lifecycle.LiveData
import com.example.notey.roomdb.Note
import com.example.notey.roomdb.NoteDao

//Repository is a single source of truth which handles
//following data ops:
//1-fetching data from the network
//2-loading the data from a local DB
class NotesRepository(private val noteDao: NoteDao) {
    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    suspend fun insertNote(note: Note){
        return noteDao.insert(note)

    }
    suspend fun deleteNote(note:Note){
        return noteDao.delete(note)
    }
    suspend fun updateNote(note: Note){
        return noteDao.update(note)
    }


}