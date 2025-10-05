package de.telma.todolist.component_notes.repository

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.utils.toNoteEntity
import de.telma.todolist.component_notes.utils.toNoteTaskEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TaskRepositoryTest: BaseRepositoryTest() {

    lateinit var repository: TaskRepository

    @Before
    override fun setUp() {
        super.setUp()
        repository = TaskRepositoryImpl(database)
    }

    @Test
    fun createNewTask_successfully_creates_task_with_correct_arguments_passed() = runTest {
        val note = noteWithNoTasks.copy()
        val task = noteTask.copy()
        database.noteDao().insertNote(note.toNoteEntity())

        val result = repository.createNewTask(note.id, task.title)
        assertTrue(result, "createNewTask returned false with correct note id")

        val newTaskId = 1L
        val expectedTaskEntity = task.copy(id = newTaskId).toNoteTaskEntity(note.id)
        val taskFromDao = database.noteTaskDao().getTaskById(newTaskId)[0]
        assertEquals(expectedTaskEntity, taskFromDao, "Task from database does not match expected task")
    }

    @Test
    fun createNewTask_fails_when_note_id_is_incorrect() = runTest {
        val note = noteWithNoTasks.copy()
        val task = noteTask.copy()
        database.noteDao().insertNote(note.toNoteEntity())
        val incorrectNoteId = 99999L

        val result = repository.createNewTask(incorrectNoteId, task.title)
        assertFalse(result, "createNewTask returned true when noteId is incorrect")
    }

    @Test
    fun updateTask_successfully_updates_existing_task() = runTest {
        val note = noteWithTask.copy()
        val task = noteTask.copy()
        database.noteDao().insertNote(note.toNoteEntity())
        database.noteTaskDao().insertTask(task.toNoteTaskEntity(note.id))
        val updatedTask = task.copy(title = "updatedTitle")

        val result = repository.updateTask(note.id, updatedTask)

        assertTrue(result, "updateTask returned false with correct note id")
    }

    @Test
    fun updateTask_fails_with_incorrect_note_id() = runTest {
        val note = noteWithTask.copy()
        val task = noteTask.copy()
        database.noteDao().insertNote(note.toNoteEntity())
        database.noteTaskDao().insertTask(task.toNoteTaskEntity(note.id))

        val incorrectNoteId = 99999L
        val result = repository.updateTask(incorrectNoteId, task)

        assertFalse(result, "updateTask returned true when noteId is incorrect")
    }

    @Test
    fun deleteTask_successfully_deletes_existing_task() = runTest {
        val note = noteWithTask.copy()
        val task = noteTask.copy()
        database.noteDao().insertNote(note.toNoteEntity())
        database.noteTaskDao().insertTask(task.toNoteTaskEntity(note.id))

        val result = repository.deleteTask(task)

        assertTrue(result, "deleteTask returned false with correct note id")
    }

    @Test
    fun deleteTask_with_non_existing_task_should_return_false() = runTest {
        val note = noteWithTask.copy()
        val task = noteTask.copy()
        database.noteDao().insertNote(note.toNoteEntity())
        database.noteTaskDao().insertTask(task.toNoteTaskEntity(note.id))
        val nonExistingTask = task.copy(id = 99999L)

        val result = repository.deleteTask(nonExistingTask)

        assertFalse(result, "deleteTask returned true with non-existing task")
    }
    @After
    override fun teardown() {
        super.teardown()
    }
}

private val noteTask = NoteTask(
    id = 3L,
    title = "task1",
    status = NoteTaskStatus.IN_PROGRESS
)

private val noteWithNoTasks = Note(
    id = 1L,
    title = "testNote1",
    status = NoteStatus.COMPLETE,
    tasksList = listOf(),
    lastUpdatedTimestamp = "2022-12-13T14:15:16Z"
)

private val noteWithTask = Note(
    id = 1L,
    title = "testNote2",
    status = NoteStatus.COMPLETE,
    tasksList = listOf(
        NoteTask(
            id = 5L,
            title = "task2",
            status = NoteTaskStatus.COMPLETE
        )
    ),
    lastUpdatedTimestamp = "2022-12-13T14:15:16Z"
)