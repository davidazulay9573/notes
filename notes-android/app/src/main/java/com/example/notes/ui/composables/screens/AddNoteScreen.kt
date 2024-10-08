package com.example.notes.ui.composables.screens

import com.example.notes.ui.composables.components.FormNote
import com.example.notes.viewmodel.NotesViewModel
import androidx.compose.runtime.*
import androidx.navigation.NavController

@Composable
fun AddNoteScreen(navController: NavController, viewModel: NotesViewModel) {
    var title by remember { mutableStateOf("Title") }
    var description by remember { mutableStateOf("") }

    FormNote(
        title = title,
        description = description,
        onTitleChange = { title = it },
        onContentChange = { description = it },
        onSubmit = {
            viewModel.add(title, description)
            navController.navigate("notes")
        }
    )
}

