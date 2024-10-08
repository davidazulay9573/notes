package com.example.notes.repository

import android.database.sqlite.SQLiteException
import com.example.notes.db.ActionsController
import com.example.notes.db.NotesController
import com.example.notes.model.Action
import com.example.notes.model.Note
import com.example.notes.network.api.NoteService

class NotesRepository(
    private val apiNotes : NoteService,
    private val localNotes : NotesController,
    private val localActions : ActionsController,
    private val isOnline: () -> Boolean
) {

    fun getAllNotes(): List<Note> {
        return localNotes.getAll();
    }

    suspend fun addNote(note: Note) {
        try {
            if (isOnline()){
                val noteServer = apiNotes.create(note);
                localNotes.insert(noteServer)
            }else {
                val noteId = localNotes.insert(note)
                localActions.insert(Action(noteId, "insert" ))
            }
        }catch (e : Exception){
            val noteId = localNotes.insert(note)
            localActions.insert(Action(noteId, "insert" ))
        }
    }

    suspend fun updateNote(note: Note) {
        try {
            if (isOnline()){
                apiNotes.update(note.id, note)
                localNotes.update(note)
            }else {
                localNotes.update(note)
                localActions.insert(Action(note.id, "update"))
            }
        }catch (e : Exception){
            localNotes.update(note)
            localActions.insert(Action(note.id, "update"))
        }catch (e : SQLiteException){
            throw NotesRepositoryException("", e)
        }
    }

    suspend fun deleteNote(id: String) {
        try {
            if (isOnline()){
                apiNotes.delete(id)
                localNotes.delete(id)
            }else {
                localNotes.delete(id)
                localActions.insert(Action(id, "delete"))
            }
        }catch (e : Exception){
            localNotes.delete(id)
            localActions.insert(Action(id, "delete"))
        }
    }

    suspend fun syncNotes() {
        if (isOnline()) {
            val actions: List<Action> = localActions.getAll()

            actions.forEach { action ->
                try {
                    when (action.type) {
                        "insert" -> {
                            val note = localNotes.get(action.noteId)
                            note?.let {
                                val noteServer = apiNotes.create(it)
                                localNotes.update(noteServer)
                            }
                        }
                        "update" -> {
                            val note = localNotes.get(action.noteId)
                            note?.let {
                                apiNotes.update(it.id, it)
                            }
                        }
                        "delete" -> {
                            apiNotes.delete(action.noteId)
                        }
                    }
                    localActions.deleteAll()
                } catch (e: Exception) {

                    e.printStackTrace()
                }
            }
        }
    }

}
