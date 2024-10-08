package com.example.notes.db

import android.content.ContentValues
import android.database.sqlite.SQLiteException
import com.example.notes.model.Note

class NotesController(private val localDB: LocalDB) {

    fun insert(note: Note)  {
        val db = localDB.writableDatabase
        try {
            val noteToInsert = if (note.id == "0") note.copy(id = generateLocalId()) else note
            val contentValues = noteToContentValue(noteToInsert)
            db.insertOrThrow("notes", null, contentValues)
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

    fun getPendingNotes(): List<Note> {
        val db = localDB.readableDatabase
        val notes = mutableListOf<Note>()
        try {
            val cursor = db.rawQuery("SELECT * FROM notes WHERE isPending = 1", null)
            cursor.use {
                while (it.moveToNext()) {
                    val note = cursorToNote(it)
                    notes.add(note)
                }
            }
            return notes
        } catch (e: SQLiteException) {
            throw DatabaseException("Failed to retrieve pending notes", e)
        } finally {
            db.close()
        }
    }

    fun getPendingDeleteNotes(): List<Note> {
        val db = localDB.readableDatabase
        val notes = mutableListOf<Note>()
        try {
            val cursor = db.rawQuery("SELECT * FROM notes WHERE isPendingDelete = 1", null)
            cursor.use {
                while (it.moveToNext()) {
                    val note = cursorToNote(it)
                    notes.add(note)
                }
            }
            return notes
        } catch (e: SQLiteException) {
            throw DatabaseException("Failed to retrieve notes pending delete", e)
        } finally {
            db.close()
        }
    }

    fun getPendingUpdateNotes(): List<Note> {
        val db = localDB.readableDatabase
        val notes = mutableListOf<Note>()
        try {
            val cursor = db.rawQuery("SELECT * FROM notes WHERE isPendingUpdate = 1", null)
            cursor.use {
                while (it.moveToNext()) {
                    val note = cursorToNote(it)
                    notes.add(note)
                }
            }
            return notes
        } catch (e: SQLiteException) {
            throw DatabaseException("Failed to retrieve notes pending update", e)
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

    fun markPendingDelete(id: String) {
        val db = localDB.writableDatabase
        try {
            val contentValues = ContentValues().apply {
                put("isPendingDelete", 1)
            }

            db.update("notes", contentValues, "id = ?", arrayOf(id))

        } catch (e: SQLiteException) {
            throw DatabaseException("Error marking note as pending delete", e)
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
            put("isPending", if (note.isPending) 1 else 0)
            put("isPendingDelete", if (note.isPendingDelete) 1 else 0)
            put("isPendingUpdate", if (note.isPendingUpdate) 1 else 0)
        }
    }

    private fun cursorToNote(cursor: android.database.Cursor): Note {
        val id = cursor.getString(cursor.getColumnIndexOrThrow("id"))
        val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
        val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
        val isPending = cursor.getInt(cursor.getColumnIndexOrThrow("isPending")) == 1
        val isPendingDelete = cursor.getInt(cursor.getColumnIndexOrThrow("isPendingDelete")) == 1 // Retrieve isPendingDelete
        val isPendingUpdate = cursor.getInt(cursor.getColumnIndexOrThrow("isPendingUpdate")) == 1 // Retrieve isPendingUpdate
        return Note(id, title, description, isPending, isPendingDelete, isPendingUpdate) // Include all fields
    }

    private fun generateLocalId(): String {
        return "local_${System.currentTimeMillis()}"
    }

}
