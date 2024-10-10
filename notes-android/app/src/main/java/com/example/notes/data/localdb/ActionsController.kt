package com.example.notes.data.localdb

import android.content.ContentValues
import com.example.notes.model.Action

class ActionsController(private val localDB: LocalDB) {

    fun insert(action: Action) {
        val db = localDB.writableDatabase
        db.insert("actions", null, actionToContentValue(action))
        db.close()
    }

    fun deleteAll() {
        val db = localDB.writableDatabase
        db.execSQL("DELETE FROM actions")
        db.close()
    }

    fun getAll(): List<Action> {
        val db = localDB.readableDatabase
        val actions = mutableListOf<Action>()
        val cursor = db.rawQuery("SELECT * FROM actions ORDER BY createdAt ASC", null)
        cursor.use {
            while (it.moveToNext()) {
                val action = cursorToAction(it)
                actions.add(action)
            }
        }
        db.close()
        return actions
    }

    /* ----------------------------- */
    private fun actionToContentValue(action: Action): ContentValues {
        return ContentValues().apply {
            put("noteId", action.noteId)
            put("type", action.type)
        }
    }

    private fun cursorToAction(cursor: android.database.Cursor): Action {
        val noteId = cursor.getString(cursor.getColumnIndexOrThrow("noteId"))
        val type = cursor.getString(cursor.getColumnIndexOrThrow("type"))
        return Action(noteId, type)
    }
}
