package de.telma.todolist.component_notes.utils

import de.telma.todolist.component_notes.model.SearchModel
import de.telma.todolist.component_notes.model.SortBy
import de.telma.todolist.component_notes.model.SortOrder

internal class SqlHelper {

    fun getNotesQueryModel(search: SearchModel, folderId: Long? = null): SqlQueryModel {
        val whereParts = mutableListOf<String>()
        val args = mutableListOf<Any?>()

        fun addClause(clause: String, value: Any?) {
            whereParts.add(clause)
            args.add(value)
        }

        val normalized = search.normalized()
        val filters = normalized.filters
        val hasCreatedRange = filters.createdFrom != null || filters.createdTo != null
        val hasUpdatedRange = filters.updatedFrom != null || filters.updatedTo != null

        require(!(hasCreatedRange && hasUpdatedRange)) {
            "Created and updated ranges cannot be set together"
        }

        if (normalized.query != null) addClause("title LIKE '%' || ? || '%' COLLATE NOCASE", normalized.query)
        if (filters.status != null) addClause("status = ?", filters.status.statusValue)
        if (folderId != null) addClause("folderId = ?", folderId)

        filters.createdFrom?.let { addClause("createdTimestamp >= ?", it) }
        filters.createdTo?.let { addClause("createdTimestamp <= ?", it) }
        filters.updatedFrom?.let { addClause("lastUpdatedTimestamp >= ?", it) }
        filters.updatedTo?.let { addClause("lastUpdatedTimestamp <= ?", it) }

        val whereSql = if (whereParts.isEmpty()) "" else "WHERE " + whereParts.joinToString(" AND ")

        val sortColumn = when (normalized.sortBy) {
            SortBy.TITLE -> "title"
            SortBy.STATUS -> "status"
            SortBy.CREATED_AT -> "createdTimestamp"
            else -> "lastUpdatedTimestamp"
        }
        val sortCollate = if (normalized.sortBy == SortBy.TITLE) " COLLATE NOCASE" else ""
        val sortOrder = if (normalized.sortOrder == SortOrder.ASC) "ASC" else "DESC"

        val sql = """
            SELECT * FROM notes
            $whereSql
            ORDER BY $sortColumn$sortCollate $sortOrder
        """.trimIndent()

        return SqlQueryModel(sql, args)
    }
}

data class SqlQueryModel(val query: String, val args: List<Any?>)
