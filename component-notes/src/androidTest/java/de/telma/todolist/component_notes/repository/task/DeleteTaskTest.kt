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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DeleteTaskTest: BaseRepositoryTest()  {
    lateinit var repository: TaskRepository

    private val noteTask = NoteTask(
        id = 3L,
        title = "task1",
        status = NoteTaskStatus.IN_PROGRESS
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

    @Before
    override fun setUp() {
        super.setUp()
        repository = TaskRepositoryImpl(database)
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
