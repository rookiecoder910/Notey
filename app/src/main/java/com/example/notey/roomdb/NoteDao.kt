package com.example.notey.roomdb

import android.provider.ContactsContract
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

//this file define the methods for the database
@Dao
interface NoteDao {
    //Dao is an interface where u provide methods for various DB ops
    @Insert
    suspend fun insert(note:Note)
    @Delete
    suspend fun delete(note:Note)
    @Update
    suspend fun update(note: Note)

    @Query("SELECT * FROM notes_table")
    //livedata is a class that provides lifecycle aware data
    //its main usage is to observe changes to data
    fun getAllNotes(): LiveData<List<Note>>




}