package de.telma.todolist.feature_main.utils

import de.telma.todolist.component_notes.model.Folder
import de.telma.todolist.core_ui.composables.FilterChipModel
import de.telma.todolist.feature_main.note_screen.models.CurrentFolderModel

fun Folder.toFilterChipModel(
    selected: Boolean,
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null
): FilterChipModel {
    return FilterChipModel(
        text = name,
        selected = selected,
        onClick = onClick,
        onLongClick = onLongClick
    )
}

fun Folder.toCurrentFolderModel(): CurrentFolderModel {
    return CurrentFolderModel(
        name = name,
        folderId = id
    )
}
