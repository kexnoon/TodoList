package de.telma.todolist.component_notes.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val timestampFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'"

fun getTimestamp(currentDateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern(timestampFormat)
    return currentDateTime.format(formatter)
}