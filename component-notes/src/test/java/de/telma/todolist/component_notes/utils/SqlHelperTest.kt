package de.telma.todolist.component_notes.utils

import de.telma.todolist.component_notes.model.Filters
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.SearchModel
import de.telma.todolist.component_notes.model.SortBy
import de.telma.todolist.component_notes.model.SortOrder
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SqlHelperTest {

    @Test
    fun `query with only default search has no where and sorts by updated desc`() {
        val helper = SqlHelper()
        val model = helper.getNotesQueryModel(SearchModel())

        assertTrue(model.args.isEmpty(), "Default query should have no args")
        assertTrue(!model.query.contains("WHERE"), "Default query should not contain WHERE")
        assertTrue(model.query.contains("ORDER BY lastUpdatedTimestamp DESC"), "Default sort should be UPDATED_AT DESC")
    }

    @Test
    fun `query with title and status builds where clauses in order`() {
        val helper = SqlHelper()
        val model = helper.getNotesQueryModel(
            SearchModel(
                query = "foo",
                filters = Filters(status = NoteStatus.COMPLETE)
            )
        )

        assertTrue(model.query.contains("title LIKE '%' || ? || '%' COLLATE NOCASE"), "Query must filter by title")
        assertTrue(model.query.contains("status = ?"), "Query must filter by status")
        assertEquals(listOf("foo", NoteStatus.COMPLETE.statusValue), model.args, "Args should follow clause order")
    }

    @Test
    fun `created range builds where and sorts by created asc`() {
        val helper = SqlHelper()
        val model = helper.getNotesQueryModel(
            SearchModel(
                filters = Filters(
                    createdFrom = "cFrom",
                    createdTo = "cTo",
                ),
                sortBy = SortBy.CREATED_AT,
                sortOrder = SortOrder.ASC
            )
        )

        assertTrue(model.query.contains("createdTimestamp >= ?"))
        assertTrue(model.query.contains("createdTimestamp <= ?"))
        assertTrue(model.query.contains("ORDER BY createdTimestamp ASC"))
        assertEquals(listOf("cFrom", "cTo"), model.args, "Created range should keep arg order")
    }

    @Test
    fun `throws when created and updated ranges set together`() {
        val helper = SqlHelper()
        assertFailsWith<IllegalArgumentException> {
            helper.getNotesQueryModel(
                SearchModel(
                    filters = Filters(
                        createdFrom = "cFrom",
                        updatedFrom = "uFrom"
                    )
                )
            )
        }
    }
}
