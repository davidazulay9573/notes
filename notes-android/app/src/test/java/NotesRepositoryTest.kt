package com.example.notes

import com.example.notes.data.api.NoteService
import com.example.notes.data.localdb.ActionsController
import com.example.notes.data.localdb.NotesController
import com.example.notes.model.Note
import com.example.notes.repository.notes.NotesRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.argThat
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(JUnit4::class)
class NotesRepositoryTest {

    private lateinit var apiNotes: NoteService
    private lateinit var localNotes: NotesController
    private lateinit var localActions: ActionsController
    private lateinit var notesRepository: NotesRepository

    @Before
    fun setup() {
        apiNotes = mock(NoteService::class.java)
        localNotes = mock(NotesController::class.java)
        localActions = mock(ActionsController::class.java)
        notesRepository = NotesRepository(apiNotes, localNotes, localActions) { true }
    }


    @Test
    fun addNoteOnlineThenSync(): Unit = runBlocking {
        val note = Note( title = "note", description = "note")

        notesRepository = NotesRepository(apiNotes, localNotes, localActions) { true }
        notesRepository.addNote(note)

        verify(apiNotes).create(note)
        verify(localNotes).insert(note)
    }

    @Test
    fun addNoteOfflineThenSync(): Unit = runBlocking {
        val note = Note( title = "note", description = "note")

        notesRepository = NotesRepository(apiNotes, localNotes, localActions) { false }
        notesRepository.addNote(note)

        verify(localNotes).insert(note)
//        verify(localActions).insertOrReplace()
    }
}
