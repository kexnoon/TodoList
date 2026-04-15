package de.telma.todolist.component_notes.useCase.folder

import de.telma.todolist.component_notes.model.Folder
import de.telma.todolist.component_notes.repository.FolderRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class GetFoldersUseCaseTest {

    private lateinit var folderRepository: FolderRepository
    private lateinit var useCase: GetFoldersUseCase

    @Before
    fun setUp() {
        folderRepository = mockk()
        useCase = GetFoldersUseCase(folderRepository)
    }

    @Test
    fun `should request folders from repository and return them sorted by lastUpdatedTimestamp`() = runTest {
        val unsorted = listOf(
            Folder(id = 1L, name = "Old", lastUpdatedTimestamp = "2023-01-01T00:00:00Z"),
            Folder(id = 2L, name = "Newest", lastUpdatedTimestamp = "2023-01-03T00:00:00Z"),
            Folder(id = 3L, name = "Middle", lastUpdatedTimestamp = "2023-01-02T00:00:00Z")
        )
        every { folderRepository.getAll() } returns flowOf(unsorted)

        val result = useCase().first()

        assertEquals(listOf(2L, 3L, 1L), result.map { it.id })
        verify(exactly = 1) { folderRepository.getAll() }
    }

    @Test
    fun `should sort folders by name when lastUpdatedTimestamp is the same`() = runTest {
        val sameTimestamp = "2023-01-03T00:00:00Z"
        val unsorted = listOf(
            Folder(id = 1L, name = "Alpha", lastUpdatedTimestamp = sameTimestamp),
            Folder(id = 2L, name = "Charlie", lastUpdatedTimestamp = sameTimestamp),
            Folder(id = 3L, name = "Bravo", lastUpdatedTimestamp = sameTimestamp)
        )
        every { folderRepository.getAll() } returns flowOf(unsorted)

        val result = useCase().first()

        assertEquals(listOf(2L, 3L, 1L), result.map { it.id })
        verify(exactly = 1) { folderRepository.getAll() }
    }

    @Test
    fun `should return empty list when repository has no folders`() = runTest {
        every { folderRepository.getAll() } returns flowOf(emptyList())

        val result = useCase().first()

        assertEquals(emptyList(), result)
        verify(exactly = 1) { folderRepository.getAll() }
    }
}
