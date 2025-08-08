package de.telma.todolist.core_ui

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.ranges.contains

fun getLastUpdatedText(timestamp: String): String {
    val currentTime = LocalDateTime.now()
    val lastUpdatedTime = LocalDateTime.parse(timestamp)
    val difference = Duration.between(lastUpdatedTime, currentTime)

    when(difference.toMinutes()) {
        in 0..1 -> return "Just now"
        in 1..60 -> return "${difference.toMinutes()} minutes ago"
        in 61..1440 -> return "${difference.toHours()} hours ago"
        in 1441..10080 -> return "${difference.toDays()} days ago"
        else -> {
            val pattern = "MMMM d, yyyy"
            val formatter = DateTimeFormatter.ofPattern(pattern)
            return lastUpdatedTime.format(formatter)
        }
    }

}
