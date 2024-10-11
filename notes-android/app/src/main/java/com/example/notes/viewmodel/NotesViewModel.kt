package com.example.notes.viewmodel

import com.example.notes.model.Note
import com.example.notes.data.synchronized_data.SynchronizedNotes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotesViewModel(
    private val notesRepository: SynchronizedNotes,
    private val handleError: (e: Exception) -> Unit
) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes = _notes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        fetchNotes()
    }

    fun get(id: String): Note? {
        return _notes.value.find { it.id == id }
    }

    fun add(title: String, description: String, onComplete: (() -> Unit)? = null ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newNote = Note(title = title, description = description)
                notesRepository.addNote(newNote)
                fetchNotes()
                onComplete?.invoke()
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun update(note: Note, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                notesRepository.updateNote(note)
                fetchNotes()
                onComplete?.invoke()
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun delete(id: String, onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                notesRepository.deleteNote(id)
                fetchNotes()
                onComplete?.invoke()
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun search(searchQuery: String, searchBy: String) {
            _isLoading.value = true
            try {
                val allNotes = notesRepository.getAllNotes()
                _notes.value = when (searchBy) {
                    "title" -> allNotes.filter { it.title.contains(searchQuery, ignoreCase = true) }
                    "description" -> allNotes.filter { it.description.contains(searchQuery, ignoreCase = true) }
                    else -> allNotes
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _isLoading.value = false
            }
    }

    fun syncNotes(onComplete: (() -> Unit)? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                notesRepository.syncNotes()
                fetchNotes()
                onComplete?.invoke()
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /* ------------------------------ */
    private fun fetchNotes() {
        _isLoading.value = true
        try {
            _notes.value = notesRepository.getAllNotes()
        } catch (e: Exception) {
            handleError(e)
        } finally {
            _isLoading.value = false
        }
    }

}
