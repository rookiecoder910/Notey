package com.example.notey.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notey.roomdb.Note

@Composable
fun DisplayNotesList(notes:List<Note>,modifier: Modifier = Modifier){
    LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(2),
    modifier= Modifier.fillMaxSize(),
    contentPadding= PaddingValues(16.dp)
    ){
        items(notes){
           note-> NoteListItem(note = note)
        }
    }

}



