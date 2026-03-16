package de.telma.todolist.component_notes.repository.note

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.sqlite.db.SimpleSQLiteQuery
import de.telma.todolist.component_notes.model.Filters
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.SearchModel
import de.telma.todolist.component_notes.model.SortBy
import de.telma.todolist.component_notes.model.SortOrder
import de.telma.todolist.component_notes.repository.BaseRepositoryTest
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.repository.NoteRepositoryImpl
import de.telma.todolist.component_notes.utils.toNote
import de.telma.todolist.component_notes.utils.toNoteEntity
import de.telma.todolist.component_notes.utils.toNoteTaskEntity
import de.telma.todolist.storage.database.entity.NoteWithTasks
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class GetAllNotesTest : BaseRepositoryTest() {
    private lateinit var repository: NoteRepository

    @Before
    override fun setUp() {
        super.setUp()
        repository = NoteRepositoryImpl(database)
    }

    @Test
    fun should_return_all_notes_when_notes_exist_in_db() = runTest {
        val tasks = listOf(getTask(), getTask())
        val note1 = getNote(tasksList = tasks)
        val note2 = getNote(id = 2L)

        database.noteDao().insertNote(note1.toNoteEntity())
        database.noteDao().insertNote(note2.toNoteEntity())
        note1.tasksList.forEach {
            database.noteTaskDao().insertTask(it.toNoteTaskEntity(note1.id))
        }

        val expected = database.noteDao()
            .getNotesWithTasks(SimpleSQLiteQuery("SELECT * FROM notes"))
            .first()
            .map(NoteWithTasks::toNote)
        val actual = repository.getNotes().first()

        assertTrue(expected.isNotEmpty(), "Notes list from DAO is empty!")
        assertTrue(actual.isNotEmpty(), "Notes list from repository is empty!")
        assertEquals(expected.size, actual.size, "Notes lists should be the same size!")
        assertEquals(expected, actual, "Notes content should match! ")
    }

    @Test
    fun should_return_notes_with_no_tasks_when_such_notes_exist_in_db() = runTest {
        val note = getNote()

        database.noteDao().insertNote(note.toNoteEntity())

        val expected = listOf(note)
        val result = repository.getNotes().first()

        assertEquals(
            expected, result,
            "Repository doesn't return notes with no tasks. "
        )

    }

    @Test
    fun should_return_empty_list_when_no_notes_in_db() = runTest {
        val notesFromRepository = repository.getNotes().first()
        assertNotNull(
            notesFromRepository,
            "Repository doesn't return empty list when there are no notes in DB."
        )
        assertTrue(
            notesFromRepository.isEmpty(),
            "Repository doesn't return empty list when there are no notes in DB."
        )
    }

    @Test
    fun search_by_query_should_be_case_insensitive() = runTest {
        val noteFoo = getNote(id = 1L, title = "Foo Note")
        val noteBar = getNote(id = 2L, title = "Bar")

        database.noteDao().insertNote(noteFoo.toNoteEntity())
        database.noteDao().insertNote(noteBar.toNoteEntity())

        val result = repository.getNotes(SearchModel(query = "foo")).first()

        assertEquals(listOf(noteFoo), result, "Search by title should return only matching note (case-insensitive).")
    }

    @Test
    fun filter_by_status_returns_only_matching_status() = runTest {
        val inProgress = getNote(id = 1L, status = NoteStatus.IN_PROGRESS)
        val complete = getNote(id = 2L, status = NoteStatus.COMPLETE)

        database.noteDao().insertNote(inProgress.toNoteEntity())
        database.noteDao().insertNote(complete.toNoteEntity())

        val result = repository.getNotes(
            SearchModel(filters = Filters(status = NoteStatus.COMPLETE))
        ).first()

        assertEquals(listOf(complete), result, "Status filter should return only COMPLETE notes.")
    }

    @Test
    fun sort_by_title_asc_returns_ordered_list() = runTest {
        val noteC = getNote(id = 1L, title = "Charlie")
        val noteA = getNote(id = 2L, title = "Alpha")
        val noteB = getNote(id = 3L, title = "Bravo")

        database.noteDao().insertNote(noteC.toNoteEntity())
        database.noteDao().insertNote(noteA.toNoteEntity())
        database.noteDao().insertNote(noteB.toNoteEntity())

        val result = repository.getNotes(
            SearchModel(sortBy = SortBy.TITLE, sortOrder = SortOrder.ASC)
        ).first()

        val titles = result.map { it.title }
        assertEquals(listOf("Alpha", "Bravo", "Charlie"), titles, "Title ASC sort should order alphabetically.")
        assertTrue(result.size == 3, "All notes should be returned when sorting.")
    }

    @Test
    fun filter_by_created_range_returns_only_in_interval() = runTest {
        val early = getNote(id = 1L, title = "Early", createdTimestamp = "2022-01-01T00:00:00Z")
        val mid = getNote(id = 2L, title = "Mid", createdTimestamp = "2023-01-01T00:00:00Z")
        val late = getNote(id = 3L, title = "Late", createdTimestamp = "2024-01-01T00:00:00Z")

        listOf(early, mid, late).forEach { database.noteDao().insertNote(it.toNoteEntity()) }

        val result = repository.getNotes(
            SearchModel(filters = Filters(createdFrom = "2022-06-01T00:00:00Z", createdTo = "2023-06-01T00:00:00Z"))
        ).first()

        assertEquals(listOf(mid), result, "Created date range should include only notes within bounds.")
    }

    @Test
    fun filter_by_updated_range_returns_only_in_interval() = runTest {
        val early = getNote(id = 1L, title = "Early", lastUpdatedTimestamp = "2022-01-01T00:00:00Z")
        val mid = getNote(id = 2L, title = "Mid", lastUpdatedTimestamp = "2023-01-01T00:00:00Z")
        val late = getNote(id = 3L, title = "Late", lastUpdatedTimestamp = "2024-01-01T00:00:00Z")

        listOf(early, mid, late).forEach { database.noteDao().insertNote(it.toNoteEntity()) }

        val result = repository.getNotes(
            SearchModel(filters = Filters(updatedFrom = "2022-06-01T00:00:00Z", updatedTo = "2023-06-01T00:00:00Z"))
        ).first()

        assertEquals(listOf(mid), result, "Updated date range should include only notes within bounds.")
    }

    @Test
    fun sort_by_created_desc_returns_correct_order() = runTest {
        val early = getNote(id = 1L, title = "Early", createdTimestamp = "2022-01-01T00:00:00Z")
        val mid = getNote(id = 2L, title = "Mid", createdTimestamp = "2023-01-01T00:00:00Z")
        val late = getNote(id = 3L, title = "Late", createdTimestamp = "2024-01-01T00:00:00Z")

        listOf(early, mid, late).forEach { database.noteDao().insertNote(it.toNoteEntity()) }

        val result = repository.getNotes(
            SearchModel(sortBy = SortBy.CREATED_AT, sortOrder = SortOrder.DESC)
        ).first()

        val titles = result.map { it.title }
        assertEquals(listOf("Late", "Mid", "Early"), titles, "Created DESC sort should order newest first.")
    }

    @Test
    fun sort_by_updated_asc_returns_correct_order() = runTest {
        val early = getNote(id = 1L, title = "Early", lastUpdatedTimestamp = "2022-01-01T00:00:00Z")
        val mid = getNote(id = 2L, title = "Mid", lastUpdatedTimestamp = "2023-01-01T00:00:00Z")
        val late = getNote(id = 3L, title = "Late", lastUpdatedTimestamp = "2024-01-01T00:00:00Z")

        listOf(early, mid, late).forEach { database.noteDao().insertNote(it.toNoteEntity()) }

        val result = repository.getNotes(
            SearchModel(sortBy = SortBy.UPDATED_AT, sortOrder = SortOrder.ASC)
        ).first()

        val titles = result.map { it.title }
        assertEquals(listOf("Early", "Mid", "Late"), titles, "Updated ASC sort should order oldest first.")
    }


    @After
    override fun teardown() {
        super.teardown()
    }
}
