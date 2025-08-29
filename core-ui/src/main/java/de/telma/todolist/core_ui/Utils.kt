package de.telma.todolist.core_ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.ranges.contains

@Composable
fun getLastUpdatedText(timestamp: String): String {
    val currentTime = LocalDateTime.now()
    val lastUpdatedTime = LocalDateTime.parse(timestamp)
    val difference = Duration.between(lastUpdatedTime, currentTime)

    when(difference.toMinutes()) {
        in 0..1 -> return stringResource(R.string.just_now)
        in 1..60 -> return stringResource(R.string.minutes_ago, difference.toMinutes())
        in 61..1440 -> return stringResource(R.string.hours_ago, difference.toHours())
        in 1441..10080 -> return stringResource(R.string.days_ago, difference.toDays())
        else -> {
            val pattern = stringResource(R.string.date_format_long)
            val formatter = DateTimeFormatter.ofPattern(pattern)
            return lastUpdatedTime.format(formatter)
        }
    }
}
