package de.telma.todolist.data.database

import androidx.room.TypeConverter
import de.telma.todolist.data.model.NoteStatus
import de.telma.todolist.data.model.NoteTaskStatus

class NoteStatusConverter {
    @TypeConverter
    fun fromNoteStatus(status: NoteStatus?): String? {
        return status?.statusValue
    }

    @TypeConverter
    fun toNoteStatus(statusString: String?): NoteStatus? {
        return statusString?.let { value ->
            NoteStatus.entries.find { it.statusValue == value }
        }
    }
}

class NoteTaskStatusConverter {
    @TypeConverter
    fun fromNoteTaskStatus(status: NoteTaskStatus?): String? {
        return status?.statusValue
    }

    @TypeConverter
    fun toNoteTaskStatus(statusString: String?): NoteTaskStatus? {
        return statusString?.let { value ->
            NoteTaskStatus.entries.find { it.statusValue == value }
        }
    }
}