package com.example.notes

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.notes.db.LocalDB
import com.example.notes.db.DatabaseException
import com.example.notes.db.NotesController
import com.example.notes.network.NetworkUtils
import com.example.notes.network.api.RetrofitClient
import com.example.notes.repository.NotesRepository
import com.example.notes.viewmodel.NotesViewModel

class MainActivity : ComponentActivity() {
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: MyNetworkCallback
    private lateinit var notesViewModel: NotesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val localDB = LocalDB(this)
        val notesController = NotesController(localDB)

        val networkUtils = NetworkUtils(this)
        val notesRepository = NotesRepository(RetrofitClient.noteService, notesController, { networkUtils.isOnline() })

        val handleDBError: (DatabaseException) -> Unit = { exception ->
            Toast.makeText(this, exception.message ?: "An error occurred", Toast.LENGTH_SHORT).show()
        }

        notesViewModel = NotesViewModel(notesRepository, handleDBError)

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = MyNetworkCallback(notesViewModel)

        registerNetworkCallback()

        setContent {
            App(notesViewModel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun registerNetworkCallback() {
        val builder = NetworkRequest.Builder()
        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
    }

    class MyNetworkCallback(private val notesViewModel: NotesViewModel) : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            notesViewModel.syncNotes()
        }

        override fun onLost(network: Network) {
            super.onLost(network)
        }
    }
}
