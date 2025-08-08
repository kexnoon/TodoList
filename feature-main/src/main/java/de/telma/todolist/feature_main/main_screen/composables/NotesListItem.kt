package de.telma.todolist.feature_main.main_screen.composables

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import de.telma.todolist.core_ui.composables.TextBodyLarge
import de.telma.todolist.core_ui.composables.TextBodyMedium
import de.telma.todolist.core_ui.composables.TextLabelMedium
import de.telma.todolist.feature_main.main_screen.model.NotesListItemModel
import de.telma.todolist.feature_main.main_screen.model.NotesListItemState
import de.telma.todolist.core_ui.getLastUpdatedText
import de.telma.todolist.core_ui.theme.AppColors
import de.telma.todolist.core_ui.theme.AppIcons

@Composable
fun NotesListItem(
    modifier: Modifier = Modifier,
    model: NotesListItemModel,
    isSelectionMode: Boolean,
    onClick: (Long) -> Unit = {},
    onLongClick: (Long) -> Unit = {},
    onCheckedChange: (Long, Boolean) -> Unit = { _, _ -> }
) {
    var isChecked by rememberSaveable { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .combinedClickable(
                onClick = {
                    if (isSelectionMode) {
                        isChecked = !isChecked
                        onCheckedChange(model.id, isChecked)
                    } else {
                        onClick(model.id)
                    }
                },
                onLongClick = {
                    if (!isSelectionMode) {
                        onLongClick.invoke(model.id)
                        isChecked = true
                    }
                }
            )
    ) {

        Row(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
            if (isSelectionMode) {
                Checkbox(
                    modifier = Modifier.padding(end = 16.dp),
                    checked = isChecked,
                    onCheckedChange = {
                        isChecked = !isChecked
                        onCheckedChange(model.id, isChecked)
                    }
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                TextLabelMedium(text = getLastUpdatedText(model.lastUpdatedTimestamp))
                TextBodyLarge(text = model.title)
                if (model.status == NotesListItemState.IN_PROGRESS)
                    TextBodyMedium(
                        color = AppColors.statusInProgress,
                        text = "In Progress: ${model.numberOfTasks} Tasks"
                    )
                else
                    TextBodyMedium(
                        color = AppColors.statusComplete,
                        text = "Completed"
                    )
            }

            Icon(painter = rememberVectorPainter(AppIcons.rightArrow), contentDescription = "")
        }
    }
}

@Composable
@Preview
fun NotesListItem_InProgress_Preview() {
    val model = NotesListItemModel(
        id = 0L,
        title = "Test Note",
        status = NotesListItemState.IN_PROGRESS,
        numberOfTasks = 7,
        lastUpdatedTimestamp = "2023-01-01T10:00:00"
    )
    NotesListItem(model = model, isSelectionMode = false)
}

@Composable
@Preview
fun NotesListItem_Complete_Preview() {
    val model = NotesListItemModel(
        id = 0L,
        title = "Test Note",
        status = NotesListItemState.COMPLETE,
        numberOfTasks = 7,
        lastUpdatedTimestamp = "2023-01-01T10:00:00"
    )
    NotesListItem(model = model, isSelectionMode = false)
}

@Composable
@Preview
fun NotesListItem_SelectionMode_Preview() {
    val model = NotesListItemModel(
        id = 0L,
        title = "Test Note",
        status = NotesListItemState.COMPLETE,
        numberOfTasks = 7,
        lastUpdatedTimestamp = "2023-01-01T10:00:00"
    )

    var isSelectionMode by rememberSaveable { mutableStateOf(false) }

    NotesListItem(
        model = model,
        isSelectionMode = isSelectionMode,
        onLongClick = { if (!isSelectionMode) isSelectionMode = true }
    )
}