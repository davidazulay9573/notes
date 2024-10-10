package com.example.notes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notes.ui.composables.layout.NavBar
import com.example.notes.ui.composables.screens.NoteScreen
import com.example.notes.ui.composables.screens.NotesScreen
import com.example.notes.ui.composables.screens.AddNoteScreen
import com.example.notes.ui.composables.screens.EditNoteScreen
import com.example.notes.viewmodel.NotesViewModel

@Composable
fun App(notesViewModel: NotesViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { NavBar(navController) },
        modifier = Modifier.padding(8.dp)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "notes",
                modifier = Modifier.weight(1f)
            ) {
                composable("notes") {
                    NotesScreen(navController, notesViewModel)
                }
                composable("notes/add") {
                    AddNoteScreen(navController, notesViewModel)
                }
                composable("notes/{noteId}") {
                    NoteScreen(navController, notesViewModel)
                }
                composable("notes/{noteId}/edit") {
                    EditNoteScreen(navController, notesViewModel)
                }
            }
        }
    }
}
