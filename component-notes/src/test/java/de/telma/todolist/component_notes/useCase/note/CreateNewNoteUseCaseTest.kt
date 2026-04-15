package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.repository.FolderRepository
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.useCase.BaseNoteComponentUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Clock
import kotlin.test.assertEquals

class CreateNewNoteUseCaseTest : BaseNoteComponentUnitTest() {

    private lateinit var noteRepository: NoteRepository
    private lateinit var folderRepository: FolderRepository
    private lateinit var clock: Clock
    private lateinit var useCase: CreateNewNoteUseCase

    @Before
    fun setUp() {
        noteRepository = mockk<NoteRepository>()
        folderRepository = mockk<FolderRepository>()
        clock = getClockForTest(getUpdatedTimestamp())
        useCase = CreateNewNoteUseCase(noteRepository, folderRepository, clock)
    }

    @Test
    fun `should return SUCCESS when note creation is successful`() = runTest {
        // SETUP
        val title = "New Note Title"
        val expectedNoteId = 1L
        coEvery { noteRepository.createNewNote(any(), any(), any()) } returns expectedNoteId

        // ACT
        val result = useCase(title)

        // ASSERT
        assertEquals(
            expected = CreateNewNoteUseCase.Result.SUCCESS(expectedNoteId),
            actual = result
        )
        coVerify { noteRepository.createNewNote(title, getUpdatedTimestamp(), null) }
        coVerify(exactly = 0) { folderRepository.updateFolderTimestamp(any(), any()) }
    }

    @Test
    fun `should return FAILURE when note creation throws an exception`() = runTest {
        // SETUP
        val title = "New Note Title"
        val exception = RuntimeException("Database error")
        coEvery { noteRepository.createNewNote(any(), any(), any()) } throws exception

        // ACT
        val result = useCase(title)

        // ASSERT
        assertEquals(
            expected = CreateNewNoteUseCase.Result.FAILURE,
            actual = result
        )
        coVerify { noteRepository.createNewNote(title, getUpdatedTimestamp(), null) }
        coVerify(exactly = 0) { folderRepository.updateFolderTimestamp(any(), any()) }
    }

    @Test
    fun `should update folder timestamp when folderId is provided`() = runTest {
        val title = "New Note Title"
        val folderId = 7L
        val expectedNoteId = 1L
        coEvery { noteRepository.createNewNote(any(), any(), any()) } returns expectedNoteId
        coEvery { folderRepository.updateFolderTimestamp(any(), any()) } returns true

        val result = useCase(title, folderId)

        assertEquals(CreateNewNoteUseCase.Result.SUCCESS(expectedNoteId), result)
        coVerify { noteRepository.createNewNote(title, getUpdatedTimestamp(), folderId) }
        coVerify { folderRepository.updateFolderTimestamp(folderId, getUpdatedTimestamp()) }
    }

    @Test
    fun `should return FAILURE when folder timestamp update returns false`() = runTest {
        val title = "New Note Title"
        val folderId = 7L
        val expectedNoteId = 1L
        coEvery { noteRepository.createNewNote(any(), any(), any()) } returns expectedNoteId
        coEvery { folderRepository.updateFolderTimestamp(any(), any()) } returns false

        val result = useCase(title, folderId)

        assertEquals(CreateNewNoteUseCase.Result.FAILURE, result)
        coVerify { noteRepository.createNewNote(title, getUpdatedTimestamp(), folderId) }
        coVerify { folderRepository.updateFolderTimestamp(folderId, getUpdatedTimestamp()) }
    }

    @Test
    fun `should return FAILURE when folder timestamp update throws`() = runTest {
        val title = "New Note Title"
        val folderId = 7L
        val expectedNoteId = 1L
        coEvery { noteRepository.createNewNote(any(), any(), any()) } returns expectedNoteId
        coEvery { folderRepository.updateFolderTimestamp(any(), any()) } throws RuntimeException("db error")

        val result = useCase(title, folderId)

        assertEquals(CreateNewNoteUseCase.Result.FAILURE, result)
        coVerify { noteRepository.createNewNote(title, getUpdatedTimestamp(), folderId) }
        coVerify { folderRepository.updateFolderTimestamp(folderId, getUpdatedTimestamp()) }
    }
}
