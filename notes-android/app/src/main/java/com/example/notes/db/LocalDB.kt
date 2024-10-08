package com.example.notes.db

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
                    description TEXT NOT NULL,
                    isPending INTEGER DEFAULT 0,
                    isPendingUpdate INTEGER DEFAULT 0,
                    isPendingDelete INTEGER DEFAULT 0
                )
            """)
        } catch (e: SQLiteException) {
            throw DatabaseException("Error creating notes table", e)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            db.execSQL("DROP TABLE IF EXISTS notes")
            onCreate(db)
        } catch (e: SQLiteException) {
            throw DatabaseException("Error upgrading database from version $oldVersion to $newVersion", e)
        }
    }
}
