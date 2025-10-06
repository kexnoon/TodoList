package de.telma.todolist.feature_main.note_screen.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import de.telma.todolist.core_ui.composables.TextHeadlineMedium
import de.telma.todolist.core_ui.composables.TextLabelLarge
import de.telma.todolist.core_ui.theme.AppColors
import de.telma.todolist.core_ui.theme.AppIcons
import de.telma.todolist.core_ui.theme.TodoListTheme
import de.telma.todolist.feature_main.R
import de.telma.todolist.feature_main.note_screen.models.NoteScreenAppBarModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteScreenAppBar(
    modifier: Modifier = Modifier,
    model: NoteScreenAppBarModel,
    onBackPressed: () -> Unit = {},
    onRenamePressed: (noteId: Long) -> Unit = {},
    onDeletePressed: (noteId: Long) -> Unit = {}
) {
    val statusString = if (model.isComplete)
        stringResource(R.string.note_screen_app_bar_status_in_progress)
    else
        stringResource(R.string.note_screen_app_bar_status_complete)

    val statusColor = if (model.isComplete) AppColors.statusComplete else AppColors.statusInProgress

    LargeTopAppBar(
        modifier = modifier.wrapContentHeight(),
        title = {
            Column(modifier = Modifier.wrapContentHeight()) {
                TextHeadlineMedium(text = model.title)
                TextLabelLarge(
                    color = statusColor,
                    text = "Status: $statusString"
                )
            } },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = AppIcons.back,
                    contentDescription = "Back to Main Screen"
                )
            }
        },
        actions = {
            IconButton(onClick = { onRenamePressed(model.noteId) }) {
                Icon(
                    imageVector = AppIcons.edit,
                    contentDescription = "Rename Note"
                )
            }
            IconButton(onClick = { onDeletePressed(model.noteId) }) {
                Icon(
                    imageVector = AppIcons.delete,
                    contentDescription = "Delete Note"
                )
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
fun NoteScreenAppBar_InProgress_Preview() {
    TodoListTheme {
        Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
            val model = NoteScreenAppBarModel(
                noteId = 0L,
                title = "Note in progress",
                isComplete = false
            )
            NoteScreenAppBar(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                model = model
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun NoteScreenAppBar_Complete_Preview() {
    TodoListTheme {
        Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
            val model = NoteScreenAppBarModel(
                noteId = 0L,
                title = "Completed note",
                isComplete = true
            )
            NoteScreenAppBar(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                model = model
            )
        }
    }
}