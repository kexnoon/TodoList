package de.telma.todolist.storage.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = FolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["folderId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["folderId"])]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(defaultValue = "Untitled")
    val title: String = "Untitled",
    val status: String,
    val folderId: Long? = null,
    val createdTimestamp: String,
    val lastUpdatedTimestamp: String,
)
