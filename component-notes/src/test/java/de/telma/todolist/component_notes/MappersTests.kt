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

        val noteEntity = NoteEntity(
            id = 0L,
            title = "Note",
            status = NoteStatus.IN_PROGRESS.statusValue
        )

        val taskEntities = listOf<NoteTaskEntity>(
            NoteTaskEntity(
                id = 1L,
                noteId = 0L,
                title = "Task1",
                status = NoteTaskStatus.IN_PROGRESS.statusValue
            ),
            NoteTaskEntity(
                id = 2L,
                noteId = 0L,
                title = "Task2",
                status = NoteTaskStatus.COMPLETE.statusValue
            )
        )

        val testNoteWithTasks = NoteWithTasks(
            note = noteEntity,
            tasks = taskEntities
        )

        val expectedNote = Note(
            id = 0L,
            title = "Note",
            status = NoteStatus.IN_PROGRESS,
            tasksList = listOf (
                NoteTask(
                    id = 1L,
                    title = "Task1",
                    status = NoteTaskStatus.IN_PROGRESS
                ),
                NoteTask(
                    id = 2L,
                    title = "Task2",
                    status = NoteTaskStatus.COMPLETE
                )
            )
        )

        val resultNote = testNoteWithTasks.toNote()
        assertEquals(expectedNote, resultNote)

    }

    @Test
    fun `maps Note to NoteEntity`() = runTest {
        val testNote = Note(
            id = 0L,
            title = "Note",
            status = NoteStatus.IN_PROGRESS,
            tasksList = listOf (
                NoteTask(
                    id = 1L,
                    title = "Task1",
                    status = NoteTaskStatus.IN_PROGRESS
                ),
                NoteTask(
                    id = 2L,
                    title = "Task2",
                    status = NoteTaskStatus.COMPLETE
                )
            )
        )

        val expectedNoteEntity = NoteEntity(
            id = 0L,
            title = "Note",
            status = NoteStatus.IN_PROGRESS.statusValue
        )

        val result = testNote.toNoteEntity()
        assertEquals(expectedNoteEntity, result)
    }

    @Test
    fun `maps NoteTask to NoteTaskEntity`() = runTest {
        val testTask = NoteTask(
            id = 1L,
            title = "Task1",
            status = NoteTaskStatus.IN_PROGRESS
        )

        val testParentId = 0L

        val expectedNoteTaskEntity = NoteTaskEntity(
            id = 1L,
            noteId = 0L,
            title = "Task1",
            status = NoteTaskStatus.IN_PROGRESS.statusValue
        )

        val result = testTask.toNoteTaskEntity(testParentId)
        assertEquals(expectedNoteTaskEntity, result)
    }
}