package de.telma.todolist.core_ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.telma.todolist.core_ui.theme.AppIcons
import de.telma.todolist.core_ui.theme.TodoListTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import de.telma.todolist.core_ui.R

@Composable
fun BasicDialog(
    title: String,
    text: String,
    confirmText: String,
    dismissText: String = "",
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    AlertDialog(
        title = { Text(text = title) },
        text = { Text(text = text) },
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(text = confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(
                    dismissText.ifBlank { stringResource(R.string.basic_dialog_dismiss_default) }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
fun InputDialog(
    title: String,
    inputLabel: String,
    confirmText: String,
    dismissText: String = "",
    onConfirm: (String) -> Unit = {},
    onDismiss: () -> Unit = {},
    validation: (String) -> InputError? = { null }
) {
    var input by remember { mutableStateOf("") }
    val error: InputError? by remember {
        derivedStateOf {
            if (input.isNotBlank())
                validation.invoke(input)
            else
                null
        }
    }

    AlertDialog(
        title = { Text(text = title) },
        text = {
            Surface(modifier = Modifier.wrapContentSize()) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = input,
                    onValueChange = { input = it },
                    label = { Text(text = inputLabel) },
                    singleLine = true,
                    isError = error != null,
                    trailingIcon = {
                        IconButton(onClick = { input = "" }) {
                            Icon(
                                imageVector = AppIcons.cancel,
                                contentDescription = "Clear input"
                            )
                        }
                    },
                    supportingText = {
                        if (error != null) {
                            Text(error!!.toString())
                        }
                    }
                )
            }
        },
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                enabled = (error == null && input.isNotBlank()),
                onClick = { onConfirm(input) }
            ) {
                Text(text = confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(
                    dismissText.ifBlank { stringResource(R.string.input_dialog_dismiss_default) }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
@Preview(showBackground = true)
private fun BasicDialog_Preview() {
    TodoListTheme {
        Surface(modifier = Modifier.wrapContentSize()) {
            BasicDialog(
                title = "Title",
                text = "Text",
                confirmText = "Confirm"
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun InputDialog_Preview() {
    TodoListTheme {
        Surface(modifier = Modifier.wrapContentSize()) {
            InputDialog(
                title = "Title",
                inputLabel = "Placeholder",
                confirmText = "Confirm"
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun DialogsPlayground_Preview() {
    TodoListTheme {
        Surface(modifier = Modifier.fillMaxSize()) {

            var showBasicDialog by remember { mutableStateOf(false) }
            var showInputDialog by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { showBasicDialog = true }) {
                    Text("Show Basic Dialog")
                }
                Button(onClick = { showInputDialog = true }) {
                    Text("Show Input Dialog")
                }
            }

            if (showBasicDialog) {
                BasicDialog(
                    title = "Basic Dialog",
                    text = "This is a basic dialog",
                    confirmText = "Confirm",
                    dismissText = "Cancel",
                    onConfirm = { showBasicDialog = false },
                    onDismiss = { showBasicDialog = false }
                )
            }

            if (showInputDialog) {
                InputDialog(
                    title = "Input Dialog",
                    inputLabel = "Enter input",
                    confirmText = "Confirm",
                    dismissText = "Cancel",
                    onConfirm = { showInputDialog = false },
                    onDismiss = { showInputDialog = false },
                    validation = { input ->
                        when {
                            input.length < 10 -> InputError("Input is too short")
                            else -> null
                        }
                    }
                )
            }
        }
    }
}

data class InputError(val message: String) {
    override fun toString(): String = message
}