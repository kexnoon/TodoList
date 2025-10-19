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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DeleteTaskTest : BaseRepositoryTest() {
    private lateinit var repository: TaskRepository

    @Before
    override fun setUp() {
        super.setUp()
        repository = TaskRepositoryImpl(database)
    }

    @Test
    fun `should_return_true_when_task_is_deleted_successfully`() = runTest {
        val note = getNote(tasksList = listOf(getTask(), getTask()))
        val task = getTask()
        database.noteDao().insertNote(note.toNoteEntity())
        database.noteTaskDao().insertTask(task.toNoteTaskEntity(note.id))

        val result = repository.deleteTask(task)

        assertTrue(result, "deleteTask returned false with correct task")
    }

    @Test
    fun `should_return_false_when_task_to_be_deleted_does_not_exist`() = runTest {
        val note = getNote(tasksList = listOf(getTask(), getTask()))
        val task = getTask()
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