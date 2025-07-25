@file:OptIn(ExperimentalMaterial3Api::class)

package de.tillhub.inputengine.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun Toolbar(
    title: String,
    onBackClick: () -> Unit,
) {
    Column {
        TopAppBar(
            colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
            title = {
                Text(
                    modifier = Modifier.semantics { contentDescription = "Toolbar title" },
                    style = MaterialTheme.typography.titleLarge,
                    text = title,
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.semantics { contentDescription = "Toolbar back button" },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "close",
                    )
                }
            },
        )
    }
}

@Preview
@Composable internal fun ToolbarPreview() {
    Toolbar("Title example") {}
}
