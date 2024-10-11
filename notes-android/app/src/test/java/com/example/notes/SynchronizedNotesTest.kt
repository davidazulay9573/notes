package com.example.notes


import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.notes.data.api.NoteService
import com.example.notes.data.localdb.ActionsController
import com.example.notes.data.localdb.NotesController
import com.example.notes.data.synchronized_data.SynchronizedNotes
import com.example.notes.model.Action
import com.example.notes.model.Note
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@RunWith(JUnit4::class)
class SynchronizedNotesTest {

    private lateinit var apiNotes: NoteService
    private lateinit var localNotes: NotesController
    private lateinit var localActions: ActionsController
    private lateinit var synchronizedNotes: SynchronizedNotes

    @Before
    fun setup() {
        apiNotes = mock(NoteService::class.java)
        localNotes = mock(NotesController::class.java)
        localActions = mock(ActionsController::class.java)
    }

    @Test
    fun addNoteOnline(): Unit = runBlocking {

    }

    @Test
    fun addNoteOfflineThenSync() = runBlocking {

    }

}
