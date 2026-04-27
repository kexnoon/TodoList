package de.telma.todolist.core_ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import de.telma.todolist.core_ui.theme.AppIcons
import de.telma.todolist.core_ui.theme.TodoListTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FilterChip(
    modifier: Modifier = Modifier,
    model: FilterChipModel
) {
    val shape = RoundedCornerShape(100.dp)
    val containerColor = if (model.selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = if (model.selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val borderColor = if (model.selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.72f)
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    Box(
        modifier = modifier
            .combinedClickable(
                onClick = model.onClick,
                onLongClick = model.onLongClick
            )
    ) {
        Surface(
            shape = shape,
            color = containerColor,
            contentColor = contentColor,
            border = androidx.compose.foundation.BorderStroke(width = 1.dp, color = borderColor)
        ) {
            Row(
                modifier = Modifier
                    .defaultMinSize(minHeight = 32.dp)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (model.icon != null) {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        imageVector = model.icon,
                        contentDescription = model.iconContentDescription
                    )
                }
                Text(
                    text = model.text,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

data class FilterChipModel(
    val text: String,
    val selected: Boolean,
    val icon: ImageVector? = null,
    val iconContentDescription: String? = null,
    val onClick: () -> Unit = {},
    val onLongClick: (() -> Unit)? = null
)

@Preview(showBackground = true)
@Composable
private fun FilterChip_Default_Preview() {
    TodoListTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val workChipModel = FilterChipModel(
                    text = "Work",
                    selected = false
                )
                val allNotesChipModel = FilterChipModel(
                    text = "All Notes",
                    selected = true
                )
                FilterChip(model = workChipModel)
                FilterChip(model = allNotesChipModel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FilterChip_WithIcon_Preview() {
    TodoListTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val newFolderModel = FilterChipModel(
                    text = "New Folder",
                    selected = false,
                    icon = AppIcons.add,
                    iconContentDescription = "Add folder"
                )
                val renameModel = FilterChipModel(
                    text = "Rename",
                    selected = true,
                    icon = AppIcons.edit,
                    iconContentDescription = "Rename"
                )
                FilterChip(model = newFolderModel)
                FilterChip(model = renameModel)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FilterChip_LongClick_Preview() {
    TodoListTheme {
        Surface {
            Box(modifier = Modifier.padding(16.dp)) {
                val longClickModel = FilterChipModel(
                    text = "Long Press Enabled",
                    selected = false,
                    icon = AppIcons.folder,
                    iconContentDescription = "Folder",
                    onClick = {},
                    onLongClick = {}
                )
                FilterChip(model = longClickModel)
            }
        }
    }
}
