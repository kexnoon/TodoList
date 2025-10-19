package de.telma.todolist.component_notes.repository.task

import androidx.test.ext.junit.runners.AndroidJUnit4
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
class CreateNewTaskTest : BaseRepositoryTest() {
    private lateinit var repository: TaskRepository

    @Before
    override fun setUp() {
        super.setUp()
        repository = TaskRepositoryImpl(database)
    }

    @Test
    fun should_return_true_when_task_is_created_successfully() = runTest {
        val note = getNote()
        val task = getTask().copy()
        database.noteDao().insertNote(note.toNoteEntity())

        val result = repository.createNewTask(note.id, task.title)
        assertTrue(result, "createNewTask returned false with correct note id")

        val newTaskId = 1L
        val expectedTaskEntity = task.copy(id = newTaskId).toNoteTaskEntity(note.id)
        val taskFromDao = database.noteTaskDao().getTaskById(newTaskId)[0]
        assertEquals(expectedTaskEntity, taskFromDao, "Task from database does not match expected task")
    }

    @Test
    fun should_return_false_when_note_id_is_incorrect() = runTest {
        val note = getNote()
        val task = getTask()
        database.noteDao().insertNote(note.toNoteEntity())
        val incorrectNoteId = 99999L

        val result = repository.createNewTask(incorrectNoteId, task.title)

        assertFalse(result, "createNewTask returned true when noteId is incorrect")
    }

    @After
    override fun teardown() {
        super.teardown()
    }
}