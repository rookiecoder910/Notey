package com.example.notey.roomdb

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

//blueprint for the database

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NotesDB : RoomDatabase(){
    abstract val notesDao: NoteDao

    //creating a database object
    //The companion object section is
        // a standard Kotlin way to ensure you only ever have one copy
        // of the database running at a time. This is called the Singleton Pattern,
        // and it prevents crashes and data inconsistencies.
        companion object{
       @Volatile
       private var INSTANCE:NotesDB?=null
        fun getInstance(context: Context):NotesDB{
            synchronized(this){
                var instance=INSTANCE
                if(instance==null){
                   instance= Room.databaseBuilder(
                      context= context.applicationContext,
                       NotesDB::class.java,
                       "notes_db"
                   ).build()
                }
                return instance
            }
        }
   }

    }
