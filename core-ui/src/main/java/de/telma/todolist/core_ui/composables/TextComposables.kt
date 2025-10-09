package de.telma.todolist.core_ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TextHeadlineMedium(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textDecoration: TextDecoration = TextDecoration.None,
    textAlign: TextAlign = TextAlign.Start,
    text: String
) {
    Text(
        modifier = modifier,
        style = MaterialTheme.typography.headlineMedium,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration,
        text = text
    )
}

@Composable
fun TextLabelLarge(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textDecoration: TextDecoration = TextDecoration.None,
    textAlign: TextAlign = TextAlign.Start,
    text: String
){
    Text(
        modifier = modifier,
        style = MaterialTheme.typography.labelLarge,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration,
        text = text
    )
}

@Composable
fun TextLabelMedium(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textDecoration: TextDecoration = TextDecoration.None,
    textAlign: TextAlign = TextAlign.Start,
    text: String
) {
    Text(
        modifier = modifier,
        style = MaterialTheme.typography.labelMedium,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration,
        text = text
    )
}

@Composable
fun TextBodyLarge(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textDecoration: TextDecoration = TextDecoration.None,
    textAlign: TextAlign = TextAlign.Start,
    text: String
) {
    Text(
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge,
        color = color,
        textDecoration = textDecoration,
        textAlign = textAlign,
        text = text
    )
}

@Composable
fun TextBodyMedium(
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textDecoration: TextDecoration = TextDecoration.None,
    textAlign: TextAlign = TextAlign.Start,
    text: String
) {
    Text(
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        textAlign = textAlign,
        textDecoration = textDecoration,
        text = text
    )
}

@Composable
@Preview(showBackground = true)
private fun TextComposables_Preview() {
    Column {
        TextHeadlineMedium(text ="TextHeadlineMedium")
        TextBodyLarge(text = "TextBodyLarge")
        TextBodyMedium(text = "TextBodyMedium")
        TextLabelLarge(text = "TextLabelLarge")
        TextLabelMedium(text = "TextLabelMedium")
    }
}