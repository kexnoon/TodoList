package de.telma.todolist.core_ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TextLabelMedium(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    text: String
) {
    Text(
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium,
        color = color,
        text = text
    )
}

@Composable
fun TextBodyLarge(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    text: String
) {
    Text(
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge,
        color = color,
        text = text
    )
}

@Composable
fun TextBodyMedium(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    text: String
) {
    Text(
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        text = text
    )
}

@Composable
@Preview(showBackground = true)
private fun TextComposables_Preview() {
    Column {
        TextBodyLarge(text = "TextBodyLarge")
        TextBodyMedium(text = "TextBodyMedium")
        TextLabelMedium(text = "TextLabelMedium")
    }
}