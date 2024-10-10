package com.example.notes.ui.composables.screens

import com.example.notes.ui.composables.components.NoteCard
import com.example.notes.ui.composables.components.SearchSection
import com.example.notes.viewmodel.NotesViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun NotesScreen(navController: NavController, viewModel: NotesViewModel) {
    val notes by viewModel.notes.collectAsState(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        SearchSection {
            query, searchBy -> viewModel.search(query, searchBy)
        }

        if (notes.isEmpty()) {
            Text(
                modifier = Modifier.padding(20.dp),
                text = "Don't have notes yet...",
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(notes) { note ->
                NoteCard(
                    note = note,
                    onClick = { navController.navigate("notes/${note.id}") },
                    onEdit = { navController.navigate("notes/${note.id}/edit") },
                    onDelete = {
                        viewModel.delete(note.id)
                        navController.navigate("notes")
                    },
                )
            }
        }
    }
}
