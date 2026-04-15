package de.telma.todolist.feature_main.utils

import de.telma.todolist.component_notes.model.Folder
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FetureMainUiMappersTest {

    @Test
    fun `buildFolderChips should place All first and New Folder last`() {
        val folders = listOf(
            Folder(id = 1L, name = "Work", lastUpdatedTimestamp = "2023-01-03T00:00:00Z"),
            Folder(id = 2L, name = "Personal", lastUpdatedTimestamp = "2023-01-02T00:00:00Z")
        )

        val chips = buildFolderChips(folders, selectedFolderId = null)

        assertEquals("All Notes", chips.first().title)
        assertEquals(null, chips.first().folderId)
        assertTrue(chips.first().isSelected)

        assertEquals("New Folder", chips.last().title)
        assertTrue(chips.last().isNewFolderChip)
        assertFalse(chips.last().isSelected)
    }

    @Test
    fun `buildFolderChips should mark selected folder chip`() {
        val folders = listOf(
            Folder(id = 10L, name = "One", lastUpdatedTimestamp = "2023-01-03T00:00:00Z"),
            Folder(id = 20L, name = "Two", lastUpdatedTimestamp = "2023-01-02T00:00:00Z")
        )

        val chips = buildFolderChips(folders, selectedFolderId = 20L)

        assertFalse(chips.first().isSelected)
        assertTrue(chips.any { it.folderId == 20L && it.isSelected })
        assertFalse(chips.any { it.folderId == 10L && it.isSelected })
    }

    @Test
    fun `buildFolderChips should preserve folders order between special chips`() {
        val folders = listOf(
            Folder(id = 2L, name = "B", lastUpdatedTimestamp = "2023-01-03T00:00:00Z"),
            Folder(id = 1L, name = "A", lastUpdatedTimestamp = "2023-01-03T00:00:00Z"),
            Folder(id = 3L, name = "C", lastUpdatedTimestamp = "2023-01-03T00:00:00Z")
        )

        val chips = buildFolderChips(folders, selectedFolderId = null)

        assertEquals(listOf(2L, 1L, 3L), chips.drop(1).dropLast(1).mapNotNull { it.folderId })
    }
}
