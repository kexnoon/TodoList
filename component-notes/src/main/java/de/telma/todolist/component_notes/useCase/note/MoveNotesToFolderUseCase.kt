package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.repository.FolderRepository
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.utils.getTimestamp
import java.time.Clock
import java.time.LocalDateTime

class MoveNotesToFolderUseCase(
    private val noteRepository: NoteRepository,
    private val folderRepository: FolderRepository,
    private val clock: Clock
) {
    sealed interface Result {
        data object SUCCESS : Result
        data object FAILURE : Result
    }

    suspend operator fun invoke(
        selectedNotes: List<Note>,
        targetFolderId: Long?
    ): Result {
        if (selectedNotes.isEmpty()) {
            return Result.SUCCESS
        }

        val notesToMove = selectedNotes.filter { note -> note.folderId != targetFolderId }
        if (notesToMove.isEmpty()) {
            return Result.SUCCESS
        }

        val noteIds = notesToMove.map { note -> note.id }
        val areNotesUpdated = noteRepository.updateNotesFolder(noteIds, targetFolderId)
        if (!areNotesUpdated) {
            return Result.FAILURE
        }

        val affectedFolderIds = linkedSetOf<Long>()
        notesToMove
            .mapNotNull { note -> note.folderId }
            .forEach { folderId -> affectedFolderIds.add(folderId) }
        targetFolderId?.let { folderId -> affectedFolderIds.add(folderId) }

        if (affectedFolderIds.isEmpty()) {
            return Result.SUCCESS
        }

        val timestamp = getTimestamp(LocalDateTime.now(clock))
        val areFolderTimestampsUpdated = folderRepository.updateFolderTimestamp(
            folderIds = affectedFolderIds.toList(),
            timestamp = timestamp
        )
        if (!areFolderTimestampsUpdated) {
            return Result.FAILURE
        }

        return Result.SUCCESS
    }
}
