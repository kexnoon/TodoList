package de.telma.todolist.component_notes

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.storage.database.entity.NoteEntity
import de.telma.todolist.storage.database.entity.NoteTaskEntity
import de.telma.todolist.storage.database.entity.NoteWithTasks
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class MappersTests {

    @Test
    fun `maps NotesWithTasks to Note`() = runTest {

        val taskEntities = listOf<NoteTaskEntity>(
            sampleTaskEntity1.copy(),
            sampleTaskEntity2.copy()
        )
        val testNoteWithTasks = NoteWithTasks(
            note = sampleNoteEntity.copy(),
            tasks = taskEntities
        )
        val expectedNote = sampleNote.copy()

        val resultNote = testNoteWithTasks.toNote()

        assertEquals(expectedNote, resultNote)

    }

    @Test
    fun `maps Note to NoteEntity`() = runTest {
        val testNote = sampleNote.copy()
        val expectedNoteEntity = sampleNoteEntity.copy()

        val result = testNote.toNoteEntity()

        assertEquals(expectedNoteEntity, result)
    }

    @Test
    fun `maps NoteTask to NoteTaskEntity`() = runTest {
        val testTask = sampleTask1.copy()
        val testParentId = 0L
        val expectedNoteTaskEntity = sampleTaskEntity1.copy()

        val result = testTask.toNoteTaskEntity(testParentId)

        assertEquals(expectedNoteTaskEntity, result)
    }
}

private val sampleTaskEntity1 = NoteTaskEntity(
    id = 1L,
    noteId = 0L,
    title = "Task1",
    status = NoteTaskStatus.IN_PROGRESS.statusValue
)

private val sampleTaskEntity2 = NoteTaskEntity(
    id = 2L,
    noteId = 0L,
    title = "Task2",
    status = NoteTaskStatus.COMPLETE.statusValue
)

private val sampleNoteEntity = NoteEntity(
    id = 0L,
    title = "Note",
    status = NoteStatus.IN_PROGRESS.statusValue
)

private val sampleTask1 = NoteTask(
    id = 1L,
    title = "Task1",
    status = NoteTaskStatus.IN_PROGRESS
)

private val sampleTask2 = NoteTask(
    id = 2L,
    title = "Task2",
    status = NoteTaskStatus.COMPLETE
)

private val sampleNote = Note(
    id = 0L,
    title = "Note",
    status = NoteStatus.IN_PROGRESS,
    tasksList = listOf(sampleTask1, sampleTask2)
)