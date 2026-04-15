package de.telma.todolist.feature_main.utils

import de.telma.todolist.component_notes.model.Folder
import de.telma.todolist.feature_main.main_screen.FolderChipModel

private const val ALL_NOTES_CHIP_TITLE = "All Notes"
private const val NEW_FOLDER_CHIP_TITLE = "New Folder"

fun buildFolderChips(
    folders: List<Folder>,
    selectedFolderId: Long?
): List<FolderChipModel> {
    val chips = mutableListOf<FolderChipModel>()
    chips.add(
        FolderChipModel(
            folderId = null,
            title = ALL_NOTES_CHIP_TITLE,
            isSelected = selectedFolderId == null
        )
    )
    folders.forEach { folder ->
        chips.add(
            FolderChipModel(
                folderId = folder.id,
                title = folder.name,
                isSelected = selectedFolderId == folder.id
            )
        )
    }
    chips.add(
        FolderChipModel(
            folderId = null,
            title = NEW_FOLDER_CHIP_TITLE,
            isNewFolderChip = true
        )
    )
    return chips
}
