package com.example.notes

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.notes.data.localdb.ActionsController
import com.example.notes.data.localdb.LocalDB
import com.example.notes.data.localdb.NotesController
import com.example.notes.data.api.RetrofitClient
import com.example.notes.repository.notes.NotesRepository
import com.example.notes.viewmodel.NotesViewModel

class MainActivity : ComponentActivity() {
    private lateinit var networkManager: NetworkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val notesViewModel = setupNotesViewModel()

        networkManager = NetworkManager(this)
        networkManager.registerNetworkCallback { notesViewModel.syncNotes() }

        setContent {
            App(notesViewModel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkManager.unregisterNetworkCallback()
    }

    /* ------------------- */
    private fun setupNotesViewModel(): NotesViewModel {
        val localDB = LocalDB(this)
        val localNotes = NotesController(localDB)
        val localActions = ActionsController(localDB)

        val notesApi = RetrofitClient.noteService;

        val notesRepository = NotesRepository(notesApi, localNotes, localActions) { networkManager.isOnline() }

        val handleError: (Exception) -> Unit = { exception ->
            exception.printStackTrace()
            Toast.makeText(this, exception.message ?: "An error occurred", Toast.LENGTH_SHORT).show()
        }

        return NotesViewModel(notesRepository, handleError)
    }
}
