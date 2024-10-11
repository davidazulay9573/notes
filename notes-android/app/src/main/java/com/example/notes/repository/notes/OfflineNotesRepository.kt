package com.example.notes.repository.notes

import com.example.notes.data.localdb.ActionsController
import com.example.notes.data.localdb.NotesController
import com.example.notes.model.Action
import com.example.notes.model.Note

internal class OfflineNotesRepository(
    private val localNotes: NotesController,
    private val localActions: ActionsController
) {
    fun add(note: Note) {
        val noteId = generateLocalId()
        localNotes.insert(note.copy(id = noteId))
        localActions.insertOrReplace(Action(noteId, "insert" ))
    }

    fun update(note: Note) {
        val existingAction = localActions.get(note.id)
        if (existingAction == null || existingAction.type != "insert"){
            localActions.insertOrReplace(Action(note.id, "update"))
        }
        localNotes.update(note)
    }

    fun delete(id: String) {
        localNotes.delete(id)

        if (!id.startsWith("local_")){
            localActions.insertOrReplace(Action(id, "delete"))
        }else {
            localActions.delete(id)
        }
    }

    /* ---------------------- */
    private fun generateLocalId(): String {
        return "local_${System.currentTimeMillis()}"
    }
}
