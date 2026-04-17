package de.telma.todolist.feature_main.utils

import de.telma.todolist.component_notes.model.Folder
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FolderMapperTest {

    @Test
    fun folder_to_filter_chip_model_should_map_name_selected_and_callbacks() {
        val folder = Folder(
            id = 5L,
            name = "Work",
            lastUpdatedTimestamp = "2024-01-01T10:00:00Z"
        )
        var isClicked = false
        var isLongClicked = false

        val model = folder.toFilterChipModel(
            selected = true,
            onClick = { isClicked = true },
            onLongClick = { isLongClicked = true }
        )

        assertEquals("Work", model.text)
        assertTrue(model.selected)
        assertNull(model.icon)
        assertNull(model.iconContentDescription)

        model.onClick()
        model.onLongClick?.invoke()

        assertTrue(isClicked)
        assertTrue(isLongClicked)
    }

    @Test
    fun folder_to_filter_chip_model_should_use_default_callbacks_and_null_long_click() {
        val folder = Folder(
            id = 7L,
            name = "Home",
            lastUpdatedTimestamp = "2024-01-02T10:00:00Z"
        )

        val model = folder.toFilterChipModel(selected = false)

        assertEquals("Home", model.text)
        assertFalse(model.selected)
        assertNull(model.onLongClick)

        model.onClick()
    }

    @Test
    fun folder_to_current_folder_model_should_map_id_and_name() {
        val folder = Folder(
            id = 11L,
            name = "Inbox",
            lastUpdatedTimestamp = "2024-03-12T10:00:00Z"
        )

        val model = folder.toCurrentFolderModel()

        assertEquals(11L, model.folderId)
        assertEquals("Inbox", model.name)
    }
}
