package com.example.notes.data.localdb

import android.content.ContentValues
import com.example.notes.model.Note

class NotesController(private val localDB: LocalDB) {

    fun insert(note: Note): String {
        val db = localDB.writableDatabase
        val noteToInsert = if (note.id == "0") note.copy(id = generateLocalId()) else note
        val contentValues = noteToContentValue(noteToInsert)
        db.insertOrThrow("notes", null, contentValues)
        db.close()
        return noteToInsert.id
    }

    fun get(noteId: String): Note? {
        val db = localDB.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM notes WHERE id = ?", arrayOf(noteId))
        cursor.use {
            if (!it.moveToFirst()) return null
            return cursorToNote(it)
        }
    }

    fun getAll(): List<Note> {
        val db = localDB.readableDatabase
        val notes = mutableListOf<Note>()
        val cursor = db.rawQuery("SELECT * FROM notes", null)
        cursor.use {
            while (it.moveToNext()) {
                val note = cursorToNote(it)
                notes.add(note)
            }
        }
        db.close()
        return notes
    }

    fun update(note: Note) {
        val db = localDB.writableDatabase
        val contentValues = noteToContentValue(note)
        db.update("notes", contentValues, "id = ?", arrayOf(note.id))
        db.close()
    }

    fun delete(id: String) {
        val db = localDB.writableDatabase
        db.execSQL("DELETE FROM notes WHERE id = ?", arrayOf(id))
        db.close()
    }

    /* ------------------------------- */
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
