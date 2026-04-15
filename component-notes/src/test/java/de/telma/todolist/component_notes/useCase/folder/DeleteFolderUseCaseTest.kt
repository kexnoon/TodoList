package de.telma.todolist.component_notes.useCase.folder

import de.telma.todolist.component_notes.repository.FolderRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class DeleteFolderUseCaseTest {

    private lateinit var folderRepository: FolderRepository
    private lateinit var useCase: DeleteFolderUseCase

    @Before
    fun setUp() {
        folderRepository = mockk()
        useCase = DeleteFolderUseCase(folderRepository)
    }

    @Test
    fun `should return SUCCESS when repository delete succeeds`() = runTest {
        coEvery { folderRepository.deleteFolder(any()) } returns true

        val result = useCase(8L)

        assertEquals(DeleteFolderUseCase.Result.SUCCESS, result)
        coVerify(exactly = 1) { folderRepository.deleteFolder(8L) }
    }

    @Test
    fun `should return FAILURE when repository delete fails`() = runTest {
        coEvery { folderRepository.deleteFolder(any()) } returns false

        val result = useCase(8L)

        assertEquals(DeleteFolderUseCase.Result.FAILURE, result)
        coVerify(exactly = 1) { folderRepository.deleteFolder(8L) }
    }
}
