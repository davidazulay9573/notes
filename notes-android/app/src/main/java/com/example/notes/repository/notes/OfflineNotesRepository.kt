package com.example.notes.repository.notes

import com.example.notes.data.localdb.ActionsController
import com.example.notes.data.localdb.NotesController
import com.example.notes.model.Action
import com.example.notes.model.Note

class OfflineNotesRepository(
    private val localNotes: NotesController,
    private val localActions: ActionsController
) {
    fun add(note: Note) {
        val noteId = generateLocalId()
        localNotes.insert(note.copy(id = noteId))
        localActions.insert(Action(noteId, "inserts" ))
    }

    fun update(note: Note) {
        localNotes.update(note)
        localActions.insert(Action(note.id, "updates"))
    }

    fun delete(id: String) {
        localNotes.delete(id)
        localActions.delete(id, "inserts")
        localActions.delete(id, "updates")
        if (!id.startsWith("local_")){
            localActions.insert(Action(id, "deletes"))
        }
    }

    private fun generateLocalId(): String {
        return "local_${System.currentTimeMillis()}"
    }
}
