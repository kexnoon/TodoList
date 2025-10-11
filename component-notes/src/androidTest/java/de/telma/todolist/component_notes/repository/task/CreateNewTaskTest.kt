package de.telma.todolist.component_notes.repository.task

import androidx.test.ext.junit.runners.AndroidJUnit4
import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.repository.BaseRepositoryTest
import de.telma.todolist.component_notes.repository.TaskRepository
import de.telma.todolist.component_notes.repository.TaskRepositoryImpl
import de.telma.todolist.component_notes.utils.toNoteEntity
import de.telma.todolist.component_notes.utils.toNoteTaskEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class CreateNewTaskTest: BaseRepositoryTest() {
    lateinit var repository: TaskRepository

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

    @Before
    override fun setUp() {
        super.setUp()
        repository = TaskRepositoryImpl(database)
    }

    @After
    override fun teardown() {
        super.teardown()
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
}