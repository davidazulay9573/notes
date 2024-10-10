package com.example.notes.data.localdb

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class LocalDB(context: Context) : SQLiteOpenHelper(context, "notes.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL("""
                CREATE TABLE notes (
                    id TEXT PRIMARY KEY NOT NULL,
                    title TEXT NOT NULL,
                    description TEXT NOT NULL
                )
            """)

            db.execSQL("""
                CREATE TABLE inserts (
                    noteId TEXT PRIMARY KEY NOT NULL,
                    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """)

            db.execSQL("""
                CREATE TABLE updates (
                    noteId TEXT PRIMARY KEY NOT NULL,
                    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """)

            db.execSQL("""
                CREATE TABLE deletes (
                    noteId TEXT PRIMARY KEY NOT NULL,
                    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """)
        } catch (e: SQLiteException) {
            throw Exception("Error creating notes table", e)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            db.execSQL("DROP TABLE IF EXISTS notes")
            onCreate(db)
        } catch (e: SQLiteException) {
            throw Exception("Error upgrading database from version $oldVersion to $newVersion", e)
        }
    }
}
