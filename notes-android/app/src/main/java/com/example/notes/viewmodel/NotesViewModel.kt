package com.example.notes.viewmodel

import com.example.notes.model.Note
import com.example.notes.repository.NotesRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.db.DatabaseException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotesViewModel(
    private val notesRepository: NotesRepository,
    private val handleDBError: (e: DatabaseException) -> Unit
) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes = _notes.asStateFlow()

    init {
        fetchNotes()
    }

    private fun fetchNotes() {
        viewModelScope.launch {
            try {
                _notes.value = notesRepository.getAllNotes()
            } catch (e: DatabaseException) {
                handleDBError(e)
            }
        }
    }

    fun add(title: String, description: String) {
        viewModelScope.launch {
            try {
                val newNote = Note(title = title, description = description)
                notesRepository.addNote(newNote)
                fetchNotes()
            } catch (e: DatabaseException) {
                handleDBError(e)
            }
        }
    }

    fun update(note: Note) {
        viewModelScope.launch {
            try {
                notesRepository.updateNote(note)
                fetchNotes()
            } catch (e: DatabaseException) {
                handleDBError(e)
            }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            try {
                notesRepository.deleteNote(id)
                fetchNotes()
            } catch (e: DatabaseException) {
                handleDBError(e)
            }
        }
    }

    fun search(searchQuery: String, searchBy: String) {
        viewModelScope.launch {
            try {
                val allNotes = notesRepository.getAllNotes()
                _notes.value = when (searchBy) {
                    "title" -> allNotes.filter { note -> note.title.contains(searchQuery, ignoreCase = true) }
                    "description" -> allNotes.filter { note -> note.description.contains(searchQuery, ignoreCase = true) }
                    else -> allNotes
                }
            } catch (e: DatabaseException) {
                handleDBError(e)
            }
        }
    }

    fun syncNotes() {
        viewModelScope.launch {
            try {
                notesRepository.syncNotes()
            } catch (e: Exception) {

            }
        }
    }
}
