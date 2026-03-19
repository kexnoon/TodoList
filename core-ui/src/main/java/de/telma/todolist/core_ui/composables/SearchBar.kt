package de.telma.todolist.core_ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.telma.todolist.core_ui.theme.AppIcons
import de.telma.todolist.core_ui.theme.TodoListTheme

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    input: String,
    state: SearchBarState,
    onInput: (String) -> Unit = { },
    onCancelClicked: () -> Unit = { },
    onFilterClicked: () -> Unit = { }
) {
    val focusManager = LocalFocusManager.current
    TextField(
        modifier = modifier,
        value = input,
        onValueChange = {
            onInput(it)
        },
        singleLine = true,
        placeholder = { Text("Search") },
        leadingIcon = { Icon(AppIcons.search, contentDescription = "") },
        trailingIcon = {
            if (state == SearchBarState.ACTIVE) {
                Row(modifier = Modifier.wrapContentWidth()) {
                    IconButton(onClick = onFilterClicked) { Icon(AppIcons.filter, contentDescription = "") }
                    IconButton(
                        onClick = {
                            onCancelClicked()
                            focusManager.clearFocus()
                        }
                    ) { Icon(AppIcons.cancel, contentDescription = "") }
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            focusedContainerColor = Color(0xFFE9E5EE),   // свой фон
            unfocusedContainerColor = Color(0xFFE9E5EE)
        ),
        shape = RoundedCornerShape(24.dp),
    )

}

@Composable
@Preview(showBackground = true)
private fun SearchBar_Default_Preview() {
    TodoListTheme {
        Surface(modifier = Modifier.wrapContentSize()) {
            val input = remember { mutableStateOf("") }
            SearchBar(input = input.value, state = SearchBarState.DEFAULT)
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun SearchBar_Active_Preview() {
    TodoListTheme {
        Surface(modifier = Modifier.wrapContentSize()) {
            val input = remember { mutableStateOf("") }
            SearchBar(input = input.value, state = SearchBarState.ACTIVE)
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun SearchBar_Playground() {
    TodoListTheme {
        Surface(modifier = Modifier.wrapContentSize()) {
            var text by remember { mutableStateOf("") }
            SearchBar(
                state = if (text.isEmpty()) SearchBarState.DEFAULT else SearchBarState.ACTIVE,
                input = text,
                onInput = { text = it },
                onCancelClicked = { text = "" },
            )
        }
    }
}

enum class SearchBarState { DEFAULT, ACTIVE }
