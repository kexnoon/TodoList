package de.telma.todolist.component_notes.model

data class Filters(
    val timestampFrom: String? = null,
    val timestampTo: String? = null,
    val status: NoteStatus? = null
)

data class SearchModel(
    val query: String = "",
    val filters: Filters = Filters()
) {
    fun normalized() = copy(
        query = query.trim().takeIf { it.isNotEmpty() } ?: "",
        filters = filters.copy(
            timestampFrom = filters.timestampFrom?.trim()?.takeIf { it.isNotEmpty() },
            timestampTo = filters.timestampTo?.trim()?.takeIf { it.isNotEmpty() }
        )
    )
}
