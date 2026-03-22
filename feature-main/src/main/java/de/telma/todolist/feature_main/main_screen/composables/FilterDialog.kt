package de.telma.todolist.feature_main.main_screen.composables

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.input.pointer.pointerInput
import de.telma.todolist.component_notes.model.Filters
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.SearchModel
import de.telma.todolist.component_notes.utils.getTimestamp
import de.telma.todolist.component_notes.utils.timestampFormat
import de.telma.todolist.core_ui.getReadableTimestamp
import de.telma.todolist.core_ui.theme.AppIcons
import de.telma.todolist.feature_main.R
import de.telma.todolist.core_ui.theme.TodoListTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FilterDialog(
    searchModel: SearchModel,
    onConfirm: (SearchModel) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf(searchModel.query ?: "") }
    var createdFrom by remember { mutableStateOf(searchModel.filters.createdFrom) }
    var createdTo by remember { mutableStateOf(searchModel.filters.createdTo) }
    var updatedFrom by remember { mutableStateOf(searchModel.filters.updatedFrom) }
    var updatedTo by remember { mutableStateOf(searchModel.filters.updatedTo) }
    var status by remember { mutableStateOf<NoteStatus?>(searchModel.filters.status) }
    var statusExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val formatter = remember { DateTimeFormatter.ofPattern(timestampFormat) }

    fun pickDateTime(onPicked: (String) -> Unit) {
        val now = LocalDateTime.now()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        val dateTime = LocalDateTime.of(
                            LocalDate.of(year, month + 1, dayOfMonth),
                            LocalTime.of(hourOfDay, minute)
                        )
                        onPicked(dateTime.format(formatter))
                    },
                    now.hour,
                    now.minute,
                    true
                ).show()
            },
            now.year,
            now.monthValue - 1,
            now.dayOfMonth
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.filters_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = query,
                    onValueChange = { query = it },
                    label = { Text(stringResource(R.string.filters_query_label)) },
                )
                DateField(
                    label = stringResource(R.string.filters_created_from),
                    value =  createdFrom?.let { getReadableTimestamp(it) } ?: "",
                    onPick = { pickDateTime { createdFrom = it } },
                    onClear = { createdFrom = null }
                )
                DateField(
                    label = stringResource(R.string.filters_created_to),
                    value =  createdTo?.let { getReadableTimestamp(it) } ?: "",
                    onPick = { pickDateTime { createdTo = it } },
                    onClear = { createdTo = null }
                )
                DateField(
                    label = stringResource(R.string.filters_updated_from),
                    value = updatedFrom?.let { getReadableTimestamp(it) } ?: "",
                    onPick = { pickDateTime { updatedFrom = it } },
                    onClear = { updatedFrom = null }
                )
                DateField(
                    label = stringResource(R.string.filters_updated_to),
                    value =  updatedTo?.let { getReadableTimestamp(it) } ?: "",
                    onPick = { pickDateTime { updatedTo = it } },
                    onClear = { updatedTo = null }
                )
                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded }
                ) {
                    TextField(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        readOnly = true,
                        value  = when (status) {
                            NoteStatus.IN_PROGRESS -> stringResource(R.string.filters_status_in_progress)
                            NoteStatus.COMPLETE -> stringResource(R.string.filters_status_completed)
                            else -> stringResource(R.string.filters_status_any)
                        },
                        onValueChange = {},
                        label = { Text(stringResource(R.string.filters_status_label)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                        colors = TextFieldDefaults.colors()
                    )
                    DropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.filters_status_any)) },
                            onClick = { status = null; statusExpanded = false }
                        )
                        NoteStatus.entries.forEach { item ->
                            DropdownMenuItem(
                                text = { when (item) {
                                    NoteStatus.IN_PROGRESS -> Text(stringResource(R.string.filters_status_in_progress))
                                    NoteStatus.COMPLETE -> Text(stringResource(R.string.filters_status_completed))
                                } },
                                onClick = { status = item; statusExpanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                    onConfirm(
                        SearchModel(
                            query = query,
                            filters = Filters(
                                createdFrom = createdFrom,
                                createdTo = createdTo,
                                updatedFrom = updatedFrom,
                                updatedTo = updatedTo,
                                status = status
                            )
                        )
                    )
                }
            ) {
                Text(stringResource(R.string.filters_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.filters_dismiss))
            }
        }
    )
}

@Composable
private fun DateField(
    label: String,
    value: String?,
    onPick: () -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        value = value ?: "",
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            onPick.invoke()
                        }
                    }
                }
            },
        trailingIcon = {
            IconButton(onClick = onClear) {
                Icon(imageVector = AppIcons.cancel, contentDescription = null)
            }
        }
    )
}

@Preview
@Composable
private fun FilterDialog_Preview() {
    TodoListTheme {
        FilterDialog(
            searchModel = SearchModel(
                query = "lorem ipsum",
                filters = Filters(
                    createdFrom = "2023-03-17T00:10Z",
                    createdTo = "2023-03-18T00:10Z",
                    updatedFrom = "2023-03-19T00:10Z",
                    updatedTo = "2023-03-20T00:10Z",
                    status = NoteStatus.IN_PROGRESS
                )
            ),
            onConfirm = {},
            onDismiss = {}
        )
    }
}


