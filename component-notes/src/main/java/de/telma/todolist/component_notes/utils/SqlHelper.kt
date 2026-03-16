package de.telma.todolist.component_notes.utils

import de.telma.todolist.component_notes.model.SearchModel
import de.telma.todolist.component_notes.model.SortBy
import de.telma.todolist.component_notes.model.SortOrder

internal class SqlHelper() {
    private val whereParts = mutableListOf<String>()
    private val args = mutableListOf<Any?>()

    fun getNotesQueryModel(search: SearchModel): SqlQueryModel {
        val normalized = search.normalized()
        val filters = normalized.filters

        if (normalized.query != null) {
            addClause("title LIKE '%' || ? || '%' COLLATE NOCASE", normalized.query)
        }
        if (filters.status != null) {
            addClause("status = ?", filters.status.statusValue)
        }
        if (filters.timestampFrom != null) {
            addClause("lastUpdatedTimestamp >= ?", filters.timestampFrom)
        }
        if (filters.timestampTo != null) {
            addClause("lastUpdatedTimestamp <= ?", filters.timestampTo)
        }

        val whereSql = if (whereParts.isEmpty()) "" else "WHERE " + whereParts.joinToString(" AND ")

        val sortColumn = when (normalized.sortBy) {
            SortBy.TITLE -> "title"
            SortBy.STATUS -> "status"
            SortBy.CREATED_AT -> "createdTimestamp"
            else -> "lastUpdatedTimestamp"
        }
        val sortOrder = if (normalized.sortOrder == SortOrder.ASC) "ASC" else "DESC"

        val sql = """
            SELECT * FROM notes
            $whereSql
            ORDER BY $sortColumn $sortOrder
        """.trimIndent()

        return SqlQueryModel(sql, args)
    }

    private fun addClause(clause: String, value: Any?) {
        whereParts.add(clause)
        args.add(value)
    }

}

data class SqlQueryModel(val query: String, val args: List<Any?>)