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
import com.example.notes.data.synchronized_data.SynchronizedNotes
import com.example.notes.viewmodel.NotesViewModel

class MainActivity : ComponentActivity() {
    private lateinit var networkManager: NetworkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val notesViewModel = setNotesViewModel()

        networkManager = NetworkManager(this, notesViewModel)
        networkManager.registerNetworkCallback()

        setContent {
            App(notesViewModel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkManager.unregisterNetworkCallback()
    }

    /* ------------------- */
    private fun setNotesViewModel(): NotesViewModel {
        val localDB = LocalDB(this)
        val notesController = NotesController(localDB)
        val actionsController = ActionsController(localDB)

        val notesApi = RetrofitClient.noteService;

        val notesRepository = SynchronizedNotes(notesApi, notesController, actionsController) { networkManager.isOnline() }

        val handleError: (Exception) -> Unit = { exception ->
            Toast.makeText(this, exception.message ?: "An error occurred", Toast.LENGTH_SHORT).show()
        }

        return NotesViewModel(notesRepository, handleError)
    }
}
