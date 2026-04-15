package de.telma.todolist.component_notes.useCase.folder

import de.telma.todolist.component_notes.repository.FolderRepository
import de.telma.todolist.component_notes.useCase.BaseNoteComponentUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Clock
import kotlin.test.assertEquals

class RenameFolderUseCaseTest : BaseNoteComponentUnitTest() {

    private lateinit var folderRepository: FolderRepository
    private lateinit var clock: Clock
    private lateinit var useCase: RenameFolderUseCase

    @Before
    fun setUp() {
        folderRepository = mockk()
        clock = getClockForTest(getUpdatedTimestamp())
        useCase = RenameFolderUseCase(folderRepository, clock)
    }

    @Test
    fun `should return INVALID_NAME for blank folder name`() = runTest {
        val result = useCase(folderId = 5L, name = "  ")

        assertEquals(RenameFolderUseCase.Result.INVALID_NAME, result)
        coVerify(exactly = 0) { folderRepository.renameFolder(any(), any(), any()) }
    }

    @Test
    fun `should trim name call repository and return SUCCESS`() = runTest {
        coEvery { folderRepository.renameFolder(any(), any(), any()) } returns true

        val result = useCase(folderId = 5L, name = "  Personal  ")

        assertEquals(RenameFolderUseCase.Result.SUCCESS, result)
        coVerify(exactly = 1) { folderRepository.renameFolder(5L, "Personal", getUpdatedTimestamp()) }
    }

    @Test
    fun `should return FAILURE when repository returns false`() = runTest {
        coEvery { folderRepository.renameFolder(any(), any(), any()) } returns false

        val result = useCase(folderId = 5L, name = "Personal")

        assertEquals(RenameFolderUseCase.Result.FAILURE, result)
        coVerify(exactly = 1) { folderRepository.renameFolder(5L, "Personal", getUpdatedTimestamp()) }
    }
}
