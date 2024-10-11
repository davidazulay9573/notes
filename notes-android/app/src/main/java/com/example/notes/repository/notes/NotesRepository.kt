package com.example.notes.repository.notes
import com.example.notes.data.localdb.ActionsController
import com.example.notes.data.localdb.NotesController
import com.example.notes.model.Note
import com.example.notes.data.api.NoteService

class NotesRepository(
    private val apiNotes: NoteService,
    private val localNotes: NotesController,
    private val localActions: ActionsController,
    private val isOnline: () -> Boolean
) {
    private val offlineNotesRepository = OfflineNotesRepository(localNotes, localActions)
    private val onlineNotesRepository = OnlineNotesRepository(apiNotes, localNotes)

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
            try {
                syncInsertedNotes()
                syncUpdatedNotes()
                syncDeletedNotes()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /* ---------------------------- */
    private suspend fun syncInsertedNotes() {
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
    }

    private suspend fun syncUpdatedNotes() {
        val updateNoteIds: List<String> = localActions.getAll("updates")
        updateNoteIds.forEach { noteId ->
            val note = localNotes.get(noteId)
            note?.let {
                apiNotes.update(it.id, it)
            }
            localActions.delete(noteId, "updates")
        }
    }

    private suspend fun syncDeletedNotes() {
        val deleteNoteIds: List<String> = localActions.getAll("deletes")
        deleteNoteIds.forEach { noteId ->
            apiNotes.delete(noteId)
            localActions.delete(noteId, "deletes")
        }
    }
}
