package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.repository.FolderRepository
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.useCase.BaseNoteComponentUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class SetNoteFolderUseCaseTest : BaseNoteComponentUnitTest() {

    private lateinit var noteRepository: NoteRepository
    private lateinit var folderRepository: FolderRepository
    private lateinit var useCase: SetNoteFolderUseCase

    @Before
    fun setUp() {
        noteRepository = mockk()
        folderRepository = mockk()
        useCase = SetNoteFolderUseCase(
            noteRepository = noteRepository,
            folderRepository = folderRepository,
            clock = getClockForTest(getUpdatedTimestamp())
        )
    }

    @Test
    fun `should assign note from No folder to folder and update destination timestamp`() = runTest {
        val noteId = 10L
        val targetFolderId = 100L
        val note = getNote(id = noteId).copy(folderId = null)

        coEvery { noteRepository.getNoteById(noteId) } returns flowOf(note)
        coEvery { noteRepository.updateNotesFolder(listOf(noteId), targetFolderId) } returns true
        coEvery { folderRepository.updateFolderTimestamp(targetFolderId, getUpdatedTimestamp()) } returns true

        val result = useCase(noteId, targetFolderId)

        assertEquals(SetNoteFolderUseCase.Result.SUCCESS, result)
        coVerifyOrder {
            noteRepository.getNoteById(noteId)
            noteRepository.updateNotesFolder(listOf(noteId), targetFolderId)
            folderRepository.updateFolderTimestamp(targetFolderId, getUpdatedTimestamp())
        }
    }

    @Test
    fun `should unassign note to No folder and update source folder timestamp`() = runTest {
        val noteId = 11L
        val sourceFolderId = 200L
        val note = getNote(id = noteId).copy(folderId = sourceFolderId)

        coEvery { noteRepository.getNoteById(noteId) } returns flowOf(note)
        coEvery { noteRepository.updateNotesFolder(listOf(noteId), null) } returns true
        coEvery { folderRepository.updateFolderTimestamp(sourceFolderId, getUpdatedTimestamp()) } returns true

        val result = useCase(noteId, null)

        assertEquals(SetNoteFolderUseCase.Result.SUCCESS, result)
        coVerifyOrder {
            noteRepository.getNoteById(noteId)
            noteRepository.updateNotesFolder(listOf(noteId), null)
            folderRepository.updateFolderTimestamp(sourceFolderId, getUpdatedTimestamp())
        }
    }

    @Test
    fun `should move note between folders and update source and destination timestamps`() = runTest {
        val noteId = 12L
        val sourceFolderId = 200L
        val targetFolderId = 300L
        val note = getNote(id = noteId).copy(folderId = sourceFolderId)

        coEvery { noteRepository.getNoteById(noteId) } returns flowOf(note)
        coEvery { noteRepository.updateNotesFolder(listOf(noteId), targetFolderId) } returns true
        coEvery { folderRepository.updateFolderTimestamp(sourceFolderId, getUpdatedTimestamp()) } returns true
        coEvery { folderRepository.updateFolderTimestamp(targetFolderId, getUpdatedTimestamp()) } returns true

        val result = useCase(noteId, targetFolderId)

        assertEquals(SetNoteFolderUseCase.Result.SUCCESS, result)
        coVerifyOrder {
            noteRepository.getNoteById(noteId)
            noteRepository.updateNotesFolder(listOf(noteId), targetFolderId)
            folderRepository.updateFolderTimestamp(sourceFolderId, getUpdatedTimestamp())
            folderRepository.updateFolderTimestamp(targetFolderId, getUpdatedTimestamp())
        }
    }

    @Test
    fun `should return FAILURE when note folder update fails`() = runTest {
        val noteId = 13L
        val targetFolderId = 301L
        val note = getNote(id = noteId).copy(folderId = null)

        coEvery { noteRepository.getNoteById(noteId) } returns flowOf(note)
        coEvery { noteRepository.updateNotesFolder(listOf(noteId), targetFolderId) } returns false

        val result = useCase(noteId, targetFolderId)

        assertEquals(SetNoteFolderUseCase.Result.FAILURE, result)
        coVerify(exactly = 1) { noteRepository.updateNotesFolder(listOf(noteId), targetFolderId) }
        coVerify(exactly = 0) { folderRepository.updateFolderTimestamp(any(), any()) }
    }

    @Test
    fun `should return FAILURE when source folder timestamp update fails on unassign`() = runTest {
        val noteId = 14L
        val sourceFolderId = 401L
        val note = getNote(id = noteId).copy(folderId = sourceFolderId)

        coEvery { noteRepository.getNoteById(noteId) } returns flowOf(note)
        coEvery { noteRepository.updateNotesFolder(listOf(noteId), null) } returns true
        coEvery { folderRepository.updateFolderTimestamp(sourceFolderId, getUpdatedTimestamp()) } returns false

        val result = useCase(noteId, null)

        assertEquals(SetNoteFolderUseCase.Result.FAILURE, result)
        coVerifyOrder {
            noteRepository.getNoteById(noteId)
            noteRepository.updateNotesFolder(listOf(noteId), null)
            folderRepository.updateFolderTimestamp(sourceFolderId, getUpdatedTimestamp())
        }
    }
}
