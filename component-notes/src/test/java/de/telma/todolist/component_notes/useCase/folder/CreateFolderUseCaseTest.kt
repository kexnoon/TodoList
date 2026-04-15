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

class CreateFolderUseCaseTest : BaseNoteComponentUnitTest() {

    private lateinit var folderRepository: FolderRepository
    private lateinit var clock: Clock
    private lateinit var useCase: CreateFolderUseCase

    @Before
    fun setUp() {
        folderRepository = mockk()
        clock = getClockForTest(getUpdatedTimestamp())
        useCase = CreateFolderUseCase(folderRepository, clock)
    }

    @Test
    fun `should return INVALID_NAME for blank folder name`() = runTest {
        val result = useCase("   ")

        assertEquals(CreateFolderUseCase.Result.INVALID_NAME, result)
        coVerify(exactly = 0) { folderRepository.createFolder(any(), any()) }
    }

    @Test
    fun `should trim name call repository and return SUCCESS`() = runTest {
        val folderId = 11L
        coEvery { folderRepository.createFolder(any(), any()) } returns folderId

        val result = useCase("  Work  ")

        assertEquals(CreateFolderUseCase.Result.SUCCESS(folderId), result)
        coVerify(exactly = 1) { folderRepository.createFolder("Work", getUpdatedTimestamp()) }
    }

    @Test
    fun `should return FAILURE when repository throws`() = runTest {
        coEvery { folderRepository.createFolder(any(), any()) } throws RuntimeException("db error")

        val result = useCase("Work")

        assertEquals(CreateFolderUseCase.Result.FAILURE, result)
        coVerify(exactly = 1) { folderRepository.createFolder("Work", getUpdatedTimestamp()) }
    }
}
