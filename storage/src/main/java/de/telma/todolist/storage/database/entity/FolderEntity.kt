package de.telma.todolist.storage.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folders")
data class FolderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val lastUpdatedTimestamp: String
)

