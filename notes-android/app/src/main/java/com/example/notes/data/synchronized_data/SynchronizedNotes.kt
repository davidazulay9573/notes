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
                val noteId = generateLocalId()
                localNotes.insert(note.copy(id = noteId))
                localActions.insert(Action(noteId, "inserts" ))
            }
        }catch (e : Exception){
            val noteId = generateLocalId()
            localNotes.insert(note.copy(id = noteId))
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
                localActions.delete(id, "inserts")
                localActions.delete(id, "updates")
                if (!id.startsWith("local_")){
                    localActions.insert(Action(id, "deletes"))
                }
              
            }
        }catch (e : Exception){
            localNotes.delete(id)
            localActions.insert(Action(id, "deletes"))
            localActions.delete(id, "inserts")
            localActions.delete(id, "updates")
            if (!id.startsWith("local_")){
                localActions.insert(Action(id, "deletes"))
            }
        }
    }

    suspend fun syncNotes() {
        if (isOnline()) {
            try {
                val insertNoteIds: List<String> = localActions.getAll("inserts")
                insertNoteIds.forEach { noteId ->
                    val note = localNotes.get(noteId)
                    note?.let {
                        val noteServer = apiNotes.create(it)
                        localNotes.delete(noteId)
                        localNotes.insert(noteServer)
                    }
                    localActions.delete(noteId, "inserts")
                }

                val updateNoteIds: List<String> = localActions.getAll("updates")
                updateNoteIds.forEach { noteId ->
                    val note = localNotes.get(noteId)
                    note?.let {
                        apiNotes.update(it.id, it)
                    }
                    localActions.delete(noteId, "updates")
                }

                val deleteNoteIds: List<String> = localActions.getAll("deletes")
                deleteNoteIds.forEach { noteId ->
                    apiNotes.delete(noteId)
                    localActions.delete(noteId, "deletes")
                }
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /* ---------------------------- */
    private fun generateLocalId(): String {
        return "local_${System.currentTimeMillis()}"
    }
}
