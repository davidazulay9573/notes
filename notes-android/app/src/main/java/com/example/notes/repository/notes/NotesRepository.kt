package com.example.notes.repository.notes
import com.example.notes.data.localdb.ActionsController
import com.example.notes.data.localdb.NotesController
import com.example.notes.model.Note
import com.example.notes.data.api.NoteService
import com.example.notes.model.Action

class NotesRepository(
    private val apiNotes: NoteService,
    private val localNotes: NotesController,
    private val localActions: ActionsController,
    private val isOnline: () -> Boolean
) {
    private val onlineNotesRepository = OnlineNotesRepository(apiNotes, localNotes)
    private val offlineNotesRepository = OfflineNotesRepository(localNotes, localActions)

    fun getAllNotes(): List<Note> {
        return localNotes.getAll()
    }

    suspend fun addNote(note: Note) {
        try {
            if (isOnline()) {
                onlineNotesRepository.add(note)
            } else {
                offlineNotesRepository.add(note)
            }
        } catch (e: Exception) {
            offlineNotesRepository.add(note)
        }
    }

    suspend fun updateNote(note: Note) {
        try {
            if (isOnline()) {
                onlineNotesRepository.update(note)
            } else {
                offlineNotesRepository.update(note)
            }
        } catch (e: Exception) {
            offlineNotesRepository.update(note)
        }
    }

    suspend fun deleteNote(id: String) {
        try {
            if (isOnline()) {
                onlineNotesRepository.delete(id)
            } else {
                offlineNotesRepository.delete(id)
            }
        } catch (e: Exception) {
            offlineNotesRepository.delete(id)
        }
    }

    suspend fun syncNotes() {
        if (isOnline()) {
            val actions: List<Action> = localActions.getAll()

            actions.forEach { action ->
                try {
                    when (action.type) {
                        "insert" -> syncInsert(action.noteId)
                        "update" -> syncUpdate(action.noteId)
                        "delete" -> syncDelete(action.noteId)
                    }
                    localActions.delete(action.noteId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /* ---------------------- */
    private suspend fun syncInsert(noteId: String) {
        val note = localNotes.get(noteId)
        note?.let {
            val noteServer = apiNotes.create(it)
            localNotes.delete(noteId)
            localNotes.insert(noteServer)
        }
    }

    private suspend fun syncUpdate(noteId: String) {
        val note = localNotes.get(noteId)
        note?.let {
            apiNotes.update(it.id, it)
        }
    }

    private suspend fun syncDelete(noteId: String) {
        apiNotes.delete(noteId)
    }
}
