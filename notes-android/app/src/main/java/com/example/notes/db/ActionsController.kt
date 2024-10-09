package com.example.notes.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.example.notes.model.Action
import com.example.notes.model.Note

class ActionsController(private val localDB: LocalDB) {

    fun insert(action: Action) {
        val db = localDB.writableDatabase
        try {
            db.insert("actions", null, actionToContentValue(action))
        } catch (e: SQLiteException) {
            throw DatabaseException("Failed to insert action for noteId: ${action.noteId}", e)
        } finally {
            db.close()
        }
    }

    fun deleteAll() {
        val db = localDB.writableDatabase
        try {
            db.execSQL("DELETE FROM actions")
        } catch (e: SQLiteException) {
            throw DatabaseException("Failed to delete all actions", e)
        } finally {
            db.close()
        }
    }


    fun getAll(): List<Action> {
        val db = localDB.readableDatabase
        val actions = mutableListOf<Action>()
        try {
            val cursor = db.rawQuery("SELECT * FROM actions ORDER BY created_at ASC", null)
            cursor.use {
                while (it.moveToNext()) {
                    val note = cursorToAction(it)
                    actions.add(note)
                }
            }
            return actions
        } catch (e: SQLiteException) {
            throw DatabaseException("Failed to retrieve actions", e)
        } finally {
            db.close()
        }
    }

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
