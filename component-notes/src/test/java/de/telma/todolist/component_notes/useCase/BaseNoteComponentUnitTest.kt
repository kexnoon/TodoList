package de.telma.todolist.component_notes.useCase

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.utils.timestampFormat
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

open class BaseNoteComponentUnitTest {

    fun getTaskComplete(
        id: Long = (0L..10000L).random(),
        title: String? = null
    ) = NoteTask(
        id = id,
        title = title ?: "Task $id",
        status = NoteTaskStatus.COMPLETE
    )

    fun getTaskInProgress(
        id: Long = (0L..10000L).random(),
        title: String? = null
    ) = NoteTask(
        id = id,
        title = title ?: "Task $id",
        status = NoteTaskStatus.IN_PROGRESS
    )

    fun getNote(
        id: Long = (0L..10000L).random(),
        title: String? = null,
        status: NoteStatus = NoteStatus.IN_PROGRESS,
        timestamp: String = getDefaultTimestamp(),
        tasks: List<NoteTask> = listOf()
    ) = Note(
        id = id,
        title = title ?: "Note $id",
        status = status,
        tasksList = tasks,
        lastUpdatedTimestamp = timestamp
    )

    fun getDefaultTimestamp(): String = "2023-01-01T10:00:00Z"

    fun getUpdatedTimestamp(): String = "2023-01-01T12:30:00Z"

    fun getClockForTest(timestampString: String = getDefaultTimestamp()): Clock {
        val formatter = DateTimeFormatter.ofPattern(timestampFormat)
        val localDateTime = LocalDateTime.parse(timestampString, formatter)
        val instant = localDateTime.toInstant(ZoneOffset.UTC)
        return Clock.fixed(instant, ZoneOffset.UTC)
    }

}