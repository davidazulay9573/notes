package com.example.notes.ui.composables.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.notes.ui.composables.components.FormNote
import com.example.notes.viewmodel.NotesViewModel

@Composable
fun EditNoteScreen(navController: NavController, viewModel: NotesViewModel) {
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val noteId = backStackEntry?.arguments?.getString("noteId") ?: return
    val note = viewModel.get(noteId)

    if (note == null) {
        Text("Note with ID: $noteId not found")
        return
    }

    var title by remember { mutableStateOf(note.title) }
    var description by remember { mutableStateOf(note.description) }
    val isLoading by viewModel.isLoading.collectAsState(initial = false)

    FormNote(
        title = title,
        description = description,
        isLoading = isLoading,
        onTitleChange = { title = it },
        onContentChange = { description = it },
        onSubmit = {
            viewModel.update(note.copy(title = title, description = description)) {
                navController.navigate("notes")
            }
        }
    )
}
