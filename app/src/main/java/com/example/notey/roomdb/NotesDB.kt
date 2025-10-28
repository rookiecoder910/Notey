package com.example.notey.roomdb

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Blueprint for the database

// 💡 IMPORTANT: Database version is incremented to 2
@Database(entities = [Note::class], version = 2, exportSchema = false)
abstract class NotesDB : RoomDatabase(){
    abstract val notesDao: NoteDao

    // Creating a database object using the Singleton Pattern
    companion object{
        @Volatile
        private var INSTANCE:NotesDB?=null

        fun getInstance(context: Context):NotesDB{
            // Ensures only one thread at a time can access the synchronized block
            synchronized(this){
                var instance=INSTANCE
                if(instance==null){
                    instance= Room.databaseBuilder(
                        context= context.applicationContext,
                        NotesDB::class.java,
                        "notes_db"
                    )
                        // 💡 Add the migration MIGRATION_1_2 here
                        .addMigrations(MIGRATION_1_2)
                        .build()
                    INSTANCE = instance
                }
                return instance as NotesDB
            }
        }

        // 💡 Define the migration from version 1 to version 2
        // This migration adds the new 'lastModified' column to the 'notes_table'.
        // It is set as NOT NULL and given a DEFAULT value of 0 to handle existing rows.
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE notes_table ADD COLUMN lastModified INTEGER NOT NULL DEFAULT 0"
                )
            }
        }
    }
}
//creating a database object
//The companion object section is
// a standard Kotlin way to ensure you only ever have one copy
// of the database running at a time. This is called the Singleton Pattern,
// and it prevents crashes and data inconsistencies.