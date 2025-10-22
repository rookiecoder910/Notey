package com.example.notey.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

//tells the ksp about it is an entity whose table name is notes_table
@Entity(tableName="notes_table")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val title:String,
    val description:String,
  val color:Int //stores color as an ARGB integer



)
