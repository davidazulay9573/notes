package com.example.notes.repository

import com.example.notes.db.NotesController
import com.example.notes.model.Note
import com.example.notes.network.api.NoteService

class NotesRepository(
    private val apiNotes: NoteService,
    private val localNotes: NotesController,
    private val isOnline: () -> Boolean
) {

    suspend fun getAllNotes(): List<Note> {
        return localNotes.getAll().filter { note -> !note.isPendingDelete };
    }

    suspend fun addNote(note: Note) {
        try {
            if (isOnline()){
                val noteServer = apiNotes.create(note);
                localNotes.insert(noteServer)
            }else {
                localNotes.insert(note.copy(isPending = true))
            }
        }catch (e : Exception){
            localNotes.insert(note.copy(isPending = true))
        }
    }

    suspend fun updateNote(note: Note) {
        try {
            if (isOnline()){
                apiNotes.update(note.id, note)
                localNotes.update(note)
            }else {
                localNotes.update(note.copy(isPendingUpdate = true))
            }
        }catch (e : Exception){
            localNotes.update(note.copy(isPendingUpdate = true))
        }
    }

    suspend fun deleteNote(id: String) {
        try {
            if (isOnline()){
                apiNotes.delete(id)
                localNotes.delete(id)
            }else {
                localNotes.markPendingDelete(id)
            }
        }catch (e : Exception){
            localNotes.markPendingDelete(id)
        }
    }

    suspend fun syncNotes() {
        if (isOnline()) {
            val pendingDeleteNotes = localNotes.getPendingDeleteNotes()
            pendingDeleteNotes.forEach { note ->
                if (!note.isPending){
                    apiNotes.delete(note.id)
                }
                localNotes.delete(note.id)
            }

            val pendingUpdateNotes = localNotes.getPendingUpdateNotes()
            pendingUpdateNotes.forEach { note ->
                if (!note.isPending) {
                    apiNotes.update(note.id, note)
                }
                localNotes.update(note.copy(isPendingUpdate = false))
            }

            val pendingNotes = localNotes.getPendingNotes()
            pendingNotes.forEach { note ->
                    val serverNote = apiNotes.create(note)
                    localNotes.delete(note.id)
                    localNotes.insert(serverNote)
            }
        }
    }
}
