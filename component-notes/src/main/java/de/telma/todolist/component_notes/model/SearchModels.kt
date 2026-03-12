package de.telma.todolist.component_notes.model

data class Filters(
    val timestampFrom: String? = null,
    val timestampTo: String? = null,
    val status: NoteStatus? = null
)

data class SearchModel(
    val query: String = "",
    val filters: Filters = Filters()
)
