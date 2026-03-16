package de.telma.todolist.component_notes.model

enum class SortBy { TITLE, STATUS, CREATED_AT, UPDATED_AT }
enum class SortOrder { ASC, DESC }

data class Filters(
    val createdFrom: String? = null,
    val createdTo: String? = null,
    val updatedFrom: String? = null,
    val updatedTo: String? = null,
    val status: NoteStatus? = null
)

data class SearchModel(
    val query: String? = null,
    val sortBy: SortBy = SortBy.UPDATED_AT,
    val sortOrder: SortOrder = SortOrder.DESC,
    val filters: Filters = Filters()
) {
    fun normalized(): SearchModel {
        val trimmedFilters = filters.copy(
            createdFrom = filters.createdFrom?.trim()?.takeIf { it.isNotEmpty() },
            createdTo = filters.createdTo?.trim()?.takeIf { it.isNotEmpty() },
            updatedFrom = filters.updatedFrom?.trim()?.takeIf { it.isNotEmpty() },
            updatedTo = filters.updatedTo?.trim()?.takeIf { it.isNotEmpty() }
        )
        val hasCreated = trimmedFilters.createdFrom != null || trimmedFilters.createdTo != null
        val hasUpdated = trimmedFilters.updatedFrom != null || trimmedFilters.updatedTo != null
        if (hasCreated && hasUpdated) {
            throw IllegalArgumentException("Use either created* or updated* filters, not both in one request")
        }
        return copy(
            query = query?.trim()?.takeIf { it.isNotEmpty() },
            filters = trimmedFilters
        )
    }
}
