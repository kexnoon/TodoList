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
import kotlin.test.assertEquals

class RenameNoteUseCaseTest: BaseNoteComponentUnitTest() {
    private lateinit var notesRepository: NoteRepository
    private lateinit var folderRepository: FolderRepository
    private lateinit var useCase: RenameNoteUseCase

    @Before
    fun setUp() {
        notesRepository = mockk<NoteRepository>()
        folderRepository = mockk<FolderRepository>()
        val clock = getClockForTest(getUpdatedTimestamp())
        useCase = RenameNoteUseCase(notesRepository, folderRepository, clock)
    }

    @Test
    fun `should update Note and return SUCCESS when given new title`() = runTest {
        //SETUP
        val oldNote = getNote().copy(folderId = 7L)

        val newTitle = "Successfully Updated Title"
        val expectedNoteAfterUpdate = oldNote.copy(title = newTitle, lastUpdatedTimestamp = getUpdatedTimestamp())

        coEvery { notesRepository.updateNote(expectedNoteAfterUpdate) } returns true
        coEvery { folderRepository.updateFolderTimestamp(7L, getUpdatedTimestamp()) } returns true

        // ACT
        val result = useCase(oldNote, newTitle)

        // ASSERT
        assertEquals(
            expected = RenameNoteUseCase.Result.SUCCESS,
            actual = result
        )
        coVerify(exactly = 1) { notesRepository.updateNote(expectedNoteAfterUpdate) }
        coVerify(exactly = 1) { folderRepository.updateFolderTimestamp(7L, getUpdatedTimestamp()) }
    }

    @Test
    fun `should fail when repository update fails`() = runTest {
        // SETUP
        val newTitle = "Failed Update Title"
        val defaultNote = getNote().copy(folderId = null)
        val updatedNote = defaultNote.copy(title = newTitle, lastUpdatedTimestamp = getUpdatedTimestamp())

        coEvery { notesRepository.updateNote(updatedNote) } returns false

        // ACT
        val result = useCase(defaultNote, newTitle)

        // ASSERT
        assertEquals(
            expected = RenameNoteUseCase.Result.FAILURE,
            actual = result
        )
        coVerify(exactly = 1) { notesRepository.updateNote(updatedNote) }
        coVerify(exactly = 0) { folderRepository.updateFolderTimestamp(any<Long>(), any<String>()) }
    }
}
