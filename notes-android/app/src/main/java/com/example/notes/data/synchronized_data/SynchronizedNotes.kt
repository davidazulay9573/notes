package com.example.notes.data.synchronized_data

import com.example.notes.data.localdb.ActionsController
import com.example.notes.data.localdb.NotesController
import com.example.notes.model.Action
import com.example.notes.model.Note
import com.example.notes.data.api.NoteService

class SynchronizedNotes(
    private val apiNotes : NoteService,
    private val localNotes : NotesController,
    private val localActions : ActionsController,
    private val isOnline: () -> Boolean
) {
    fun getAllNotes(): List<Note> {
        return localNotes.getAll()
    }

    suspend fun addNote(note: Note) {
        try {
            if (isOnline()){
                val noteServer = apiNotes.create(note)
                localNotes.insert(noteServer)
            }else {
                val noteId = localNotes.insert(note)
                localActions.insert(Action(noteId, "inserts" ))
            }
        }catch (e : Exception){
            val noteId = localNotes.insert(note)
            localActions.insert(Action(noteId, "inserts" ))
        }
    }

    suspend fun updateNote(note: Note) {
        try {
            if (isOnline()){
                apiNotes.update(note.id, note)
                localNotes.update(note)
            }else {
                localNotes.update(note)
                localActions.insert(Action(note.id, "updates"))
            }
        }catch (e : Exception){
            localNotes.update(note)
            localActions.insert(Action(note.id, "updates"))
        }
    }

    suspend fun deleteNote(id: String) {
        try {
            if (isOnline()){
                apiNotes.delete(id)
                localNotes.delete(id)
            }else {
                localNotes.delete(id)
                localActions.insert(Action(id, "deletes"))
            }
        }catch (e : Exception){
            localNotes.delete(id)
            localActions.insert(Action(id, "deletes"))
        }
    }

    suspend fun syncNotes() {
        if (isOnline()) {
            val insertNoteIds: List<String> = localActions.getAll("inserts")
            insertNoteIds.forEach { noteId ->
                try {
                    val note = localNotes.get(noteId)
                    note?.let {
                        val noteServer = apiNotes.create(it)
                        localNotes.delete(noteId)
                        localNotes.insert(noteServer)
                    }
                    localActions.delete(noteId, "inserts")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val updateNoteIds: List<String> = localActions.getAll("updates")
            updateNoteIds.forEach { noteId ->
                try {
                    val note = localNotes.get(noteId)
                    note?.let {
                        apiNotes.update(it.id, it)
                    }
                    localActions.delete(noteId, "updates")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val deleteNoteIds: List<String> = localActions.getAll("deletes")
            deleteNoteIds.forEach { noteId ->
                try {
                    apiNotes.delete(noteId)
                    localActions.delete(noteId, "deletes")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}
