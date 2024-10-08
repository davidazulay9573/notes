package com.example.notes.db

import android.content.ContentValues
import android.database.sqlite.SQLiteException
import com.example.notes.model.Note

class NotesController(private val localDB: LocalDB) {

    fun insert(note: Note) : String  {
        val db = localDB.writableDatabase
        try {
            val noteToInsert = if (note.id == "0") note.copy(id = generateLocalId()) else note
            val contentValues = noteToContentValue(noteToInsert)
            db.insertOrThrow("notes", null, contentValues)
            return noteToInsert.id;
        } catch (e: SQLiteException) {
            throw DatabaseException("Failed to insert note", e)
        } finally {
            db.close()
        }
    }

    fun get(noteId: String): Note? {
        val db = localDB.readableDatabase
        try {
            val cursor = db.rawQuery("SELECT * FROM notes WHERE id = ?", arrayOf(noteId))
            cursor.use {
                if (!it.moveToFirst()) return null
                return cursorToNote(it)
            }
        } catch (e: SQLiteException) {
            throw DatabaseException("Failed to retrieve note with id: $noteId", e)
        } finally {
            db.close()
        }
    }

    fun getAll(): List<Note> {
        val db = localDB.readableDatabase
        val notes = mutableListOf<Note>()
        try {
            val cursor = db.rawQuery("SELECT * FROM notes ORDER BY isPending DESC", null)
            cursor.use {
                while (it.moveToNext()) {
                    val note = cursorToNote(it)
                    notes.add(note)
                }
            }
            return notes
        } catch (e: SQLiteException) {
            throw DatabaseException("Failed to retrieve notes", e)
        } finally {
            db.close()
        }
    }

    fun update(note: Note) {
        val db = localDB.writableDatabase
        try {
            val contentValues = noteToContentValue(note)
            db.update("notes", contentValues, "id = ?", arrayOf(note.id))
        } catch (e: SQLiteException) {
            throw DatabaseException("Failed to update note with id: ${note.id}", e)
        } finally {
            db.close()
        }
    }

    fun delete(id: String) {
        val db = localDB.writableDatabase
        try {
            db.execSQL("DELETE FROM notes WHERE id = ?", arrayOf(id))
        } catch (e: SQLiteException) {
            throw DatabaseException("Failed to delete note with id: $id", e)
        } finally {
            db.close()
        }
    }

    /* ------------------------------------- */
    private fun noteToContentValue(note: Note): ContentValues {
        return ContentValues().apply {
            put("id", note.id)
            put("title", note.title)
            put("description", note.description)
        }
    }

    private fun cursorToNote(cursor: android.database.Cursor): Note {
        val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
        val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
        val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
        return Note(id, title, description)
    }

    private fun generateLocalId(): String {
        return "local_${System.currentTimeMillis()}"
    }

}
