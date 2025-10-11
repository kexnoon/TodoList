package de.telma.todolist.component_notes.useCase.task

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.repository.TaskRepository
import de.telma.todolist.component_notes.utils.getTimestamp
import java.time.Clock
import java.time.LocalDateTime

class CreateNewTaskUseCase(
    private val taskRepository: TaskRepository,
    private val noteRepository: NoteRepository,
    private val clock: Clock
) {

    suspend operator fun invoke(note: Note, title: String): Boolean {
        var result = false

        if(taskRepository.createNewTask(note.id, title)) {
            result = noteRepository.updateNote(
                note.copy(lastUpdatedTimestamp = getTimestamp(LocalDateTime.now(clock)))
            )
        }

        return result
    }


}