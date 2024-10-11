package com.example.notes.data.localdb

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.notes.model.Action

class ActionsController(private val localDB: LocalDB) {

    fun insert(action: Action) {
        val db = localDB.writableDatabase
        when (action.type) {
            "inserts" -> {
                db.insertWithOnConflict("inserts", null, actionToContentValue(action), SQLiteDatabase.CONFLICT_REPLACE)
            }
            "updates" -> {
                db.insertWithOnConflict("updates", null, actionToContentValue(action), SQLiteDatabase.CONFLICT_REPLACE)
            }
            "deletes" -> {
                db.insertWithOnConflict("deletes", null, actionToContentValue(action), SQLiteDatabase.CONFLICT_REPLACE)
            }
            else -> throw IllegalArgumentException("Unknown action type: ${action.type}")
        }
        db.close()
    }

    fun delete(noteId : String, table : String) {
        val db = localDB.writableDatabase
        db.delete(table, "noteId=?", arrayOf(noteId))
        db.close()
    }

    fun getAll(table: String): List<String> {
        val db = localDB.readableDatabase
        val noteIds = mutableListOf<String>()
        val cursor = db.rawQuery("SELECT * FROM $table ORDER BY createdAt ASC", null)
        cursor.use {
            while (it.moveToNext()) {
                val noteId = extractNoteIdFromCursor(it)
                noteIds.add(noteId)
            }
        }
        db.close()
        return noteIds
    }

    /* ----------------------------- */
    private fun actionToContentValue(action: Action): ContentValues {
        return ContentValues().apply {
            put("noteId", action.noteId)
        }
    }

    private fun extractNoteIdFromCursor(cursor: android.database.Cursor): String {
        return cursor.getString(cursor.getColumnIndexOrThrow("noteId"))
    }
}


