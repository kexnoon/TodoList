package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.repository.FolderRepository
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.useCase.BaseNoteComponentUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class MoveNotesToFolderUseCaseTest : BaseNoteComponentUnitTest() {

    private lateinit var noteRepository: NoteRepository
    private lateinit var folderRepository: FolderRepository
    private lateinit var useCase: MoveNotesToFolderUseCase

    @Before
    fun setUp() {
        noteRepository = mockk()
        folderRepository = mockk()
        useCase = MoveNotesToFolderUseCase(
            noteRepository = noteRepository,
            folderRepository = folderRepository,
            clock = getClockForTest(getUpdatedTimestamp())
        )
    }

    @Test
    fun `should move notes to existing folder and update destination timestamp`() = runTest {
        val targetFolderId = 300L
        val selectedNotes = listOf(
            getNote(id = 10L).copy(folderId = null),
            getNote(id = 11L).copy(folderId = null)
        )
        val noteIds = listOf(10L, 11L)

        coEvery { noteRepository.updateNotesFolder(noteIds, targetFolderId) } returns true
        coEvery { folderRepository.updateFolderTimestamp(listOf(targetFolderId), getUpdatedTimestamp()) } returns true

        val result = useCase(selectedNotes, targetFolderId)

        assertEquals(MoveNotesToFolderUseCase.Result.SUCCESS, result)
        coVerifyOrder {
            noteRepository.updateNotesFolder(noteIds, targetFolderId)
            folderRepository.updateFolderTimestamp(listOf(targetFolderId), getUpdatedTimestamp())
        }
    }

    @Test
    fun `should move notes to no folder and update all source folders timestamps`() = runTest {
        val selectedNotes = listOf(
            getNote(id = 20L).copy(folderId = 100L),
            getNote(id = 21L).copy(folderId = 200L),
            getNote(id = 22L).copy(folderId = 100L)
        )
        val noteIds = listOf(20L, 21L, 22L)
        // Contract: affected folders are passed in source first-seen order, then target (if any).
        val affectedFolderIdsInSourceOrderThenTarget = listOf(100L, 200L)

        coEvery { noteRepository.updateNotesFolder(noteIds, null) } returns true
        coEvery {
            folderRepository.updateFolderTimestamp(
                affectedFolderIdsInSourceOrderThenTarget,
                getUpdatedTimestamp()
            )
        } returns true

        val result = useCase(selectedNotes, null)

        assertEquals(MoveNotesToFolderUseCase.Result.SUCCESS, result)
        coVerifyOrder {
            noteRepository.updateNotesFolder(noteIds, null)
            folderRepository.updateFolderTimestamp(
                affectedFolderIdsInSourceOrderThenTarget,
                getUpdatedTimestamp()
            )
        }
    }

    @Test
    fun `should move notes from mixed sources and update source plus destination timestamps`() = runTest {
        val targetFolderId = 500L
        val selectedNotes = listOf(
            getNote(id = 30L).copy(folderId = 100L),
            getNote(id = 31L).copy(folderId = 200L),
            getNote(id = 32L).copy(folderId = null),
            getNote(id = 33L).copy(folderId = 100L)
        )
        val noteIds = listOf(30L, 31L, 32L, 33L)
        val affectedFolderIdsInSourceOrderThenTarget = listOf(100L, 200L, 500L)

        coEvery { noteRepository.updateNotesFolder(noteIds, targetFolderId) } returns true
        coEvery {
            folderRepository.updateFolderTimestamp(
                affectedFolderIdsInSourceOrderThenTarget,
                getUpdatedTimestamp()
            )
        } returns true

        val result = useCase(selectedNotes, targetFolderId)

        assertEquals(MoveNotesToFolderUseCase.Result.SUCCESS, result)
        coVerifyOrder {
            noteRepository.updateNotesFolder(noteIds, targetFolderId)
            folderRepository.updateFolderTimestamp(
                affectedFolderIdsInSourceOrderThenTarget,
                getUpdatedTimestamp()
            )
        }
    }

    @Test
    fun `should ignore notes already in target folder in mixed selection`() = runTest {
        val targetFolderId = 500L
        val selectedNotes = listOf(
            getNote(id = 60L).copy(folderId = targetFolderId), // already in target
            getNote(id = 61L).copy(folderId = 200L), // should move
            getNote(id = 62L).copy(folderId = null) // should move
        )
        val noteIdsToMove = listOf(61L, 62L)
        val affectedFolderIdsInSourceOrderThenTarget = listOf(200L, 500L)

        coEvery { noteRepository.updateNotesFolder(noteIdsToMove, targetFolderId) } returns true
        coEvery {
            folderRepository.updateFolderTimestamp(
                affectedFolderIdsInSourceOrderThenTarget,
                getUpdatedTimestamp()
            )
        } returns true

        val result = useCase(selectedNotes, targetFolderId)

        assertEquals(MoveNotesToFolderUseCase.Result.SUCCESS, result)
        coVerifyOrder {
            noteRepository.updateNotesFolder(noteIdsToMove, targetFolderId)
            folderRepository.updateFolderTimestamp(
                affectedFolderIdsInSourceOrderThenTarget,
                getUpdatedTimestamp()
            )
        }
        coVerify(exactly = 0) { noteRepository.updateNotesFolder(listOf(60L, 61L, 62L), targetFolderId) }
    }

    @Test
    fun `should return FAILURE when notes folder update fails`() = runTest {
        val targetFolderId = 700L
        val selectedNotes = listOf(
            getNote(id = 40L).copy(folderId = 111L),
            getNote(id = 41L).copy(folderId = null)
        )
        val noteIds = listOf(40L, 41L)

        coEvery { noteRepository.updateNotesFolder(noteIds, targetFolderId) } returns false

        val result = useCase(selectedNotes, targetFolderId)

        assertEquals(MoveNotesToFolderUseCase.Result.FAILURE, result)
        coVerify(exactly = 1) { noteRepository.updateNotesFolder(noteIds, targetFolderId) }
        coVerify(exactly = 0) { folderRepository.updateFolderTimestamp(any<List<Long>>(), any<String>()) }
    }

    @Test
    fun `should return FAILURE when folder timestamp update fails`() = runTest {
        val targetFolderId = 801L
        val selectedNotes = listOf(
            getNote(id = 50L).copy(folderId = 900L),
            getNote(id = 51L).copy(folderId = null)
        )
        val noteIds = listOf(50L, 51L)
        val affectedFolderIdsInSourceOrderThenTarget = listOf(900L, 801L)

        coEvery { noteRepository.updateNotesFolder(noteIds, targetFolderId) } returns true
        coEvery {
            folderRepository.updateFolderTimestamp(
                affectedFolderIdsInSourceOrderThenTarget,
                getUpdatedTimestamp()
            )
        } returns false

        val result = useCase(selectedNotes, targetFolderId)

        assertEquals(MoveNotesToFolderUseCase.Result.FAILURE, result)
        coVerifyOrder {
            noteRepository.updateNotesFolder(noteIds, targetFolderId)
            folderRepository.updateFolderTimestamp(
                affectedFolderIdsInSourceOrderThenTarget,
                getUpdatedTimestamp()
            )
        }
    }
}
