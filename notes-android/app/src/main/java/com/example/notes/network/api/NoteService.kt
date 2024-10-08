package com.example.notes.network.api

import com.example.notes.model.Note
import retrofit2.http.*

interface NoteService {
    @GET("notes")
    suspend fun getAllNotes(): List<Note>

    @POST("notes")
    suspend fun create(@Body note: Note) : Note

    @PUT("notes/{id}")
    suspend fun update(@Path("id") id: String, @Body note: Note)

    @DELETE("notes/{id}")
    suspend fun delete(@Path("id") id: String)
}

