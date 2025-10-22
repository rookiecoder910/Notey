//package com.example.notey.navigation
//
//import androidx.compose.runtime.Composable
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//
//import com.example.notey.screens.NotesViewingSection
//import com.example.notey.viewmodel.NoteViewModel
//
//@Composable
//fun NoteyNavGraph(
//    navController: NavHostController,
//    noteViewModel: NoteViewModel
//) {
//    NavHost(
//        navController = navController,
//        startDestination = "home"
//    ) {
//        composable("home") {
//            HomeScreen(
//                noteViewModel = noteViewModel,
//                onNoteClick = { noteId ->
//                    navController.navigate("detail/$noteId")
//                }
//            )
//        }
//
//        composable("detail/{noteId}") { backStackEntry ->
//            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
//            NotesViewingSection(noteId = noteId, viewModel = noteViewModel)
//        }
//    }
//}
