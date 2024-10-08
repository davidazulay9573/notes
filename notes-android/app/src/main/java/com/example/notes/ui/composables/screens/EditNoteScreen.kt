package com.example.notes.ui.composables.screens

import com.example.notes.ui.composables.components.FormNote
import com.example.notes.viewmodel.NotesViewModel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.notes.model.Note

@Composable
fun EditNoteScreen(navController: NavController, viewModel: NotesViewModel) {
    val notes by viewModel.notes.collectAsState()
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val noteId = backStackEntry?.arguments?.getString("noteId") ?: return

    val note: Note? = notes.find { it.id == noteId }
    if (note == null) {
        Text("note with id:${noteId} not found")
        return
    }

    var title by remember { mutableStateOf(note.title) }
    var description by remember { mutableStateOf(note.description) }

    FormNote(
        title = title,
        description = description,
        onTitleChange = { title = it },
        onContentChange = { description = it },
        onSubmit = {
            viewModel.update(note.copy(title = title, description = description))
            navController.navigate("notes")
        }
    )
}
