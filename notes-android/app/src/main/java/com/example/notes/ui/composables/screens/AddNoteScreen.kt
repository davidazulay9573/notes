package com.example.notes.ui.composables.screens

import com.example.notes.ui.composables.components.FormNote
import com.example.notes.viewmodel.NotesViewModel
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*

@Composable
fun AddNoteScreen(navController: NavController, viewModel: NotesViewModel) {
    var title by remember { mutableStateOf("Title") }
    var description by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState(initial = false)

    Box(modifier = Modifier.fillMaxSize()) {
        FormNote(
            title = title,
            description = description,
            isLoading = isLoading,
            onTitleChange = { title = it },
            onContentChange = { description = it },
            onSubmit = {
                viewModel.add(title, description) {
                    navController.navigate("notes")
                }
            }
        )
    }
}
