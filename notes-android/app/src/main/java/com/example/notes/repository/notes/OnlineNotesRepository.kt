package com.example.notes.repository.notes

import com.example.notes.data.api.NoteService
import com.example.notes.data.localdb.NotesController
import com.example.notes.model.Note

class OnlineNotesRepository(
    private val apiNotes: NoteService,
    private val localNotes: NotesController
) {
    suspend fun add(note: Note) {
        val noteServer = apiNotes.create(note)
        localNotes.insert(noteServer)
    }

    suspend fun update(note: Note) {
        apiNotes.update(note.id, note)
        localNotes.update(note)
    }

    suspend fun delete(id: String) {
        apiNotes.delete(id)
        localNotes.delete(id)
    }
}
