package com.example.notes.ui.composables.screens

import com.example.notes.viewmodel.NotesViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.notes.ui.composables.components.ConfirmationDialog

@Composable
fun NoteScreen(navController: NavController, viewModel: NotesViewModel) {
    val notes by viewModel.notes.collectAsState()
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val noteId = backStackEntry?.arguments?.getString("noteId") ?: return
    val note = notes.find { it.id == noteId }

    if (note == null) {
        Text("note with id: $noteId not found")
        return
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = note.title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        HorizontalDivider(
            modifier = Modifier.padding(8.dp).height(2.dp)
        )
        Text(
            text = note.description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Back")
            }

            Button(
                onClick = { navController.navigate("notes/${noteId}/edit") },
                modifier = Modifier.padding(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Edit")
            }
            Button(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Delete")
            }
        }

        if (showDeleteDialog) {
            ConfirmationDialog(
                message = "Are you sure you want to delete this note?",
                onConfirm = {
                    viewModel.delete(noteId)
                    showDeleteDialog = false
                    navController.navigate("notes")
                },
                onDismiss = {
                    showDeleteDialog = false
                }
            )
        }
    }
}
